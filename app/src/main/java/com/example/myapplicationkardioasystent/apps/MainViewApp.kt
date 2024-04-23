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
            //TU GENERALNIE MUSIMY WSTAWIĆ POTEM PRZENIESIENIE DO OKNA STATYSTYK (ten średni pomiar,
            // max min itp, ale nie mamy jeszcze takiego okna xd)
        }

        // Obsługa przycisku "Ustawienia"
        settingsButton.setOnClickListener {
            //TU GENERALNIE MUSIMY POTEM WSTAWIĆ POTEM PRZENIESIENIE DO OKNA USTAWIENIA (dać użytkownikowi
        // możliwość zmiany godzin pomiaru itp.) ALE TAKIEGO OKNA TEŻ JESZCZE NIE MAMY WIĘC TU NIC NIE WPISYWAŁAM
            // Wykorzystanie update danych w bazie danych
        }

        // Obsługa przycisku "Poradnik zdrowia"
        healthGuideButton.setOnClickListener {
            openActivity2()
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
    private fun openActivity2() {
        val intent = Intent(this, HealthAdvices::class.java)
        startActivity(intent)
    }
}
