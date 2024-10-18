package com.example.myapplicationkardioasystent.apps

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationkardioasystent.R
import com.example.myapplicationkardioasystent.login.MainActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore

/**
 *  Główna aktywność aplikacji, wyświetlająca interfejs użytkownika.
 *  Umożliwia nawigację do różnych funkcji aplikacji oraz wyświetlenie mapy.
 */
class MainViewApp : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var enterTheMeasurementResultButton: Button
    private lateinit var statisticsButton: Button
    private lateinit var settingsButton: Button
    private lateinit var healthGuideButton: Button
    private lateinit var logOutButton: Button
    private lateinit var calendarView: CalendarView
    private lateinit var helloUserText: TextView
    private lateinit var measurementResultsText: TextView
    private lateinit var mMap: GoogleMap
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_view_app)

        // Odczytanie UID użytkownika
        val uID = intent.getStringExtra("uID")

        // Inicjalizacja widoków
        enterTheMeasurementResultButton = findViewById(R.id.enterTheMeasurementResultButton)
        statisticsButton = findViewById(R.id.statisticsButton)
        settingsButton = findViewById(R.id.settingsButton)
        healthGuideButton = findViewById(R.id.healthGuideButton)
        logOutButton = findViewById(R.id.logOutButton)
        helloUserText = findViewById(R.id.helloUserText)
        calendarView = findViewById(R.id.calendarView)

        // Powitanie użytkownika
        helloUserText.text = "Witaj ${uID}!"

        // Obsługa przycisków
        enterTheMeasurementResultButton.setOnClickListener {
            openActivity(uID.toString())
        }
        statisticsButton.setOnClickListener {
            openActivityStatistics(uID.toString())
        }
        settingsButton.setOnClickListener {
            openActivitySettings(uID.toString())
        }
        healthGuideButton.setOnClickListener {
            openActivityHealthAdvices(uID.toString())
        }
        logOutButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Obsługa kalendarza
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "${year}-${String.format("%02d", month + 1)}-${String.format("%02d", dayOfMonth)}"
            fetchMeasurementResults(selectedDate)
        }

        // Inicjalizacja fragmentu mapy
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Dodanie markera w Warszawie i przesunięcie kamery
        val warsaw = LatLng(52.2297, 21.0122)
        mMap.addMarker(MarkerOptions().position(warsaw).title("Marker in Warsaw"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(warsaw, 10f))
    }

    /**
     * Pobiera wyniki pomiarów dla określonej daty z bazy danych i wyświetla je w oknie dialogowym.
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

                // Wiadomość dla użytkownika w zależności od liczby pomiarów
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
     * Wyświetla dialog z określonym tytułem i treścią.
     */
    private fun showDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK", null)
        builder.show()
    }

    /**
     * Metoda do otwarcia aktywności wprowadzania wyniku pomiaru.
     */
    private fun openActivity(userID: String) {
        val intent = Intent(this, EnterMeasurment::class.java)
        intent.putExtra("userID", userID)
        startActivity(intent)
    }

    /**
     * Metoda do otwarcia aktywności poradnika zdrowia.
     */
    private fun openActivityHealthAdvices(userID: String) {
        val intent = Intent(this, HealthAdvices::class.java)
        intent.putExtra("userID", userID)
        startActivity(intent)
    }

    /**
     * Metoda do otwarcia aktywności ustawień.
     */
    private fun openActivitySettings(userID: String) {
        val intent = Intent(this, Settings::class.java)
        intent.putExtra("userID", userID)
        startActivity(intent)
    }

    /**
     * Metoda do otwarcia aktywności statystyk.
     */
    private fun openActivityStatistics(userID: String) {
        val intent = Intent(this, Statistics::class.java)
        intent.putExtra("userID", userID)
        startActivity(intent)
    }
}
