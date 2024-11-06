package com.example.myapplicationkardioasystent.apps

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationkardioasystent.Chatbot
import com.example.myapplicationkardioasystent.R
import com.example.myapplicationkardioasystent.cloudFirestore.User
import com.example.myapplicationkardioasystent.registation.Gender
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
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

        val sendRaportsButton = findViewById<Button>(R.id.sendRaportsButton)
        sendRaportsButton.setOnClickListener {
            openMainActivity2(userId.toString())
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

        // Ustalanie daty sprzed 30 dni
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        val date30DaysAgo = calendar.time

        // Filtrowanie wyników z ostatnich 30 dni
        val filteredDates = sortedDates.filter { dateFormat.parse(it)?.after(date30DaysAgo) == true }

        // Zbieranie wartości pulsu z przefiltrowanych wyników
        val pulseValues = filteredDates.map { avgPulsePerDay[it]?.toFloat() ?: 0f }

        // Obliczanie minimalnej, maksymalnej oraz średniej wartości
        val minPulse = pulseValues.minOrNull() ?: 0f
        val maxPulse = pulseValues.maxOrNull() ?: 0f
        val avgPulse = if (pulseValues.isNotEmpty()) pulseValues.average().toFloat() else 0f

        // Wyświetlanie wartości w TextView
        minPulseTextView.text = "%.2f".format(minPulse)
        maxPulseTextView.text = "%.2f".format(maxPulse)
        avgPulseTextView.text = "%.2f".format(avgPulse)

        filteredDates.forEachIndexed { index, date ->
            val avgPulseForDate = avgPulsePerDay[date] ?: 0.0
            entries.add(Entry(index.toFloat(), avgPulseForDate.toFloat()))
        }

        // Tworzenie LineDataSet z ciemnoszarym kolorem
        val lineDataSet = LineDataSet(entries, "Średnie tętno")
        lineDataSet.color = Color.DKGRAY  // Ciemny szary kolor
        lineDataSet.setCircleColor(Color.DKGRAY)  // Kolor kółek na punktach
        lineDataSet.lineWidth = 2f
        lineDataSet.circleRadius = 3f
        lineDataSet.setDrawValues(false)

        val lineData = LineData(lineDataSet)
        lineChart.data = lineData

        // Ustawienia dla osi X
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index >= 0 && index < filteredDates.size) {
                    displayFormat.format(dateFormat.parse(filteredDates[index])!!)
                } else {
                    ""
                }
            }
        }
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = -30f
        xAxis.setLabelCount(filteredDates.size / 2, true)

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

        // Pobieramy dane użytkownika
        getUserData { age, gender ->
            if (age != -1 && gender != "unknown") {
                val (lowerLimit, upperLimit) = calculatePulseLimits(age, gender)

                // Tworzenie linii limitów
                val lowerLimitLine = LimitLine(lowerLimit.toFloat(), "Dolna granica normy")
                lowerLimitLine.lineWidth = 2f
                lowerLimitLine.lineColor = Color.RED
                lowerLimitLine.textColor = Color.RED

                val upperLimitLine = LimitLine(upperLimit.toFloat(), "Górna granica normy")
                upperLimitLine.lineWidth = 2f
                upperLimitLine.lineColor = Color.RED
                upperLimitLine.textColor = Color.RED

                // Dodanie linii limitów do osi Y
                val yAxisLeft = lineChart.axisLeft
                yAxisLeft.addLimitLine(lowerLimitLine)
                yAxisLeft.addLimitLine(upperLimitLine)

                // Obliczanie procentu wyników w normie
                val inRangeCount = pulseValues.count { it >= lowerLimit && it <= upperLimit }
                val totalCount = pulseValues.size
                val percentageInRange = if (totalCount > 0) (inRangeCount.toFloat() / totalCount) * 100 else 0f

                // Znalezienie widoku TextView i ustawienie wartości procentowej
                val percentageTextView = findViewById<TextView>(R.id.percentageInRangeTextView)
                percentageTextView.text = "%.2f%%".format(percentageInRange)

                // Odświeżenie wykresu
                lineChart.invalidate()
            }
        }
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

    /**
     * Metoda do otwierania aktywności związanej z chatem i przekazywania wartości tętna.
     *
     * @param userID identyfikator użytkownika, który jest przekazywany do głównej aktywności.
     */
    private fun openMainActivity2(userID: String) {
        val intent = Intent(this, Chatbot::class.java)
        intent.putExtra("uID", userID)
        intent.putExtra("minPulse", minPulseTextView.text.toString())
        intent.putExtra("maxPulse", maxPulseTextView.text.toString())
        intent.putExtra("avgPulse", avgPulseTextView.text.toString())
        startActivity(intent)
    }

    /**
     * Metoda do liczenia limitów tętna na podstawie wieku i płci.
     */
    private fun calculatePulseLimits(age: Int, gender: String): Pair<Int, Int> {
        val pulseRange = when (age) {
            in 18..25 -> if (gender == Gender.MALE.toString()) 62 to 73 else 64 to 80
            in 26..35 -> if (gender == Gender.MALE.toString()) 62 to 73 else 64 to 81
            in 36..45 -> if (gender == Gender.MALE.toString()) 63 to 75 else 65 to 82
            in 46..55 -> if (gender == Gender.MALE.toString()) 64 to 76 else 66 to 83
            in 56..65 -> if (gender == Gender.MALE.toString()) 62 to 75 else 64 to 82
            else -> if (gender == Gender.MALE.toString()) 62 to 73 else 64 to 81
        }
        return pulseRange
    }

    /**
     * Metoda pobierająca dane o użytkowniku
     */
    private fun getUserData(callback: (Int, String) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.email
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val currentUser = document.toObject(User::class.java)
                    if (currentUser != null) {
                        val birthYear = currentUser.yearOfBirth.toIntOrNull()
                        val gender = currentUser.sex
                        if (birthYear != null && gender != null) {
                            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                            val age = currentYear - birthYear
                            callback(age, gender)
                        } else {
                            callback(-1, "unknown")
                        }
                    }
                }
                .addOnFailureListener {
                    callback(-1, "unknown")
                }
        } else {
            callback(-1, "unknown")
        }
    }
}

