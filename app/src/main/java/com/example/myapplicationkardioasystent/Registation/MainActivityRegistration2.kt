package com.example.myapplicationkardioasystent.Registation

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import com.example.myapplicationkardioasystent.CloudFirestore.FirestoreDatabaseOperations
import com.example.myapplicationkardioasystent.CloudFirestore.User
import com.example.myapplicationkardioasystent.Login.ActivityMainLogin
import com.example.myapplicationkardioasystent.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Klasa reprezentująca drugi etap rejestracji użytkownika.
 * Zawiera ona formularz do wprowadzenia danych użytkownika oraz logikę rejestracji w Firebase Authentication.
 */
class MainActivityRegistration2 : BaseActivity() {
    // Referencja do obiektu FirebaseFirestore do interakcji z bazą danych Firestore
    val db = Firebase.firestore

    // Obiekt do obsługi operacji na bazie danych Firestore
    private val dbOperations = FirestoreDatabaseOperations(db)

    private lateinit var nickInputRejestracja: EditText
    private lateinit var hasloInputRejestracja: EditText
    private lateinit var RanoRejestracjaInput: EditText
    private lateinit var PoludnieRejestracja: EditText
    private lateinit var WieczorRejestracja: EditText
    private lateinit var zalozKontroButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_registration2)

        // Odczytaj parametry z Intent
        val imie = intent.getStringExtra("imie")
        val nazwisko = intent.getStringExtra("nazwisko")
        val plec = intent.getStringExtra("plec")
        val wiek = intent.getStringExtra("wiek")
        val pyt = intent.getStringExtra("pyt")
        val nazwaLek = intent.getStringExtra("nazwaLek")
        val godzina = intent.getStringExtra("godzina")

        // Inicjalizacja elementów interfejsu użytkownika
        nickInputRejestracja = findViewById(R.id.emailInputRejestracja)
        hasloInputRejestracja = findViewById(R.id.hasloInputRejestracja)
        RanoRejestracjaInput = findViewById(R.id.ranoRejestracjaInput)
        PoludnieRejestracja = findViewById(R.id.PoludnieRejestracja)
        WieczorRejestracja = findViewById(R.id.WieczorRejestracja)
        zalozKontroButton = findViewById(R.id.zalozKontoButton)

        // Obsługa kliknięcia przycisku "Zarejestruj się"
        zalozKontroButton.setOnClickListener {
            registerUser(
                imie.toString(),
                nazwisko.toString(),
                plec.toString(),
                wiek.toString(),
                pyt.toString(),
                nazwaLek.toString(),
                godzina.toString()
            );
        }
    }

    /**
     * Walidacja wprowadzonych danych rejestracji.
     * @return True, jeśli wszystkie pola zostały wypełnione poprawnie, w przeciwnym razie False.
     */
    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(nickInputRejestracja?.text.toString().trim { it <= ' ' }) -> {
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

    /**
     * Rejestracja użytkownika w Firebase Authentication.
     */
    private fun registerUser(
        imie: String,
        nazwisko: String,
        plec: String,
        wiek: String,
        pyt: String,
        nazwaLek: String,
        godzina: String
    ) {
        if (validateRegisterDetails()) {
            val login: String = nickInputRejestracja?.text.toString().trim() { it <= ' ' }
            val password: String = hasloInputRejestracja?.text.toString().trim() { it <= ' ' }

            // Utworzenie użytkownika w FirebaseAuth
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(login, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->
                        if (task.isSuccessful) {
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            showErrorSnackBar(
                                "You are registered successfully. Your user id is ${firebaseUser.uid}",
                                false
                            )

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
            val rano = RanoRejestracjaInput.text.toString()
            val poludnie = PoludnieRejestracja.text.toString()
            val wieczor = WieczorRejestracja.text.toString()
            val user = User(
                imie,
                nazwisko,
                plec,
                wiek,
                pyt,
                nazwaLek,
                godzina,
                login,
                password,
                rano,
                poludnie,
                wieczor
            )
            // Uruchomienie korutyny w wątku głównym
            GlobalScope.launch(Dispatchers.Main) {
                // Dodanie studenta do bazy danych Firestore
                dbOperations.addUser(login, user)
            }
        }
    }
}