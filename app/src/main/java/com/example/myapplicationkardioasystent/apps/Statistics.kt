package com.example.myapplicationkardioasystent.apps

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplicationkardioasystent.R
import com.example.myapplicationkardioasystent.cloudFirestore.Measurment
import com.example.myapplicationkardioasystent.recyclerView.MeasurementAdapter
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Aktywność wyświetlająca statystyki wyników zdrowia użytkowika zalogowanego w aplikacji.
 * Umożliwia użytkownikowi powrót do głównego widoku aplikacji.
 */
class Statistics : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val patientMeasurementList = mutableListOf<Measurment>()
    private lateinit var adapter: MeasurementAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.statistics)
        setData()

        // Inicjalizacja RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        // Ustawienie adaptera dla RecyclerView, który będzie obsługiwał listę pomiarów pacjenta
        adapter = MeasurementAdapter(patientMeasurementList, intent)
        recyclerView.adapter = adapter

        // Ustawienie menedżera układu dla RecyclerView, w tym przypadku używamy LinearLayoutManager
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Obsługa przycisku "Powrót"
        val returnFromStatisticsButton = findViewById<Button>(R.id.returnFromStatisticsButton)
        returnFromStatisticsButton.setOnClickListener {
            // Powrót do głównego okna aplikacji
            openMainActivity()
        }
    }

    private fun setData() {
        db.collection("measurements")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val bloodPressure = document.getString("bloodPressure")
                    val date = document.getString("date")
                    val hour = document.getString("hour")
                    val pulse = document.getString("pulse")
                    val uId = document.getString("userID")
                    val measurement = Measurment(
                        uId.orEmpty(),
                        date.orEmpty(),
                        hour.orEmpty(),
                        bloodPressure.orEmpty(),
                        pulse.orEmpty()
                    )
                    if (uId == intent.getStringExtra("userID")) {
                        patientMeasurementList.add(measurement)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed: $exception", Toast.LENGTH_SHORT).show()
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
