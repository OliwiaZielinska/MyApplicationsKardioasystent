package com.example.myapplicationkardioasystent.apps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
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
/**
 * Klasa Maps reprezentuje ekran z mapą oraz obsługuje interakcje związane z lokalizacją.
 */
class Maps : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap //obiekt mapy
    private lateinit var fusedLocationClient: FusedLocationProviderClient //klient do uzyskiwania lokalizacji
    private lateinit var lastLocation: Location //ostatnia znaleziona lokalizacja użytkownika


    private lateinit var returnFromMapsButton: Button //przycisk do powrotu do głównej aktywności
    private lateinit var searchEditText: EditText //pole wyszukiwania
    private lateinit var searchButton: Button //przycisk wyszukiwania
    private lateinit var typeSpinner: Spinner //spinner wyboru typu miejsca (kardiolog/apteka/szpital)

    companion object {
        private const val LOCATION_REQUEST_CODE = 1 //kod żądania uprawnień do lokalizacji
    }
    /**
     * Metoda wywoływana przy tworzeniu aktywności.
     * Inicjalizuje widok oraz elementy UI, a także ustawia odpowiednie listener'y.
     *
     * @param savedInstanceState Zapisany stan aktywności
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.maps)
        //pobranie ID użytkownika z przesłanych danych
        val uID = intent.getStringExtra("uID")
        //inicjalizacja fragmentu mapy
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        //inicjalizacja klienta do lokalizacji
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //inicjalizacja elementów UI
        returnFromMapsButton = findViewById(R.id.returnFromMapsButton)
        searchEditText = findViewById(R.id.searchEditText) // Inicjalizacja EditText
        searchButton = findViewById(R.id.searchButton) // Inicjalizacja przycisku
        typeSpinner = findViewById(R.id.typeSpinner) // Inicjalizacja spinnera

        // Inicjalizacja spinnera
        val types = arrayOf("Apteka", "Kardiolog", "Szpital")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = adapter
        //inicjalizacja spinnera z typami miejsc
        val userId = FirebaseAuth.getInstance().currentUser!!.email
        returnFromMapsButton.setOnClickListener {
            openMainActivity(userId.toString()) //powrót do głównej aktywności
        }

        //ustawienie listenera dla przycisku wyszukiwania
        searchButton.setOnClickListener {
            val selectedType = typeSpinner.selectedItem.toString()
            when (selectedType) {
                "Apteka" -> findNearbyPlaces("pharmacy") //wyszukiwanie aptek
                "Kardiolog" -> findNearbyPlaces("cardiologist") //wyszukiwanie kardiologów
                "Szpital" -> findNearbyPlaces("hospital") //wyszukiwanie szpitali
                else -> Toast.makeText(this, "Wybierz typ miejsca", Toast.LENGTH_SHORT).show()
            }
        }
    }
    /**
     * Metoda do wyszukiwania pobliskich miejsc według podanego typu.
     *
     * @param type Typ miejsca do wyszukiwania (np. apteka, kardiolog, szpital)
     */
    private fun findNearbyPlaces(type: String) {
        val apiKey = getApiKeyFromManifest()
        if (apiKey != null) {
            val locationString = "${lastLocation.latitude},${lastLocation.longitude}"
            val radius = 20000 // promień wyszukiwania w metrach (20 km)
            val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$locationString&radius=$radius&type=$type&key=$apiKey"

            // Żądanie HTTP do Google Places API
            val request = object : StringRequest(
                Method.GET, url,
                Response.Listener { response ->
                    try {
                        // Parsowanie odpowiedzi JSON
                        val jsonObject = JSONObject(response)
                        val results = jsonObject.getJSONArray("results")

                        // Usunięcie istniejących markerów
                        mMap.clear()

                        // Iteracja po wynikach i dodanie markerów na mapie
                        for (i in 0 until results.length()) {
                            val place = results.getJSONObject(i)
                            val latLng = place.getJSONObject("geometry").getJSONObject("location")
                            val lat = latLng.getDouble("lat")
                            val lng = latLng.getDouble("lng")
                            val placeName = place.getString("name")

                            // Dodanie markera na mapie dla każdego znalezionego miejsca
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(LatLng(lat, lng))
                                    .title(placeName) // nazwa miejsca jako tytuł markera
                            )
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace() // obsługa błędów parsowania JSON
                    }
                },
                Response.ErrorListener { error ->
                    // obsługa błędów związanych z żądaniem HTTP
                    Log.e("MapsActivity", "Błąd podczas wyszukiwania: ${error.message}")
                }) {}

            // Dodanie żądania do kolejki sieciowej Volley
            Volley.newRequestQueue(this).add(request)
        } else {
            Log.e("MapsActivity", "Brak klucza API w AndroidManifest.xml")
        }
    }
    /**
     * Metoda do pobierania klucza API z manifestu aplikacji.
     *
     * @return Klucz API, lub null, jeśli nie został znaleziony.
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
     * Metoda wywoływana, gdy mapa jest gotowa do użycia.
     * Ustawia przyciski powiększania oraz listener'a dla markerów.
     *
     * @param googleMap Obiekt mapy, który jest gotowy do użycia.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true // włącza przyciski powiększania na mapie
        mMap.setOnMarkerClickListener(this) // ustawia słuchacza zdarzeń kliknięcia na marker
        setUpMap() // inicjalizacja mapy i lokalizacji
    }
    /**
     * Metoda do konfiguracji mapy i lokalizacji użytkownika.
     */
    private fun setUpMap() {
        // Sprawdzenie uprawnień do lokalizacji
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                Maps.LOCATION_REQUEST_CODE //żądanie uprawnień lokalizacji
            )
            return
        }

        mMap.isMyLocationEnabled = true // włączenie warstwy lokalizacji użytkownika

        // pobranie ostatniej lokalizacji użytkownika
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)

                // dodanie markera dla bieżącej lokalizacji użytkownika
                placeMarkerOnMap(currentLatLong)

                // przesunięcie kamery mapy na bieżącą lokalizację
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 13f))
            }
        }
    }
    /**
     * Metoda do dodawania markera na mapie dla podanej lokalizacji.
     *
     * @param location Lokalizacja, dla której ma zostać dodany marker.
     */
    private fun placeMarkerOnMap(location: LatLng) {
        val markerOptions = MarkerOptions().position(location).title("Twoja lokalizacja")
        mMap.addMarker(markerOptions) // dodaje marker dla bieżącej lokalizacji użytkownika
    }
    /**
     * Metoda wywoływana, gdy kliknięto na marker.
     * Aktualnie nie wykonuje żadnej akcji.
     *
     * @param marker Marker, który został kliknięty.
     * @return Zwraca false, aby nie zatrzymywać domyślnej akcji kliknięcia.
     */
    override fun onMarkerClick(marker: Marker) = false
    /**
     * Metoda do otwierania głównej aktywności.
     *
     * @param userID ID użytkownika do przekazania do nowej aktywności.
     */
    private fun openMainActivity(userID: String) {
        val intent = Intent(this, MainViewApp::class.java)
        intent.putExtra("uID", userID)
        startActivity(intent)
    }
}
