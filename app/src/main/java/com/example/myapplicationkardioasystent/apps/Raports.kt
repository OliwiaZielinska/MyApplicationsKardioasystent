package com.example.myapplicationkardioasystent.apps

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationkardioasystent.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Aktywność do wyświetlania raportu z pomiarów tętna.
 */
class Raports : AppCompatActivity() {

    private lateinit var lineChart: LineChart
    private val db = FirebaseFirestore.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.raports)

        lineChart = findViewById(R.id.lineChart)
        val userId = FirebaseAuth.getInstance().currentUser!!.email

        // Pobieranie danych z Firestore
        db.collection("measurements")
            .whereEqualTo("userID", userId)
            .get()
            .addOnSuccessListener { result ->
                val pulseData = mutableMapOf<String, MutableList<Int>>()

                for (document in result) {
                    val date = document.getString("date")
                    val pulse = document.getString("pulse")?.toIntOrNull()

                    if (date != null && pulse != null) {
                        if (!pulseData.containsKey(date)) {
                            pulseData[date] = mutableListOf()
                        }
                        pulseData[date]?.add(pulse)
                    }
                }

                // Obliczanie średniego tętna dla każdego dnia
                val avgPulsePerDay = pulseData.mapValues { (_, pulses) ->
                    pulses.average()
                }

                // Wyświetlanie danych na wykresie
                displayChart(avgPulsePerDay)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Błąd: $exception", Toast.LENGTH_SHORT).show()
            }

        val returnFromRaportsButton = findViewById<Button>(R.id.returnFromRaportsButton)
        returnFromRaportsButton.setOnClickListener {
            openMainActivity(userId.toString())
        }
    }

    /**
     * Metoda do wyświetlania wykresu na podstawie średnich wartości tętna.
     */
    private fun displayChart(avgPulsePerDay: Map<String, Double>) {
        val entries = mutableListOf<Entry>()
        val sortedDates = avgPulsePerDay.keys.sortedBy { dateFormat.parse(it) }

        sortedDates.forEachIndexed { index, date ->
            val avgPulse = avgPulsePerDay[date] ?: 0.0
            entries.add(Entry(index.toFloat(), avgPulse.toFloat()))
        }

        val lineDataSet = LineDataSet(entries, "Średnie tętno")
        val lineData = LineData(lineDataSet)
        lineChart.data = lineData
        lineChart.invalidate() // Odśwież wykres
    }

    /**
     * Otwiera główną aktywność aplikacji.
     */
    private fun openMainActivity(userID : String) {
        val intent = Intent(this, MainViewApp::class.java)
        intent.putExtra("uID", userID)
        startActivity(intent)
    }
}