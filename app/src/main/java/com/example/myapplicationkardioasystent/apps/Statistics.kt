package com.example.myapplicationkardioasystent.apps

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplicationkardioasystent.R
import com.example.myapplicationkardioasystent.cloudFirestore.Measurment
import com.example.myapplicationkardioasystent.recyclerView.MeasurementAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Aktywność do wyświetlania statystyk pomiarów.
 */
class Statistics : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val patientMeasurementList = mutableListOf<Measurment>()
    private val filteredMeasurementList = mutableListOf<Measurment>()
    private lateinit var adapter: MeasurementAdapter
    private lateinit var spinnerFilter: Spinner
    /**
     * Inicjalizuje widok statystyk oraz ustawia dane dla listy pomiarów i filtra.
     * Wywołuje funkcję odpowiedzialną za wczytanie danych pomiarów i ich filtrowanie.
     * @param savedInstanceState Obiekt przechowujący stan aktywności, przekazany podczas jej tworzenia.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.statistics)
        setData()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = MeasurementAdapter(filteredMeasurementList, intent)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        spinnerFilter = findViewById(R.id.spinnerFilter)
        val adapterSpinner = ArrayAdapter.createFromResource(
            this,
            R.array.filter_options,
            android.R.layout.simple_spinner_item
        )
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilter.adapter = adapterSpinner

        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val filter = parent.getItemAtPosition(position)?.toString() ?: "Tydzień"
                filterData(filter)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        val returnFromStatisticsButton = findViewById<Button>(R.id.returnFromStatisticsButton)
        val userId = FirebaseAuth.getInstance().currentUser!!.email
        returnFromStatisticsButton.setOnClickListener {
            openMainActivity(userId.toString())
        }

        val raportOfStatisticsButton = findViewById<Button>(R.id.raportOfStatisticsButton)
        raportOfStatisticsButton.setOnClickListener {
            openRaportsActivity(userId.toString())
        }
    }

    /**
     * Pobiera dane pomiarowe pacjenta z bazy danych Firebase Firestore.
     */
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
                val filter = spinnerFilter.selectedItem?.toString() ?: "Tydzień"
                filterData(filter)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Błąd: $exception", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Filtruje dane pomiarowe na podstawie wybranego kryterium.
     * @param filter Kryterium, na podstawie którego dane mają zostać przefiltrowane.
     *               Może to być np. "Tydzień", "Miesiąc" lub "Rok".
     */
    private fun filterData(filter: String) {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        filteredMeasurementList.clear()
        for (measurement in patientMeasurementList) {
            try {
                val measurementDate = dateFormat.parse(measurement.date)
                if (measurementDate != null) {
                    val shouldAdd = when (filter) {
                        "Tydzień" -> {
                            calendar.add(Calendar.WEEK_OF_YEAR, -1)
                            measurementDate.after(calendar.time)
                        }
                        "Miesiąc" -> {
                            calendar.add(Calendar.MONTH, -1)
                            measurementDate.after(calendar.time)
                        }
                        "Rok" -> {
                            calendar.add(Calendar.YEAR, -1)
                            measurementDate.after(calendar.time)
                        }
                        else -> true
                    }
                    if (shouldAdd) {
                        filteredMeasurementList.add(measurement)
                    }
                    calendar.time = Calendar.getInstance().time
                }
            } catch (e: ParseException) {
                //logowany błąd parsowania daty
            }
        }

        // Sortowanie pomiarów po dacie i godzinie
        filteredMeasurementList.sortBy { measurement ->
            try {
                dateTimeFormat.parse("${measurement.date} ${measurement.hour}")
            } catch (e: ParseException) {
                null
            }
        }

        adapter.notifyDataSetChanged()
    }


    /**
     * Otwiera główną aktywność aplikacji.
     * @param userID Identyfikator użytkownika, który jest przekazywany do głównej aktywności.
     */
    private fun openMainActivity(userID : String) {
        val intent = Intent(this, MainViewApp::class.java)
        intent.putExtra("uID", userID)
        startActivity(intent)
    }

    /**
     * Otwiera layout raportów.
     * @param userID Identyfikator użytkownika, który jest przekazywany do głównej aktywności.
     */
    private fun openRaportsActivity(userID : String) {
        val intent = Intent(this, Raports::class.java)
        intent.putExtra("uID", userID)
        startActivity(intent)
    }
}
