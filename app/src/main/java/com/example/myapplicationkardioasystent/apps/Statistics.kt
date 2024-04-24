package com.example.myapplicationkardioasystent.apps
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationkardioasystent.R

/**
 * Aktywność wyświetlająca statystyki wyników zdrowia użytkowika zalogowanego w aplikacji.
 * Umożliwia użytkownikowi powrót do głównego widoku aplikacji.
 */
class Statistics : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.statistics)

        // Obsługa przycisku "Powrót"
        val returnFromStatisticsButton= findViewById<Button>(R.id.returnFromStatisticsButton)
        returnFromStatisticsButton.setOnClickListener {
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
