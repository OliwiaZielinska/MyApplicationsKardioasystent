package com.example.myapplicationkardioasystent.Apps
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationkardioasystent.Login.MainActivity
import com.example.myapplicationkardioasystent.R

/**
 *  Główna aktywność aplikacji, wyświetlająca interfejs użytkownika.
 *  Umożliwia nawigację do różnych funkcji aplikacji.
 */
class MainViewApp : AppCompatActivity() {

    private lateinit var wprowadzWynikPomiaruButton: Button
    private lateinit var statystykiButton: Button
    private lateinit var ustawieniaButton: Button
    private lateinit var poradnikZdrowiaButton: Button
    private var witajUzytkownikuText: TextView? = null
    private lateinit var wylogujSieButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_view_app)

        wprowadzWynikPomiaruButton = findViewById(R.id.wprowadzWynikPomiaruButton)
        statystykiButton = findViewById(R.id.statystykiButton)
        ustawieniaButton = findViewById(R.id.ustawieniaButton)
        poradnikZdrowiaButton = findViewById(R.id.poradnikZdrowiaButton)
        wylogujSieButton = findViewById(R.id.wylogujSieButton)


        // Obsługa przycisku "Wprowadź wynik pomiaru"
        wprowadzWynikPomiaruButton.setOnClickListener {
            openActivity()
        }


        // Obsługa przycisku "Statystyki"
        statystykiButton.setOnClickListener {
            //TU GENERALNIE MUSIMY WSTAWIĆ POTEM PRZENIESIENIE DO OKNA STATYSTYK (ten średni pomiar,
            // max min itp, ale nie mamy jeszcze takiego okna xd)
        }

        // Obsługa przycisku "Ustawienia"
        ustawieniaButton.setOnClickListener {
            //TU GENERALNIE MUSIMY POTEM WSTAWIĆ POTEM PRZENIESIENIE DO OKNA USTAWIENIA (dać użytkownikowi
        // możliwość zmiany godzin pomiaru itp.) ALE TAKIEGO OKNA TEŻ JESZCZE NIE MAMY WIĘC TU NIC NIE WPISYWAŁAM
        }

        // Obsługa przycisku "Poradnik zdrowia"
        poradnikZdrowiaButton.setOnClickListener {
            openActivity2()
        }

        wylogujSieButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    /**
     * Metoda do otwarcia aktywności wprowadzania wyniku pomiaru.
     */
    private fun openActivity() {
        val intent = Intent(this, EnterMeasurment::class.java)
        startActivity(intent)
    }
    /**
     * Metoda do otwarcia aktywności poradnika zdrowia.
     */
    private fun openActivity2() {
        val intent = Intent(this, HealthAdvices::class.java)
        startActivity(intent)
    }

    fun getUserId(userID: String) {
        // Inicjalizacja elementów interfejsu użytkownika
        val intent = intent

        witajUzytkownikuText = findViewById(R.id.witajUzytkownikuText)
        // Ustaw tekst powitalny, wykorzystując wartość userID
        witajUzytkownikuText?.text = "Welcome ${userID}!"
    }
}
