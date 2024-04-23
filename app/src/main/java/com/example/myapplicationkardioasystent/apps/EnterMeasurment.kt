package com.example.myapplicationkardioasystent.apps
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationkardioasystent.R
import com.example.myapplicationkardioasystent.cloudFirestore.Measurment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Aktywność umożliwiająca wprowadzenie wyniku pomiaru.
 * Umożliwia użytkownikowi zapisanie pomiaru i powrót do głównego widoku aplikacji.
 */
class EnterMeasurment : AppCompatActivity() {
    private lateinit var dateOfMeasurementInputText: TextInputEditText
    private lateinit var hourInputText: TextInputEditText
    private lateinit var bloodPressureInputText: TextInputEditText
    private lateinit var pulseInputText: TextInputEditText

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.enter_measurment_app)

        // Inicjalizacja elementów interfejsu użytkownika
        dateOfMeasurementInputText = findViewById(R.id.dateOfMeasurementInputText)
        hourInputText = findViewById(R.id.hourInputText)
        bloodPressureInputText = findViewById(R.id.bloodPressureInputText)
        pulseInputText = findViewById(R.id.pulseInputText)

        // Tutaj obsługa przycisku "Zapisz wynik pomiaru"
        val recordMeasurementResultButton = findViewById<Button>(R.id.recordMeasurementResultButton)
        recordMeasurementResultButton.setOnClickListener {
            val userID = intent.getStringExtra("userID").toString()
            val date = dateOfMeasurementInputText.text.toString()
            val hour = hourInputText.text.toString()
            val bloodPressure = bloodPressureInputText.text.toString()
            val pulse = pulseInputText.text.toString()

            val measurment = Measurment(
                userID,
                date,
                hour,
                bloodPressure,
                pulse
            )

            // Uruchomienie korutyny w wątku głównym
            GlobalScope.launch(Dispatchers.Main) {
                // Pobierz referencję do kolekcji
                val collectionRef = FirebaseFirestore.getInstance().collection("measurements")

                // Dodaj nowy dokument z automatycznie wygenerowanym identyfikatorem
                val documentRef = collectionRef.document()

                // Ustaw pola dokumentu
                documentRef.set(measurment)
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentRef.id}")
                        // Tutaj możesz obsłużyć sukces, np. wyświetlić komunikat dla użytkownika
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                        // Obsłuż błąd, np. wyświetlając komunikat dla użytkownika
                    }
            }

            openActivity(userID)
        }
    }
    /**
     * Metoda do otwarcia głównego widoku aplikacji.
     */
    private fun openActivity(userID : String) {
        val intent = Intent(this, MainViewApp::class.java)
        intent.putExtra("uID", userID)
        startActivity(intent)
    }
}
