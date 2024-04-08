package com.example.myapplicationkardioasystent
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class MainViewApp : AppCompatActivity() {

    private lateinit var wprowadzWynikPomiaruButton: Button
    private lateinit var statystykiButton: Button
    private lateinit var ustawieniaButton: Button
    private lateinit var poradnikZdrowiaButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_view_app)

        // Inicjalizacja elementów interfejsu użytkownika
        wprowadzWynikPomiaruButton = findViewById(R.id.wprowadzWynikPomiaruButton)
        statystykiButton = findViewById(R.id.statystykiButton)
        ustawieniaButton = findViewById(R.id.ustawieniaButton)
        poradnikZdrowiaButton = findViewById(R.id.poradnikZdrowiaButton)

        // Obsługa przycisku "Wprowadź wynik pomiaru"
        wprowadzWynikPomiaruButton.setOnClickListener {
            openActivity()
            //TU GENERALNIE powinno przenosić DO TEGO EnterMeasurment, ale no wiadomo że nie przenosi xd
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
            //TU GENERALNIE MUSIMY POTEM WSTAWIĆ POTEM PRZENIESIENIE DO OKNA Z PORADAMI ZDROWIA
        // ALE TAKIEGO OKNA TEŻ JESZCZE NIE MAMY WIĘC TU NIC NIE WPISYWAŁAM
        }
    }
//to teoretycznie powinno przenieść użytkownika do tej aktywności wprowadzenia pomiaru, ale no jak wiesz to nie do końca działa xd
    private fun openActivity() {
        val intent = Intent(this, EnterMeasurment::class.java)
        startActivity(intent)
    }
}
