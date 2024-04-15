package com.example.myapplicationkardioasystent.Login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationkardioasystent.R
import com.example.myapplicationkardioasystent.Registation.MainActivityRegistration1

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)

        // Inicjalizacja elementów interfejsu użytkownika
        val zalogujSieButtonKoniec = findViewById<Button>(R.id.zalogujSieButtonKoniec)
        val zarejestrujSieButton1 = findViewById<Button>(R.id.zarejestrujSieButton1)

        // Obsługa kliknięcia przycisku "Zarejestruj się"
        zarejestrujSieButton1.setOnClickListener {
            val intent = Intent(this, MainActivityRegistration1::class.java)
            startActivity(intent)
        }
        zalogujSieButtonKoniec.setOnClickListener {
            val intent = Intent(this, ActivityMainLogin::class.java)
            startActivity(intent)
        }
    }
}