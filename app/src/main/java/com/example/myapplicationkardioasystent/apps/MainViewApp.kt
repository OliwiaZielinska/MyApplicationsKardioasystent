package com.example.myapplicationkardioasystent.apps
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationkardioasystent.R
import com.example.myapplicationkardioasystent.login.MainActivity

/**
 *  Główna aktywność aplikacji, wyświetlająca interfejs użytkownika.
 *  Umożliwia nawigację do różnych funkcji aplikacji.
 */
class MainViewApp : AppCompatActivity() {

    private lateinit var enterTheMeasurementResultButton: Button
    private lateinit var statisticsButton: Button
    private lateinit var settingsButton: Button
    private lateinit var healthGuideButton: Button
    private var helloUserText: TextView? = null
    private lateinit var logOutButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_view_app)

        val uID = intent.getStringExtra("uID")

        enterTheMeasurementResultButton = findViewById(R.id.enterTheMeasurementResultButton)
        statisticsButton = findViewById(R.id.statisticsButton)
        settingsButton = findViewById(R.id.settingsButton)
        healthGuideButton = findViewById(R.id.healthGuideButton)
        logOutButton = findViewById(R.id.logOutButton)

        helloUserText = findViewById(R.id.helloUserText)
        // Ustaw tekst powitalny, wykorzystując wartość userID
        helloUserText?.text = "Welcome ${uID}!"

        // Obsługa przycisku "Wprowadź wynik pomiaru"
        enterTheMeasurementResultButton.setOnClickListener {
            openActivity(uID.toString())
        }


        // Obsługa przycisku "Statystyki"
        statisticsButton.setOnClickListener {
            openActivityStatistics()
        }

        // Obsługa przycisku "Ustawienia"
        settingsButton.setOnClickListener {
            openActivitySettings()
        }

        // Obsługa przycisku "Poradnik zdrowia"
        healthGuideButton.setOnClickListener {
            openActivityHealthAdvices()
        }

        // Obsługa przycisku "Wyloguj się"
        logOutButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    /**
     * Metoda do otwarcia aktywności wprowadzania wyniku pomiaru.
     */
    private fun openActivity(userID : String) {
        val intent = Intent(this, EnterMeasurment::class.java)
        intent.putExtra("userID", userID)
        startActivity(intent)
    }
    /**
     * Metoda do otwarcia aktywności poradnika zdrowia.
     */
    private fun openActivityHealthAdvices() {
        val intent = Intent(this, HealthAdvices::class.java)
        startActivity(intent)
    }

    /**
     * Metoda do otwarcia aktywności związanej ze zmianą ustawień dotyczących przyjmowanych leków.
     */
    private fun openActivitySettings() {
        val intent = Intent(this, Settings::class.java)
        startActivity(intent)
    }
    /**
     * Metoda do otwarcia aktywności związanej z wyświetleniem statystyk zalogowanego użytkownika.
     */
    private fun openActivityStatistics() {
        val intent = Intent(this, Statistics::class.java)
        startActivity(intent)
    }
}
