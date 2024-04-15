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
        return when {
            TextUtils.isEmpty(imieInput?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_name), true)
                false
            }
            TextUtils.isEmpty(nazwiskoInput?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_surname), true)
                false
            }
            TextUtils.isEmpty(plecInput?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_gender), true)
                false
            }
            TextUtils.isEmpty(wiekInput?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_age), true)
                false
            }
            TextUtils.isEmpty(pytInput?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_question), true)
                false
            }
            TextUtils.isEmpty(nazwaLekInput?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_doctor_name), true)
                false
            }
            TextUtils.isEmpty(godzinaInput?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_time), true)
                false
            }
            else -> true
        }
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