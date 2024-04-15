package com.example.myapplicationkardioasystent.Registation

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.myapplicationkardioasystent.Login.ActivityMainLogin
import com.example.myapplicationkardioasystent.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class MainActivityRegistration2 : BaseActivity() {

    private lateinit var nickInputRejestracja: EditText
    private lateinit var hasloInputRejestracja: EditText
    private lateinit var RanoRejestracjaInput: EditText
    private lateinit var PoludnieRejestracja: EditText
    private lateinit var WieczorRejestracja: EditText
    private lateinit var zalozKontroButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_registration2)

        // Inicjalizacja elementów interfejsu użytkownika
        nickInputRejestracja = findViewById(R.id.emailInputRejestracja)
        hasloInputRejestracja = findViewById(R.id.hasloInputRejestracja)
        RanoRejestracjaInput = findViewById(R.id.ranoRejestracjaInput)
        PoludnieRejestracja = findViewById(R.id.PoludnieRejestracja)
        WieczorRejestracja = findViewById(R.id.WieczorRejestracja)
        zalozKontroButton = findViewById(R.id.zalozKontoButton)

        // Obsługa kliknięcia przycisku "Zarejestruj się"
        zalozKontroButton.setOnClickListener {
            registerUser();
        }
    }

    // Walidacja danych rejestracji
    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(nickInputRejestracja?.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(hasloInputRejestracja?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            TextUtils.isEmpty(RanoRejestracjaInput?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_morning_pulse), true)
                false
            }
            TextUtils.isEmpty(PoludnieRejestracja?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_midday_pulse), true)
                false
            }
            TextUtils.isEmpty(WieczorRejestracja?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_evening_pulse), true)
                false
            }
            else -> true
        }
    }

    // Rejestracja użytkownika
    private fun registerUser() {
        if (validateRegisterDetails()) {
            val login: String = nickInputRejestracja?.text.toString().trim() {it <= ' '}
            val password: String = hasloInputRejestracja?.text.toString().trim() {it <= ' '}

            // Utworzenie użytkownika w FirebaseAuth
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(login, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->
                        if (task.isSuccessful) {
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            showErrorSnackBar("You are registered successfully. Your user id is ${firebaseUser.uid}", false)

                            // Wylogowanie użytkownika i zakończenie aktywności
                            FirebaseAuth.getInstance().signOut()
                            finish()
                            val intent = Intent(this, ActivityMainLogin::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    }
                )
        }
    }
}