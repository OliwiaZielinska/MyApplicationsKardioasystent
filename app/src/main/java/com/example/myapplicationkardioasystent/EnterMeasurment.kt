package com.example.myapplicationkardioasystent
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class EnterMeasurment : AppCompatActivity() {


    private lateinit var dataPomiaruInputText: TextInputEditText
    private lateinit var godzinaInputText: TextInputEditText
    private lateinit var cisnienieInputText: TextInputEditText
    private lateinit var tetnoInputText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.enter_measurment_app)

        // Inicjalizacja elementów interfejsu użytkownika
        dataPomiaruInputText = findViewById(R.id.dataPomiaruInputText)
        godzinaInputText = findViewById(R.id.godzinaInputText)
        cisnienieInputText = findViewById(R.id.cisnienieInputText)
        tetnoInputText = findViewById(R.id.tetnoInputText)

        // Tutaj obsługa przycisku "Zapisz wynik pomiaru"
        val zapiszWynikPomiaruButton = findViewById<Button>(R.id.zapiszWynikPomiaruButton)
        zapiszWynikPomiaruButton.setOnClickListener {
            openActivity()
            //zakładam że to ma przenosić użytkownika do głównego okna apki, ale mozna to potem zmienić, jak stwierdzimy, że chcemy inaczej
        }
    }
    //to teoretycznie powinno przenieść użytkownika do tej aktywności powrotu do głównego okna, ale nie działa xd
    private fun openActivity() {
        val intent = Intent(this, MainViewApp::class.java)
        startActivity(intent)
    }
}
