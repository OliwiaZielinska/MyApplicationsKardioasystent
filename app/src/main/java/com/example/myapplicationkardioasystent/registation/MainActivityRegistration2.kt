package com.example.myapplicationkardioasystent.registation

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import com.example.myapplicationkardioasystent.R
import com.example.myapplicationkardioasystent.cloudFirestore.FirestoreDatabaseOperations
import com.example.myapplicationkardioasystent.cloudFirestore.User
import com.example.myapplicationkardioasystent.login.ActivityMainLogin
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Klasa reprezentująca drugi etap rejestracji użytkownika.
 * Zawiera ona formularz do wprowadzenia danych użytkownika oraz logikę rejestracji w Firebase Authentication.
 */
class MainActivityRegistration2 : BaseActivity() {
    // Referencja do obiektu FirebaseFirestore do interakcji z bazą danych Firestore
    private val db = Firebase.firestore

    // Obiekt do obsługi operacji na bazie danych Firestore
    private val dbOperations = FirestoreDatabaseOperations(db)

    private lateinit var emailInputRegistration: EditText
    private lateinit var inputPasswordRegistration: EditText
    private lateinit var morningRegistrationInput: EditText
    private lateinit var middayRegistrationInput: EditText
    private lateinit var eveningRegistrationInput: EditText
    private lateinit var creatingAccountButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_registration2)

        // Odczytaj parametry z Intent
        val name = intent.getStringExtra("name")
        val surname = intent.getStringExtra("surname")
        val sex = intent.getStringExtra("sex")
        val age = intent.getStringExtra("age")
        val question = intent.getStringExtra("question")
        val drugsName = intent.getStringExtra("drugsName")
        val timeOfTakingMedication = intent.getStringExtra("timeOfTakingMedication")

        // Inicjalizacja elementów interfejsu użytkownika
        emailInputRegistration = findViewById(R.id.emailInputRegistration)
        inputPasswordRegistration = findViewById(R.id.InputPasswordRegistration)
        morningRegistrationInput = findViewById(R.id.morningRegistrationInput)
        middayRegistrationInput = findViewById(R.id.middayRegistrationInput)
        eveningRegistrationInput = findViewById(R.id.eveningRegistrationInput)
        creatingAccountButton = findViewById(R.id.creatingAccountButton)

        // Obsługa kliknięcia przycisku "Zarejestruj się"
        creatingAccountButton.setOnClickListener {
            registerUser(
                name.toString(),
                surname.toString(),
                sex.toString(),
                age.toString(),
                question.toString(),
                drugsName.toString(),
                timeOfTakingMedication.toString()
            )
        }
    }

    /**
     * Walidacja wprowadzonych danych rejestracji.
     * @return True, jeśli wszystkie pola zostały wypełnione poprawnie, w przeciwnym razie False.
     */
    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(emailInputRegistration.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(inputPasswordRegistration.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            TextUtils.isEmpty(morningRegistrationInput.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_time), true)
                false
            }

            TextUtils.isEmpty(middayRegistrationInput.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_time), true)
                false
            }

            TextUtils.isEmpty(eveningRegistrationInput.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_time), true)
                false
            }

            else -> true
        }
    }

    /**
     * Rejestracja użytkownika w Firebase Authentication.
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun registerUser(
        name: String,
        surname: String,
        sex: String,
        age: String,
        question: String,
        drugsName: String,
        timeOfTakingMedication: String
    ) {
        if (validateRegisterDetails()) {
            val login: String = emailInputRegistration.text.toString().trim { it <= ' ' }
            val password: String = inputPasswordRegistration.text.toString().trim { it <= ' ' }

            // Utworzenie użytkownika w FirebaseAuth
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(login, password)
                .addOnCompleteListener { task ->
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
            val morningMeasurement = morningRegistrationInput.text.toString()
            val middayMeasurement = middayRegistrationInput.text.toString()
            val eveningMeasurement = eveningRegistrationInput.text.toString()
            val user = User(
                name,
                surname,
                sex,
                age,
                question,
                drugsName,
                timeOfTakingMedication,
                login,
                password,
                morningMeasurement,
                middayMeasurement,
                eveningMeasurement
            )
            // Uruchomienie korutyny w wątku głównym
            GlobalScope.launch(Dispatchers.Main) {
                // Dodanie studenta do bazy danych Firestore userId=login
                dbOperations.addUser(login, user)
            }
        }
    }
}