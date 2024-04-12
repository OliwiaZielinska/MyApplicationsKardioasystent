package com.example.myapplicationkardioasystent
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HealthAdvices  : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.health_advices)


        val buttonPowrot = findViewById<Button>(R.id.powrotPoradnikButton)

        // Obsługa przycisku "Powrót"
        buttonPowrot.setOnClickListener {
            //powrót do głównego okna aplikacji
            val intent = Intent(this, MainViewApp::class.java)
            startActivity(intent)
        }
    }
}







