package com.example.myapplicationkardioasystent

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class TakeMedicineNotification : AppCompatActivity() {

    private lateinit var nazwaLekuInputText: TextInputEditText
    private lateinit var godzinaLekuInputText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            setContentView(R.layout.take_medicament_notification)

        // Inicjalizacja elementów interfejsu użytkownika
        nazwaLekuInputText = findViewById(R.id.nazwaLekuInputText)
        godzinaLekuInputText = findViewById(R.id.godzinaLekuInputText)

        // Tutaj możesz dodać kod obsługi przycisku "Zamknij powiadomienie"
        val buttonZamknijPowiadomienie = findViewById<Button>(R.id.zamknijPowiadomienieLekButton)
        buttonZamknijPowiadomienie.setOnClickListener {
            openActivity()
           //zakładam że powinno to przenosić użytkownika do okna MainViewApp, ale można to potem zmodyfikować
        }

        // Tutaj obsługa przycisku "Przypomnij później"
        val buttonPrzypomnijPozniej = findViewById<Button>(R.id.przypomnijPozniejButton)
        buttonPrzypomnijPozniej.setOnClickListener {
            //to powinno coś raczej robić, ale nie wiem jak zrobić, żeby jakby po jakimś czasie znowu przypominało
        }
    }

    private fun openActivity() {
        //to teoretycznie powinno przenieść użytkownika do tej aktywności powrotu do głównego okna, ale nie działa xd
            val intent = Intent(this, MainViewApp::class.java)
            startActivity(intent)
        }

    }

