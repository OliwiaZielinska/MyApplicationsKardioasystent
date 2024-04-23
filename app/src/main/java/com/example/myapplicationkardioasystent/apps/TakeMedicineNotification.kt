package com.example.myapplicationkardioasystent.apps

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationkardioasystent.R
import com.google.android.material.textfield.TextInputEditText

/**
 * Aktywność odpowiedzialna za wyświetlanie powiadomienia o konieczności przyjęcia leku.
 * Umożliwia użytkownikowi zamknięcie powiadomienia lub przypomnienie o przyjęciu leku później.
 */

class TakeMedicineNotification : AppCompatActivity() {

    private lateinit var drugNameInputText: TextInputEditText
    private lateinit var timeOfDrugInputText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            setContentView(R.layout.take_medicament_notification)

        // Inicjalizacja elementów interfejsu użytkownika
        drugNameInputText = findViewById(R.id.drugNameInputText)
        timeOfDrugInputText = findViewById(R.id.timeOfDrugInputText)


        val closeNotificationDrugButton = findViewById<Button>(R.id.closeNotificationDrugButton)
        closeNotificationDrugButton.setOnClickListener {
            openActivity()
        }

        // Tutaj obsługa przycisku "Przypomnij później"
        val remindLaterButton = findViewById<Button>(R.id.remindLaterButton)
        remindLaterButton.setOnClickListener {
            //to powinno coś raczej robić, ale nie wiem jak zrobić, żeby jakby po jakimś czasie znowu przypominało
        }
    }

    /**
     * Metoda otwierania aktywności głównej.
     */

    private fun openActivity() {
            val intent = Intent(this, MainViewApp::class.java)
            startActivity(intent)
        }

    }

