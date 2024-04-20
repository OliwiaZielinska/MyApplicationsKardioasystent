package com.example.myapplicationkardioasystent.Apps
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationkardioasystent.R

/**
 * Aktywność wyświetlająca poradnik zdrowia pomagający zadbać o kondycję serca.
 * Umożliwia użytkownikowi powrót do głównego widoku aplikacji.
 */

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
    /**
     * Metoda do otwarcia głównego widoku aplikacji.
     */
    private fun openActivity2() {
        val intent = Intent(this, MainViewApp::class.java)
        startActivity(intent)
    }
}