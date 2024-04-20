package com.example.myapplicationkardioasystent.Apps
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.example.myapplicationkardioasystent.R
import com.example.myapplicationkardioasystent.Registation.BaseActivity
import com.google.android.material.textfield.TextInputEditText

/**
 * Aktywność odpowiedzialna za wyświetlanie powiadomienia o braku wykonanego pomiaru.
 * Umożliwia użytkownikowi wprowadzenie wyniku pomiaru lub zamknięcie powiadomienia.
 */

class MissingMeasurmantNotification : BaseActivity () {

    // Deklaracje pól TextInputEditText
    private lateinit var dataPomiaruInputText: TextInputEditText
    private lateinit var godzinaPomiaruInputText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.missing_measurment_notification)

        // Inicjalizacja elementów interfejsu użytkownika
        dataPomiaruInputText = findViewById(R.id.dataPomiaruInputText)
        godzinaPomiaruInputText = findViewById(R.id.godzinaPomiaruInputText)

        // Tutaj obsługa przycisku "Wprowadź wynik pomiaru"
        val buttonWprowadzWynik = findViewById<Button>(R.id.wprowadzWynikPomiaruPowiadomienieButton)
        buttonWprowadzWynik.setOnClickListener {
            openActivity1()
        }

        // Tutaj obsługa przycisku "Zamknij powiadomienie"
        val buttonZamknijPowiadomienie = findViewById<Button>(R.id.zamknijPowiadomienieButton)
        buttonZamknijPowiadomienie.setOnClickListener {
            openActivity2()
        }
    }

    /**
     * Metoda do otwarcia aktywności wprowadzania wyniku pomiaru.
     */
    private fun openActivity1() {
        val intent = Intent(this, EnterMeasurment::class.java)
        startActivity(intent)
    }

    /**
     * Metoda do otwarcia aktywności głównej.
     */
    private fun openActivity2() {
        val intent = Intent(this, MainViewApp::class.java)
        startActivity(intent)
    }
}