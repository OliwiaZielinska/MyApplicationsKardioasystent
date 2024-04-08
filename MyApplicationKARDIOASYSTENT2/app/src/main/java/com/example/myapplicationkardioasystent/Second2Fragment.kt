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
import com.example.myapplicationkardioasystent.databinding.FragmentSecond2Binding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class Second2Fragment : AppCompatActivity() {

    private lateinit var LoginNickInput: EditText
    private lateinit var LoginHasloInput: EditText
    private lateinit var LoginZalogujSieButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_second2)

        // Inicjalizacja elementów interfejsu użytkownika
        LoginNickInput = findViewById(R.id.editText)
        LoginHasloInput = findViewById(R.id.LoginHasloInput)
        LoginZalogujSieButton = findViewById(R.id.LoginZalogujSieButton)

        // Obsługa kliknięcia przycisku "Zarejestruj się"
        LoginZalogujSieButton.setOnClickListener {
            openActivity()
        }
    }

    // Metoda do otwierania okna głównego po zalogowaniu się
    private fun openActivity(){
        val intent = Intent(this, MainViewApp::class.java)
        startActivity(intent)
    }
}