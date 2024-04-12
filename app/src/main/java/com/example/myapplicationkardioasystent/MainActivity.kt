package com.example.myapplicationkardioasystent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicjalizacja elementów interfejsu użytkownika
        val zalogujSieButtonKoniec = findViewById<Button>(R.id.zalogujSieButtonKoniec)
        val zarejestrujSieButton1 = findViewById<Button>(R.id.zarejestrujSieButton1)

        // Obsługa kliknięcia przycisku "Zarejestruj się"
        zarejestrujSieButton1.setOnClickListener {
            val intent = Intent(this, SecondFragment::class.java)
            startActivity(intent)
        }
        zalogujSieButtonKoniec.setOnClickListener {
            val intent = Intent(this, Second2Fragment::class.java)
            startActivity(intent)
        }
    }
}