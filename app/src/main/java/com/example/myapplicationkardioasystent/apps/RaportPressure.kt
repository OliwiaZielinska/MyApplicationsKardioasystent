package com.example.myapplicationkardioasystent.apps

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
 * Aktywność wyświetlająca raport ciśnienia krwi użytkownika.
 * Umożliwia pobranie danych z Firestore, ich przetworzenie i wyświetlenie na wykresie.
 */
class RaportPressure : AppCompatActivity() {

    private lateinit var lineChart: LineChart
    private lateinit var minSystolicTextView: TextView
    private lateinit var maxSystolicTextView: TextView
    private lateinit var minDiastolicTextView: TextView
    private lateinit var maxDiastolicTextView: TextView
    private lateinit var avgSystolicTextView: TextView
    private lateinit var avgDiastolicTextView: TextView
    private val db = FirebaseFirestore.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayFormat = SimpleDateFormat("dd.MM", Locale.getDefault())
    /**
     * Metoda wywoływana podczas tworzenia aktywności.
     * Inicjalizuje widoki, pobiera dane z Firestore, a następnie wyświetla je na wykresie.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.raport_pressure)


        lineChart = findViewById(R.id.pressureLineChart)
        minSystolicTextView = findViewById(R.id.minSystolicTextView)
        maxSystolicTextView = findViewById(R.id.maxSystolicTextView)
        minDiastolicTextView = findViewById(R.id.minDiastolicTextView)
        maxDiastolicTextView = findViewById(R.id.maxDiastolicTextView)
        avgDiastolicTextView = findViewById(R.id.averageDiastolicTextView)
        avgSystolicTextView = findViewById(R.id.averageSystolicTextView)
        val userId = FirebaseAuth.getInstance().currentUser!!.email

        // Pobieranie danych z Firestore
        db.collection("measurements")
            .whereEqualTo("userID", userId)
            .get()
            .addOnSuccessListener { result ->

                // Mapy do przechowywania danych ciśnienia
                val systolicData = mutableMapOf<String, MutableList<Float>>()
                val diastolicData = mutableMapOf<String, MutableList<Float>>()

                // Grupowanie danych według daty
                for (document in result) {
                    val date = document.getString("date")
                    val bloodPressure = document.getString("bloodPressure")

                    if (date != null && bloodPressure != null) {
                        val pressureParts = bloodPressure.split("/")
                        val systolic = pressureParts.getOrNull(0)?.toFloatOrNull()
                        val diastolic = pressureParts.getOrNull(1)?.toFloatOrNull()

                        // Jeśli wartości ciśnienia są poprawne
                        if (systolic != null && diastolic != null) {
                            // Dodawanie danych do odpowiednich map
                            systolicData.getOrPut(date) { mutableListOf() }.add(systolic)
                            diastolicData.getOrPut(date) { mutableListOf() }.add(diastolic)
                        }
                    }
                }

                // Obliczanie średnich dla systolicznego i diastolicznego ciśnienia dla każdego dnia
                val avgSystolicPerDay = systolicData.mapValues { (_, systolics) -> systolics.average().toFloat() }
                val avgDiastolicPerDay = diastolicData.mapValues { (_, diastolics) -> diastolics.average().toFloat() }

                // Wyświetlanie średnich na wykresie
                displayPressureChart(avgSystolicPerDay, avgDiastolicPerDay)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Błąd: $exception", Toast.LENGTH_SHORT).show()
            }

        findViewById<Button>(R.id.returnFromPressureRaportButton).setOnClickListener {
            finish()
        }
    }
    /**
     * Metoda wyświetlająca wykres ciśnienia.
     * Na wykresie prezentowane są średnie wartości ciśnienia skurczowego i rozkurczowego w ciągu ostatnich 30 dni.
     */
    private fun displayPressureChart(
        avgSystolicPerDay: Map<String, Float>,
        avgDiastolicPerDay: Map<String, Float>
    ) {
        val entriesSystolic = mutableListOf<Entry>()
        val entriesDiastolic = mutableListOf<Entry>()

        // Sortowanie dat (kluczy mapy) w porządku rosnącym
        val sortedDates = avgSystolicPerDay.keys.sortedBy { dateFormat.parse(it) }

        // Ustalanie daty sprzed 30 dni
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        val date30DaysAgo = calendar.time

        // Filtrowanie wyników z ostatnich 30 dni
        val filteredDates = sortedDates.filter { dateFormat.parse(it)?.after(date30DaysAgo) == true }

        // Zbieranie wartości skurczowego i rozkurczowego ciśnienia z przefiltrowanych wyników
        filteredDates.forEachIndexed { index, date ->
            val systolic = avgSystolicPerDay[date] ?: 0f
            val diastolic = avgDiastolicPerDay[date] ?: 0f

            // Tworzenie wpisów na wykres
            entriesSystolic.add(Entry(index.toFloat(), systolic))
            entriesDiastolic.add(Entry(index.toFloat(), diastolic))
        }

        // Obliczanie średnich dla skurczowego i rozkurczowego ciśnienia
        val averageSystolic = filteredDates.map { avgSystolicPerDay[it] ?: 0f }.average().toFloat()
        val averageDiastolic = filteredDates.map { avgDiastolicPerDay[it] ?: 0f }.average().toFloat()

        avgSystolicTextView.text = "%.2f".format(averageSystolic)
        avgDiastolicTextView.text = "%.2f".format(averageDiastolic)

        // Tworzenie wykresów dla ciśnienia skurczowego i rozkurczowego
        val systolicDataSet = LineDataSet(entriesSystolic, "Skurczowe").apply {
            color = Color.RED
            setCircleColor(Color.RED)
            lineWidth = 2f
            circleRadius = 3f
            setDrawValues(false)
        }

        val diastolicDataSet = LineDataSet(entriesDiastolic, "Rozkurczowe").apply {
            color = Color.BLUE
            setCircleColor(Color.BLUE)
            lineWidth = 2f
            circleRadius = 3f
            setDrawValues(false)
        }
        // Łączenie danych w wykres
        val lineData = LineData(systolicDataSet, diastolicDataSet)
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
                // Obliczanie limitów ciśnienia na podstawie wieku i płci
                val (systolicLowerLimit, systolicUpperLimit) = calculateSystolicLimits(age, gender)
                val (diastolicLowerLimit, diastolicUpperLimit) = calculateDiastolicLimits(age, gender)

                // Tworzenie linii limitów dla ciśnienia skurczowego i rozkurczowego
                val systolicLowerLimitLine =
                    LimitLine(systolicLowerLimit.toFloat(), "Dolna granica normy").apply {
                        lineWidth = 2f
                        lineColor = Color.RED
                        textColor = Color.RED
                    }

                val systolicUpperLimitLine =
                    LimitLine(systolicUpperLimit.toFloat(), "Górna granica normy").apply {
                        lineWidth = 2f
                        lineColor = Color.RED
                        textColor = Color.RED
                    }

                val diastolicLowerLimitLine =
                    LimitLine(diastolicLowerLimit.toFloat(), "Dolna granica normy").apply {
                        lineWidth = 2f
                        lineColor = Color.BLUE
                        textColor = Color.BLUE
                    }

                val diastolicUpperLimitLine =
                    LimitLine(diastolicUpperLimit.toFloat(), "Górna granica normy").apply {
                        lineWidth = 2f
                        lineColor = Color.BLUE
                        textColor = Color.BLUE
                    }

                // Dodanie linii limitów do osi Y
                val yAxisLeft = lineChart.axisLeft
                yAxisLeft.addLimitLine(systolicLowerLimitLine)
                yAxisLeft.addLimitLine(systolicUpperLimitLine)
                yAxisLeft.addLimitLine(diastolicLowerLimitLine)
                yAxisLeft.addLimitLine(diastolicUpperLimitLine)

                val systolicMin = avgSystolicPerDay.values.minOrNull() ?: 2f
                val systolicMax = avgSystolicPerDay.values.maxOrNull() ?: 2f
                val diastolicMin = avgDiastolicPerDay.values.minOrNull() ?: 2f
                val diastolicMax = avgDiastolicPerDay.values.maxOrNull() ?: 2f

                minSystolicTextView.text = "%.2f".format(systolicMin)
                maxSystolicTextView.text = "%.2f".format(systolicMax)
                minDiastolicTextView.text = "%.2f".format(diastolicMin)
                maxDiastolicTextView.text = "%.2f".format(diastolicMax)

                // Odświeżenie wykresu
                lineChart.invalidate()
            }
        }
    }

    /**
     * Funkcja obliczająca limity ciśnienia skurczowego w zależności od wieku i płci.
     *
     * @param age Wiek użytkownika.
     * @param gender Płeć użytkownika.
     * @return Para wartości (limit górny, limit dolny) ciśnienia.
     */
    private fun calculateSystolicLimits(age: Int, gender: String): Pair<Int, Int> {
        return when (age) {
            in 18..39 -> if (gender == Gender.MALE.toString()) 114 to 124 else 105 to 115
            in 40..59 -> if (gender == Gender.MALE.toString()) 119 to 129 else 117 to 127
            else -> if (gender == Gender.MALE.toString()) 128 to 138 else 134 to 144
        }
    }

    /**
     * Funkcja obliczająca limity ciśnienia rozkurczowego w zależności od wieku i płci.
     *
     * @param age Wiek użytkownika.
     * @param gender Płeć użytkownika.
     * @return Para wartości (limit górny, limit dolny) ciśnienia.
     */
    private fun calculateDiastolicLimits(age: Int, gender: String): Pair<Int, Int> {
        return when (age) {
            in 18..39 -> if (gender == Gender.MALE.toString()) 65 to 75 else 63 to 73
            in 40..59 -> if (gender == Gender.MALE.toString()) 72 to 82 else 69 to 79
            else -> if (gender == Gender.MALE.toString()) 64 to 74 else 63 to 73
        }
    }
    /**
     * Funkcja pobierająca dane użytkownika z Firestore.
     *
     * @param callback Funkcja zwrotna, która zwraca dane użytkownika (wiek, płeć).
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