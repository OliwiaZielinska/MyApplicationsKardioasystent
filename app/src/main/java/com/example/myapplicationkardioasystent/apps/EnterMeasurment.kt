package com.example.myapplicationkardioasystent.apps
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationkardioasystent.R
import com.google.android.material.textfield.TextInputEditText

/**
 * Aktywność umożliwiająca wprowadzenie wyniku pomiaru.
 * Umożliwia użytkownikowi zapisanie pomiaru i powrót do głównego widoku aplikacji.
 */
class EnterMeasurment : AppCompatActivity() {
    private lateinit var dateOfMeasurementInputText: TextInputEditText
    private lateinit var hourInputText: TextInputEditText
    private lateinit var bloodPressureInputText: TextInputEditText
    private lateinit var pulseInputText: TextInputEditText

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
