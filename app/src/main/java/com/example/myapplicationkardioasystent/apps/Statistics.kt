package com.example.myapplicationkardioasystent.apps

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplicationkardioasystent.R
import com.example.myapplicationkardioasystent.cloudFirestore.Measurment
import com.example.myapplicationkardioasystent.recyclerView.MeasurementAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Aktywność wyświetlająca statystyki wyników zdrowia użytkowika zalogowanego w aplikacji.
 * Umożliwia użytkownikowi powrót do głównego widoku aplikacji.
 */
class Statistics : AppCompatActivity() {
    private lateinit var measurementAdapter: MeasurementAdapter
    private lateinit var currentUserUid: String // Zmienna przechowująca identyfikator zalogowanego użytkownika

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.statistics)

        // Pobierz identyfikator aktualnie zalogowanego użytkownika
        currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        val recyclerView = findViewById<RecyclerView>(R.id.measurementRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Pobranie danych z Firebase Firestore
        val db = FirebaseFirestore.getInstance()
        db.collection("measurements")
            .whereEqualTo("userID", currentUserUid) // Dodaj filtr, aby pobrać tylko pomiary dla aktualnie zalogowanego użytkownika
            .get()
            .addOnSuccessListener { result ->
                val measurements = mutableListOf<Measurment>()
                for (document in result) {
                    val measurement = document.toObject(Measurment::class.java)
                    measurements.add(measurement)
                }
                // Initialize adapter after fetching data
                measurementAdapter = MeasurementAdapter(measurements, currentUserUid)
                recyclerView.adapter = measurementAdapter
            }
            .addOnFailureListener { exception ->
                // Obsługa błędu
            }

        // Obsługa przycisku "Powrót"
        val returnFromStatisticsButton = findViewById<Button>(R.id.returnFromStatisticsButton)
        returnFromStatisticsButton.setOnClickListener {
            // Powrót do głównego okna aplikacji
            openMainActivity()
        }
    }


    /**
     * Metoda do otwarcia głównego widoku aplikacji.
     */
    private fun openMainActivity() {
        val intent = Intent(this, MainViewApp::class.java)
        startActivity(intent)
    }
}
