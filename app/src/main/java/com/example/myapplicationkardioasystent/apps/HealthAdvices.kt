package com.example.myapplicationkardioasystent.apps
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationkardioasystent.R

/**
 * Aktywność wyświetlająca poradnik zdrowia pomagający zadbać o kondycję serca.
 * Umożliwia użytkownikowi powrót do głównego widoku aplikacji.
 */
class HealthAdvices : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.health_advices)

        // Obsługa przycisku "Powrót"
        val backGuideButton = findViewById<Button>(R.id.backGuideButton)
        backGuideButton.setOnClickListener {
            // Powrót do głównego okna aplikacji
            openMainActivity()
        }
    }

    /**
     * Metoda do otwarcia głównego widoku aplikacji.
     */
    private fun openMainActivity() {
        val intent = Intent(this, MainViewApp::class.java)
        startActivity(intent)
    }
}
