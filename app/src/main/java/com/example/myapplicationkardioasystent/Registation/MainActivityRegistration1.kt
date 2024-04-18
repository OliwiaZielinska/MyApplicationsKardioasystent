package com.example.myapplicationkardioasystent.Registation

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import com.example.myapplicationkardioasystent.R

class MainActivityRegistration1 : BaseActivity() {
    private lateinit var imieInput:EditText
    private lateinit var nazwiskoInput:EditText
    private lateinit var plecInput:EditText
    private lateinit var wiekInput:EditText
    private lateinit var pytInput:EditText
    private lateinit var nazwaLekInput:EditText
    private lateinit var godzinaInput:EditText
    private lateinit var zarejestrujSieButtonKoniec: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_registration1)

        // Inicjalizacja elementów interfejsu użytkownika
        imieInput = findViewById(R.id.imieInput)
        nazwiskoInput = findViewById(R.id.nazwiskoInput)
        plecInput = findViewById(R.id.plecInput)
        wiekInput = findViewById(R.id.wiekInput)
        pytInput = findViewById(R.id.lekiPytanieInput)
        nazwaLekInput = findViewById(R.id.nazwaLekuInput)
        godzinaInput = findViewById(R.id.godzinaInput)
        zarejestrujSieButtonKoniec = findViewById(R.id.zarejestrujSieButton)

        // Obsługa kliknięcia przycisku "Zarejestruj się"
        zarejestrujSieButtonKoniec.setOnClickListener {
            registerUser()
        }
    }

    private fun validateRegisterDetails(): Boolean {
        val imie = imieInput?.text.toString().trim()
        val nazwisko = nazwiskoInput?.text.toString().trim()
        val plec = plecInput?.text.toString().trim()
        val wiek = wiekInput?.text.toString().trim()
        val pyt = pytInput?.text.toString().trim()
        val nazwaLek = nazwaLekInput?.text.toString().trim()
        val godzina = godzinaInput?.text.toString().trim()

        if (TextUtils.isEmpty(imie)) {
            showErrorSnackBar(resources.getString(R.string.err_msg_enter_name), true)
            return false
        }

        if (TextUtils.isEmpty(nazwisko)) {
            showErrorSnackBar(resources.getString(R.string.err_msg_enter_surname), true)
            return false
        }

        if (TextUtils.isEmpty(plec)) {
            showErrorSnackBar(resources.getString(R.string.err_msg_enter_gender), true)
            return false
        }

        if (TextUtils.isEmpty(wiek)) {
            showErrorSnackBar(resources.getString(R.string.err_msg_enter_age), true)
            return false
        }

        if (TextUtils.isEmpty(pyt)) {
            showErrorSnackBar(resources.getString(R.string.err_msg_enter_question), true)
            return false
        }

        if (pyt.equals("Tak", ignoreCase = true)) {
            showErrorSnackBar(resources.getString(R.string.err_msg_enter_doctor_name), true)
            return false
        }

        if (pyt.equals("Nie", ignoreCase = true)) {
            // Jeśli użytkownik nie przyjmuje leków, wyłącz możliwość edycji pól godziny i nazwy leku
            godzinaInput.isEnabled = false
            nazwaLekInput.isEnabled = false

            // Bezpośrednio zwracamy true, ponieważ nie ma potrzeby walidacji pól godziny i nazwy leku
            return true
        }

        // Sprawdzamy tylko wtedy, gdy użytkownik deklaruje, że przyjmuje leki
        if (TextUtils.isEmpty(nazwaLek)) {
            showErrorSnackBar(resources.getString(R.string.err_msg_enter_doctor_name), true)
            return false
        }

        if (TextUtils.isEmpty(godzina)) {
            showErrorSnackBar(resources.getString(R.string.err_msg_enter_time), true)
            return false
        }

        return true
    }


    private fun registerUser() {
        if (validateRegisterDetails()) {
            openActivity()
        }
    }

    // Metoda do otwierania drugiej aktywności
    private fun openActivity(){
        val intent = Intent(this, MainActivityRegistration2::class.java)
        startActivity(intent)
    }
}