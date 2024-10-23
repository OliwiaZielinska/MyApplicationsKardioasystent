package com.example.myapplicationkardioasystent.apps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.myapplicationkardioasystent.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONException
import org.json.JSONObject

class Maps : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener  {
    //obiekt mapy Google
    private lateinit var mMap: GoogleMap
    //klient lokalizacji do uzyskiwania ostatniej lokalizacji użytkownika
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    //zmienna przechowująca ostatnią lokalizację użytkownika
    private lateinit var lastLocation: Location

    private lateinit var returnFromMapsButton: Button

    companion object {
        private const val LOCATION_REQUEST_CODE = 1 //kod zapytania o uprawnienia lokalizacji
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.maps)

        //odczytanie UID użytkownika przekazanego w Intent
        val uID = intent.getStringExtra("uID")

        //inicjalizacja fragmentu mapy
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this) //asynchroniczne przygotowanie mapy

        //inicjalizacja klienta lokalizacji
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        returnFromMapsButton =  findViewById<Button>(R.id.returnFromMapsButton)
        val userId = FirebaseAuth.getInstance().currentUser!!.email
        returnFromMapsButton.setOnClickListener {
            openMainActivity(userId.toString())
        }
    }

    /**
     * Metoda wywoływana po przygotowaniu mapy Google.
     * Inicjalizuje mapę i ustawia jej konfigurację.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true //włącza przyciski powiększania na mapie
        mMap.setOnMarkerClickListener(this) //ustawia słuchacza zdarzeń kliknięcia na marker
        setUpMap() //inicjalizacja mapy i lokalizacji
    }
    /**
     * Metoda konfigurująca mapę, ustawiająca uprawnienia i lokalizację użytkownika.
     */
    private fun setUpMap() {
        //sprawdzenie, czy aplikacja ma odpowiednie uprawnienia do lokalizacji
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                Maps.LOCATION_REQUEST_CODE
            )
            return
        }

        mMap.isMyLocationEnabled = true //włączenie warstwy lokalizacji użytkownika

        //pobranie ostatniej lokalizacji użytkownika
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)

                //dodanie markera dla bieżącej lokalizacji użytkownika
                placeMarkerOnMap(currentLatLong)

                //przesunięcie kamery mapy na bieżącą lokalizację
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 13f))

                //wyszukanie pobliskich szpitali
                findNearbyHospitals(currentLatLong)
            }
        }
    }
    /**
     * Pobiera klucz API z pliku AndroidManifest.xml.
     *
     * @return Klucz API jako String lub null, jeśli klucz nie istnieje.
     */
    private fun getApiKeyFromManifest(): String? {
        try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            val bundle = applicationInfo.metaData
            return bundle.getString("com.google.android.geo.API_KEY")
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null
    }
    /**
     * Wyszukuje pobliskie szpitale za pomocą Google Places API i dodaje markery na mapie.
     *
     * @param location Lokalizacja użytkownika jako obiekt LatLng.
     */
    private fun findNearbyHospitals(location: LatLng) {
        val apiKey = getApiKeyFromManifest() //pobranie klucza API z manifestu

        if (apiKey != null) {
            val locationString = "${location.latitude},${location.longitude}"
            val radius = 20000  //promień wyszukiwania w metrach (szpitale w promieniu 20 km)
            val type = "hospital"
            val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$locationString&radius=$radius&type=$type&key=$apiKey"

            //żądanie HTTP do Google Places API
            val request = object : StringRequest(
                Method.GET, url,
                Response.Listener { response ->
                    try {
                        //parsowanie odpowiedzi JSON
                        val jsonObject = JSONObject(response)
                        val results = jsonObject.getJSONArray("results")

                        //iteracja po wynikach i dodanie markerów na mapie
                        for (i in 0 until results.length()) {
                            val place = results.getJSONObject(i)
                            val latLng = place.getJSONObject("geometry")
                                .getJSONObject("location")
                            val lat = latLng.getDouble("lat")
                            val lng = latLng.getDouble("lng")
                            val placeName = place.getString("name")

                            //dodanie markera na mapie dla każdego znalezionego szpitala
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(LatLng(lat, lng))
                                    .title(placeName)  //nazwa szpitala jako tytuł markera
                            )
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace() //obsługa błędów parsowania JSON
                    }
                },
                Response.ErrorListener { error ->
                    //obsługa błędów związanych z żądaniem HTTP
                    Log.e("MapsActivity", "Błąd podczas wyszukiwania: ${error.message}")
                }) {}

            //dodanie żądania do kolejki sieciowej Volley
            Volley.newRequestQueue(this).add(request)
        } else {
            Log.e("MapsActivity", "Brak klucza API w AndroidManifest.xml")
        }
    }
    /**
     * Dodaje marker na mapę w określonej lokalizacji.
     *
     * @param location Lokalizacja, w której ma zostać dodany marker (obiekt LatLng).
     */
    private fun placeMarkerOnMap(location: LatLng) {
        val markerOptions = MarkerOptions().position(location).title("Twoja lokalizacja")
        mMap.addMarker(markerOptions)  //dodaje marker dla bieżącej lokalizacji użytkownika
    }
    /**
     * Obsługuje zdarzenie kliknięcia na marker na mapie.
     *
     * @param marker Marker, na który kliknięto.
     * @return Zawsze zwraca false, aby zezwolić na domyślne zachowanie kliknięcia.
     */
    override fun onMarkerClick(marker: Marker) = false

    /**
     * Otwiera główną aktywność aplikacji.
     */
    private fun openMainActivity(userID : String) {
        val intent = Intent(this, MainViewApp::class.java)
        intent.putExtra("uID", userID)
        startActivity(intent)
    }

}