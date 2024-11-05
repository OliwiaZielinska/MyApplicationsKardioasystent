package com.example.myapplicationkardioasystent.apps

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationkardioasystent.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

/**
 * HealthAdvices - Aktywność odpowiedzialna za wyświetlanie porad zdrowotnych dla użytkowników
 * w formie losowej porady na temat zdrowia serca oraz zdrowego trybu życia.
 * Użytkownik może losować kolejne porady i wrócić do głównej aktywności aplikacji.
 */
class HealthAdvices : AppCompatActivity() {
    private lateinit var adviceTextView: TextView
    private lateinit var firestore: FirebaseFirestore
    private var healthTips = mutableListOf<String>()

    /**
     * Inicjalizuje komponenty widoku i ustawia funkcjonalność przycisków.
     * Ładuje listę porad zdrowotnych z Firestore i ustawia akcję dla przycisku losującego porady.
     * @param savedInstanceState Zapisany stan instancji aktywności, jeśli jest dostępny.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.health_advices)

        adviceTextView = findViewById(R.id.adviceTextView)
        firestore = FirebaseFirestore.getInstance()

        loadHealthTips()

        val drawAdviceButton = findViewById<Button>(R.id.drawAdviceButton)
        drawAdviceButton.setOnClickListener {
            displayRandomAdvice()
        }

        val userId = FirebaseAuth.getInstance().currentUser!!.email

        val backGuideButton = findViewById<Button>(R.id.backGuideButton)
        backGuideButton.setOnClickListener {
            openMainActivity(userId.toString())
        }
    }
    /**
     * Pobiera listę porad zdrowotnych z kolekcji "HealthTips" w Firestore.
     * Dodaje każdą poradę do listy `healthTips`. Jeśli wystąpi błąd, wyświetla komunikat Toast.
     */
    private fun loadHealthTips() {
        firestore.collection("HealthTips")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val advice = document.getString("text")
                    if (advice != null) {
                        healthTips.add(advice)
                    }
                }

                displayRandomAdvice()
            }
            .addOnFailureListener { exception ->
                Log.e("HealthAdvices", "Nie udało się pobrać porad zdrowotnych", exception)
                Toast.makeText(this, "Nie udało się pobrać porad zdrowotnych.", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Wyświetla losową poradę zdrowotną z listy `healthTips`.
     * Jeśli lista jest pusta, wyświetla komunikat "Brak porad zdrowotnych do wyświetlenia."
     */
    private fun displayRandomAdvice() {
        if (healthTips.isNotEmpty()) {
            val randomAdvice = healthTips[Random.nextInt(healthTips.size)]
            adviceTextView.text = randomAdvice
        } else {
            adviceTextView.text = "Brak porad zdrowotnych do wyświetlenia."
        }
    }
    /**
     * Otwiera główną aktywność aplikacji `MainViewApp`, przekazując identyfikator użytkownika.
     * @param userID Adres e-mail aktualnie zalogowanego użytkownika.
     */
    private fun openMainActivity(userID : String) {
        val intent = Intent(this, MainViewApp::class.java)
        intent.putExtra("uID", userID)
        startActivity(intent)
    }
}