package com.example.myapplicationkardioasystent
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText


class MissingMeasurmantNotification : AppCompatActivity () {


    private lateinit var dataPomiaruInputText: TextInputEditText
    private lateinit var godzinaPomiaruInputText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.missing_measurment_notification)

        // Inicjalizacja elementów interfejsu użytkownika
        dataPomiaruInputText = findViewById(R.id.dataPomiaruInputText)
        godzinaPomiaruInputText = findViewById(R.id.godzinaPomiaruInputText)

        // Tutaj obsługa przycisku "Wprowadź wynik pomiaru"
        val buttonWprowadzWynik = findViewById<Button>(R.id.wprowadzWynikPomiaruPowiadomienieButton)
        buttonWprowadzWynik.setOnClickListener {
            openActivity1()
            //TU GENERALNIE powinno przenosić DO TEGO EnterMeasurment, ale no wiadomo że nie przenosi xd
        }

        // Tutaj obsługa przycisku "Zamknij powiadomienie"
        val buttonZamknijPowiadomienie = findViewById<Button>(R.id.zamknijPowiadomienieButton)
        buttonZamknijPowiadomienie.setOnClickListener {
            openActivity2()
            //zakładam że powinno to przenosić użytkownika do okna MainViewApp, ale można to potem zmodyfikować
        }
    }

    //to teoretycznie powinno przenieść użytkownika do tej aktywności wprowadzenia pomiaru, ale  to nie do końca działa xd
    private fun openActivity1() {
        val intent = Intent(this, EnterMeasurment::class.java)
        startActivity(intent)
    }

    private fun openActivity2() {
        //to teoretycznie powinno przenieść użytkownika do tej aktywności powrotu do głównego okna, ale nie działa xd
        val intent = Intent(this, MainViewApp::class.java)
        startActivity(intent)
    }
}