package com.example.myapplicationkardioasystent.apps
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationkardioasystent.R
import com.example.myapplicationkardioasystent.cloudFirestore.FirestoreDatabaseOperations
import com.example.myapplicationkardioasystent.cloudFirestore.Measurment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Aktywność umożliwiająca wprowadzenie wyniku pomiaru.
 * Umożliwia użytkownikowi zapisanie pomiaru i powrót do głównego widoku aplikacji.
 */
class EnterMeasurment : AppCompatActivity() {
    // Referencja do obiektu FirebaseFirestore do interakcji z bazą danych Firestore
    private val db = Firebase.firestore

    // Obiekt do obsługi operacji na bazie danych Firestore
    private val dbOperations = FirestoreDatabaseOperations(db)

    private lateinit var dateOfMeasurementInputText: TextInputEditText
    private lateinit var hourInputText: TextInputEditText
    private lateinit var bloodPressureInputText: TextInputEditText
    private lateinit var pulseInputText: TextInputEditText

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.enter_measurment_app)

        val userID = intent.getStringExtra("userID")

        // Inicjalizacja elementów interfejsu użytkownika
        dateOfMeasurementInputText = findViewById(R.id.dateOfMeasurementInputText)
        hourInputText = findViewById(R.id.hourInputText)
        bloodPressureInputText = findViewById(R.id.bloodPressureInputText)
        pulseInputText = findViewById(R.id.pulseInputText)

        // Tutaj obsługa przycisku "Zapisz wynik pomiaru"
        val recordMeasurementResultButton = findViewById<Button>(R.id.recordMeasurementResultButton)
        recordMeasurementResultButton.setOnClickListener {
            val date = dateOfMeasurementInputText.text.toString()
            val hour = hourInputText.text.toString()
            val bloodPressure = bloodPressureInputText.text.toString()
            val pulse = pulseInputText.text.toString()
            val measurment = Measurment(
                date,
                hour,
                bloodPressure,
                pulse
            )
            // Uruchomienie korutyny w wątku głównym
            GlobalScope.launch(Dispatchers.Main) {
                // Dodanie studenta do bazy danych Firestore userId=login
                dbOperations.addMeasurment(userID.toString(), measurment)
            }
            openActivity()
        }
    }
    /**
     * Metoda do otwarcia głównego widoku aplikacji.
     */
    private fun openActivity() {
        val intent = Intent(this, MainViewApp::class.java)
        startActivity(intent)
    }
}
