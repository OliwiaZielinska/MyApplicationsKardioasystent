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
import com.google.firebase.firestore.FirebaseFirestore

/**
 *  Główna aktywność aplikacji, wyświetlająca interfejs użytkownika.
 *  Umożliwia nawigację do różnych funkcji aplikacji oraz wyświetlenie mapy z lokalizacjami szpitali w pobliżu.
 */
class MainViewApp : AppCompatActivity(){

    //deklaracja przycisków i innych elementów interfejsu użytkownika
    private lateinit var enterTheMeasurementResultButton: Button
    private lateinit var statisticsButton: Button
    private lateinit var settingsButton: Button
    private lateinit var healthGuideButton: Button
    private lateinit var logOutButton: Button
    private lateinit var calendarView: CalendarView
    private lateinit var helloUserText: TextView
    private lateinit var measurementResultsText: TextView
    private lateinit var mapsButton: Button
    private val db = FirebaseFirestore.getInstance() //inicjalizacja Firestore

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
        mapsButton = findViewById(R.id.mapsButton)

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

        mapsButton.setOnClickListener {
            openActivityMaps(uID.toString()) //otwiera aktywność map
        }

        //obsługa wyboru daty w kalendarzu
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate =
                "${year}-${String.format("%02d", month + 1)}-${String.format("%02d", dayOfMonth)}"
            fetchMeasurementResults(selectedDate)
        }
    }

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
     * Otwiera aktywność map.
     *
     * @param userID ID użytkownika przekazywane do nowej aktywności.
     */
    private fun openActivityMaps(userID: String) {
        val intent = Intent(this, Maps::class.java)
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
