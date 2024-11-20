package com.example.myapplicationkardioasystent.login
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationkardioasystent.R
import com.example.myapplicationkardioasystent.registation.MainActivityRegistration1

/**
 * Główna aktywność mająca za zadanie obsługiwać ekran logowania i rejestracji.
 * Umożliwia przejście do ekranu logowannia lub rejestracji.
 */
class MainActivity : AppCompatActivity() {
    /**
     * Inicjalizuje widok aktywności głównej oraz ustawia akcje dla przycisków i elementów interfejsu.
     * @param savedInstanceState Obiekt, który zawiera dane zapisane w poprzednim stanie aktywności.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)

        // Inicjalizacja elementów interfejsu użytkownika
        val logInButtonEnd = findViewById<Button>(R.id.logInButtonEnd)
        val signUpButton1 = findViewById<Button>(R.id.signUpButton1)

        // Obsługa kliknięcia przycisku "Zarejestruj się"
        signUpButton1.setOnClickListener {
            val intent = Intent(this, MainActivityRegistration1::class.java)
            startActivity(intent)
        }
        logInButtonEnd.setOnClickListener {
            val intent = Intent(this, ActivityMainLogin::class.java)
            startActivity(intent)
        }
    }
}