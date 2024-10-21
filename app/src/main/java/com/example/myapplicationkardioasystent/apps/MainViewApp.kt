package com.example.myapplicationkardioasystent.apps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.myapplicationkardioasystent.R
import com.example.myapplicationkardioasystent.login.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONException
import org.json.JSONObject

/**
 *  Główna aktywność aplikacji, wyświetlająca interfejs użytkownika.
 *  Umożliwia nawigację do różnych funkcji aplikacji oraz wyświetlenie mapy z lokalizacjami szpitali w pobliżu.
 */
class MainViewApp : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    //obiekt mapy Google
    private lateinit var mMap: GoogleMap
    //klient lokalizacji do uzyskiwania ostatniej lokalizacji użytkownika
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    //zmienna przechowująca ostatnią lokalizację użytkownika
    private lateinit var lastLocation: Location
    //deklaracja przycisków i innych elementów interfejsu użytkownika
    private lateinit var enterTheMeasurementResultButton: Button
    private lateinit var statisticsButton: Button
    private lateinit var settingsButton: Button
    private lateinit var healthGuideButton: Button
    private lateinit var logOutButton: Button
    private lateinit var calendarView: CalendarView
    private lateinit var helloUserText: TextView
    private lateinit var measurementResultsText: TextView
    private val db = FirebaseFirestore.getInstance() //inicjalizacja Firestore

    companion object {
        private const val LOCATION_REQUEST_CODE = 1 //kod zapytania o uprawnienia lokalizacji
    }
    /**
     * Funkcja wywoływana podczas tworzenia aktywności.
     * Ustawia widok oraz inicjalizuje przyciski i inne elementy interfejsu użytkownika.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_view_app)

        //odczytanie UID użytkownika przekazanego w Intent
        val uID = intent.getStringExtra("uID")

        //inicjalizacja widoków interfejsu użytkownika
        enterTheMeasurementResultButton = findViewById(R.id.enterTheMeasurementResultButton)
        statisticsButton = findViewById(R.id.statisticsButton)
        settingsButton = findViewById(R.id.settingsButton)
        healthGuideButton = findViewById(R.id.healthGuideButton)
        logOutButton = findViewById(R.id.logOutButton)
        helloUserText = findViewById(R.id.helloUserText)
        calendarView = findViewById(R.id.calendarView)

        //powitanie użytkownika
        helloUserText.text = "Witaj ${uID}!"

        //przypisanie obsługi zdarzeń do przycisków
        enterTheMeasurementResultButton.setOnClickListener {
            openActivity(uID.toString()) //otwiera aktywność wprowadzania wyników pomiaru
        }
        statisticsButton.setOnClickListener {
            openActivityStatistics(uID.toString())  //otwiera aktywność statystyk
        }
        settingsButton.setOnClickListener {
            openActivitySettings(uID.toString()) //otwiera aktywność ustawień
        }
        healthGuideButton.setOnClickListener {
            openActivityHealthAdvices(uID.toString()) //otwiera aktywność porad zdrowotnych
        }
        logOutButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() //kończy bieżącą aktywność
        }

        //obsługa wyboru daty w kalendarzu
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "${year}-${String.format("%02d", month + 1)}-${String.format("%02d", dayOfMonth)}"
            fetchMeasurementResults(selectedDate)
        }

        //inicjalizacja fragmentu mapy
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this) //asynchroniczne przygotowanie mapy

        //inicjalizacja klienta lokalizacji
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
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
     * Pobiera wyniki pomiarów dla określonej daty z bazy danych Firestore
     * i wyświetla je w oknie dialogowym.
     *
     * @param date Wybrana data w formacie "yyyy-MM-dd", dla której pobierane są pomiary.
     */
    private fun fetchMeasurementResults(date: String) {
        val uID = intent.getStringExtra("uID") ?: return
        db.collection("measurements")
            .whereEqualTo("userID", uID)
            .whereEqualTo("date", date)
            .get()
            .addOnSuccessListener { documents ->
                val results = StringBuilder()
                val measurementCount = documents.size()

                //wiadomość dla użytkownika w zależności od liczby pomiarów
                when {
                    measurementCount >= 3 -> results.append("Jesteś wzorowym użytkownikiem, wymagana ilość pomiarów została osiągnięta :)\n\n")
                    measurementCount == 2 -> results.append("Wykonano 2 z 3 wymaganych pomiarów, brakuje 1 pomiaru.\n\n")
                    measurementCount == 1 -> results.append("Wykonano 1 z 3 wymaganych pomiarów, brakuje 2 pomiarów.\n\n")
                    else -> results.append("Brak pomiarów dla wybranego dnia :(\n\n")
                }

                for (document in documents) {
                    val bloodPressure = document.getString("bloodPressure")
                    val pulse = document.getString("pulse")
                    val hour = document.getString("hour")
                    results.append("Godzina: $hour\nCiśnienie: $bloodPressure\nTętno: $pulse\n\n")
                }

                showDialog("WYNIKI POMIARÓW: ", results.toString())
            }
            .addOnFailureListener { exception ->
                Log.w("MainViewApp", "Error getting documents: ", exception)
                showDialog("Błąd", "Błąd podczas pobierania wyników")
            }
    }

    /**
     * Wyświetla okno dialogowe z podanym tytułem i treścią.
     *
     * @param title Tytuł dialogu.
     * @param message Treść dialogu.
     */
    private fun showDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK", null)
        builder.show()
    }

    /**
     * Otwiera aktywność wprowadzania wyników pomiaru.
     *
     * @param userID ID użytkownika przekazywane do nowej aktywności.
     */
    private fun openActivity(userID: String) {
        val intent = Intent(this, EnterMeasurment::class.java)
        intent.putExtra("userID", userID)
        startActivity(intent)
    }

    /**
     * Otwiera aktywność poradnika zdrowotnego.
     *
     * @param userID ID użytkownika przekazywane do nowej aktywności.
     */
    private fun openActivityHealthAdvices(userID: String) {
        val intent = Intent(this, HealthAdvices::class.java)
        intent.putExtra("userID", userID)
        startActivity(intent)
    }

    /**
     * Otwiera aktywność ustawień.
     *
     * @param userID ID użytkownika przekazywane do nowej aktywności.
     */
    private fun openActivitySettings(userID: String) {
        val intent = Intent(this, Settings::class.java)
        intent.putExtra("userID", userID)
        startActivity(intent)
    }

    /**
     * Otwiera aktywność statystyk.
     *
     * @param userID ID użytkownika przekazywane do nowej aktywności.
     */
    private fun openActivityStatistics(userID: String) {
        val intent = Intent(this, Statistics::class.java)
        intent.putExtra("userID", userID)
        startActivity(intent)
    }
}
