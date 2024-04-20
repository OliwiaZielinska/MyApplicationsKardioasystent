package com.example.myapplicationkardioasystent.Apps
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


    private lateinit var dataPomiaruInputText: TextInputEditText
    private lateinit var godzinaInputText: TextInputEditText
    private lateinit var cisnienieInputText: TextInputEditText
    private lateinit var tetnoInputText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.enter_measurment_app)

        // Inicjalizacja elementów interfejsu użytkownika
        dataPomiaruInputText = findViewById(R.id.dataPomiaruInputText)
        godzinaInputText = findViewById(R.id.godzinaInputText)
        cisnienieInputText = findViewById(R.id.cisnienieInputText)
        tetnoInputText = findViewById(R.id.tetnoInputText)

        // Tutaj obsługa przycisku "Zapisz wynik pomiaru"
        val zapiszWynikPomiaruButton = findViewById<Button>(R.id.zapiszWynikPomiaruButton)
        zapiszWynikPomiaruButton.setOnClickListener {
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
