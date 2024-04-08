package com.example.myapplicationkardioasystent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class SecondFragment : AppCompatActivity() {
    private lateinit var imieInput:EditText
    private lateinit var nazwiskoInput:EditText
    private lateinit var plecInput:EditText
    private lateinit var wiekInput:EditText
    private lateinit var pytInput:EditText
    private lateinit var nazwaLekInput:EditText
    private lateinit var godzinaInput:EditText
    private lateinit var zarejestrujSieButtonKoniec: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_second)

        // Inicjalizacja elementów interfejsu użytkownika
        imieInput = findViewById(R.id.imieInput)
        nazwiskoInput = findViewById(R.id.nazwiskoInput)
        plecInput = findViewById(R.id.plecInput)
        wiekInput = findViewById(R.id.wiekInput)
        pytInput = findViewById(R.id.lekiPytanieInput)
        nazwaLekInput = findViewById(R.id.nazwaLekuInput)
        godzinaInput = findViewById(R.id.godzinaInput)
        zarejestrujSieButtonKoniec = findViewById(R.id.zarejestrujSieButton)

        // Obsługa kliknięcia przycisku "Zarejestruj się"
        zarejestrujSieButtonKoniec.setOnClickListener {
            openActivity()
        }
    }

    // Metoda do otwierania drugiej aktywności
    private fun openActivity(){
        val intent = Intent(this, MainActivityLogin::class.java)
        startActivity(intent)
    }
}