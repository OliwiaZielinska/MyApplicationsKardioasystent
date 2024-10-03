package com.example.myapplicationkardioasystent.apps

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationkardioasystent.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale
/**
 * Klasa Raports odpowiedzialna za wyświetlanie raportu pomiarów tętna użytkownika. Zawiera metody do pobierania danych z Firestore,
 * wyświetlania wykresu oraz obliczania statystyk tętna w ostatnich 30 dniach.
 */
class Raports : AppCompatActivity() {

    private lateinit var lineChart: LineChart
    private lateinit var minPulseTextView: TextView
    private lateinit var maxPulseTextView: TextView
    private lateinit var avgPulseTextView: TextView
    private val db = FirebaseFirestore.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayFormat = SimpleDateFormat("dd.MM", Locale.getDefault())
    /**
     * Metoda wykonywana podczas tworzenia aktywności.
     * Inicjalizuje widoki, pobiera dane użytkownika i dane z Firestore oraz wywołuje metodę do wyświetlania wykresu.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.raports)

        lineChart = findViewById(R.id.lineChart)
        minPulseTextView = findViewById(R.id.minPulseTextView)
        maxPulseTextView = findViewById(R.id.maxPulseTextView)
        avgPulseTextView = findViewById(R.id.avgPulseTextView)
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
     * Metoda do wyświetlania wykresu średnich wartości tętna.
     *
     * @param avgPulsePerDay Mapa z datami jako klucze i średnimi wartościami tętna jako wartości.
     */
    private fun displayChart(avgPulsePerDay: Map<String, Double>) {
        val entries = mutableListOf<Entry>()
        val sortedDates = avgPulsePerDay.keys.sortedBy { dateFormat.parse(it) }

        // Zbieranie wartości pulsu
        val pulseValues = avgPulsePerDay.values.map { it.toFloat() }

        // Obliczanie minimalnej, maksymalnej oraz średniej wartości
        val minPulse = pulseValues.minOrNull() ?: 0f
        val maxPulse = pulseValues.maxOrNull() ?: 0f
        val avgPulse = if (pulseValues.isNotEmpty()) pulseValues.average().toFloat() else 0f

        // Wyświetlanie wartości w TextView
        minPulseTextView.text = "Minimalne HR: %.2f".format(minPulse)
        maxPulseTextView.text = "Maksymalne HR: %.2f".format(maxPulse)
        avgPulseTextView.text = "Średnie HR: %.2f".format(avgPulse)

        sortedDates.forEachIndexed { index, date ->
            val avgPulseForDate = avgPulsePerDay[date] ?: 0.0
            entries.add(Entry(index.toFloat(), avgPulseForDate.toFloat()))
        }

        val lineDataSet = LineDataSet(entries, "Średnie tętno")
        val lineData = LineData(lineDataSet)
        lineChart.data = lineData

        // Ustawienia dla osi X
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index >= 0 && index < sortedDates.size) {
                    displayFormat.format(dateFormat.parse(sortedDates[index])!!)
                } else {
                    ""
                }
            }
        }
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = -30f
        xAxis.setLabelCount(sortedDates.size / 2, true)

        lineChart.setExtraOffsets(10f, 10f, 10f, 20f)

        lineChart.description.text = "Data pomiaru"

        // Ustawienia dla osi Y
        val yAxisLeft = lineChart.axisLeft
        yAxisLeft.axisMinimum = 0f
        yAxisLeft.setDrawGridLines(true)

        lineChart.axisRight.isEnabled = false

        // Ustawienie legendy
        val legend = lineChart.legend
        legend.isEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)

        lineChart.invalidate()
    }
    /**
     * Metoda do otwierania aktywności głównej.
     *
     * @param userID identyfikator użytkownika, który jest przekazywany do głównej aktywności.
     */
    private fun openMainActivity(userID: String) {
        val intent = Intent(this, MainViewApp::class.java)
        intent.putExtra("uID", userID)
        startActivity(intent)
    }
}
