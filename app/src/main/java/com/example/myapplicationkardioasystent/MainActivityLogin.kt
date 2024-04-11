package com.example.myapplicationkardioasystent

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.myapplicationkardioasystent.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class MainActivityLogin : AppCompatActivity() {

    private lateinit var nickInputRejestracja: EditText
    private lateinit var hasloInputRejestracja: EditText
    private lateinit var RanoRejestracjaInput: EditText
    private lateinit var PoludnieRejestracja: EditText
    private lateinit var WieczorRejestracja: EditText
    private lateinit var zalozKontroButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_login)

        // Inicjalizacja elementów interfejsu użytkownika
        nickInputRejestracja = findViewById(R.id.nickInputRejestracja)
        hasloInputRejestracja = findViewById(R.id.hasloInputRejestracja)
        RanoRejestracjaInput = findViewById(R.id.ranoRejestracjaInput)
        PoludnieRejestracja = findViewById(R.id.PoludnieRejestracja)
        WieczorRejestracja = findViewById(R.id.WieczorRejestracja)
        zalozKontroButton = findViewById(R.id.zalozKontoButton)

        // Obsługa kliknięcia przycisku "Zarejestruj się"
        zalozKontroButton.setOnClickListener {
            openActivity()
        }
    }

    // Metoda do otwierania drugiej aktywności POPRAWIĆ ABY PRZENOSIŁO NA GŁÓWNĄ STRONĘ :)
    private fun openActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}