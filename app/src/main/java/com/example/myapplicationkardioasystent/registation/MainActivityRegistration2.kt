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
import java.security.MessageDigest

/**
 * Klasa reprezentująca drugi etap rejestracji użytkownika.
 * Zawiera ona formularz do wprowadzenia danych użytkownika oraz logikę rejestracji w Firebase Authentication.
 */
class MainActivityRegistration2 : BaseActivity() {
    private val db = Firebase.firestore
    private lateinit var emailInputRegistration: EditText
    private lateinit var inputPasswordRegistration: EditText
    private lateinit var morningRegistrationInput: EditText
    private lateinit var middayRegistrationInput: EditText
    private lateinit var eveningRegistrationInput: EditText
    private lateinit var creatingAccountButton: Button
    private lateinit var dbOperations: FirestoreDatabaseOperations // Deklaracja dbOperations

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_registration2)

        val name = intent.getStringExtra("name")
        val surname = intent.getStringExtra("surname")
        val sexString = intent.getStringExtra("sex")
        val sex = when (sexString) {
            "MALE" -> Gender.MALE
            "FEMALE" -> Gender.FEMALE
            else -> Gender.OTHER
        }
        val yearOfBirth = intent.getStringExtra("yearOfBirth")
        val questionString = intent.getStringExtra("question")
        val question = questionString == "Tak"
        val drugsName = intent.getStringExtra("drugsName")
        val timeOfTakingMedication = intent.getStringExtra("timeOfTakingMedication")

        // Inicjalizacja dbOperations
        dbOperations = FirestoreDatabaseOperations(db)

        emailInputRegistration = findViewById(R.id.emailInputRegistration)
        inputPasswordRegistration = findViewById(R.id.InputPasswordRegistration)
        morningRegistrationInput = findViewById(R.id.morningRegistrationInput)
        middayRegistrationInput = findViewById(R.id.middayRegistrationInput)
        eveningRegistrationInput = findViewById(R.id.eveningRegistrationInput)
        creatingAccountButton = findViewById(R.id.creatingAccountButton)

        creatingAccountButton.setOnClickListener {
            registerUser(
                name.toString(),
                surname.toString(),
                sex,
                yearOfBirth.toString(),
                question,
                drugsName.toString(),
                timeOfTakingMedication.toString()
            )
        }
    }

    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(emailInputRegistration.text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(inputPasswordRegistration.text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            TextUtils.isEmpty(morningRegistrationInput.text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_time), true)
                false
            }

            TextUtils.isEmpty(middayRegistrationInput.text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_time), true)
                false
            }

            TextUtils.isEmpty(eveningRegistrationInput.text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_time), true)
                false
            }

            else -> true
        }
    }
    private fun registerUser(
        name: String,
        surname: String,
        sex: Gender,
        yearOfBirth: String,
        question: Boolean,
        drugsName: String,
        timeOfTakingMedication: String
    ) {
        if (validateRegisterDetails()) {
            val login: String = emailInputRegistration.text.toString().trim()
            val password: String = inputPasswordRegistration.text.toString().trim()
            val hashedPassword = hashPassword(password)

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(login, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        showErrorSnackBar(
                            "You are registered successfully. Your user id is ${firebaseUser.uid}",
                            false
                        )

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
                sex.toString(),
                yearOfBirth,
                question,
                drugsName,
                timeOfTakingMedication,
                login,
                hashedPassword,
                morningMeasurement,
                middayMeasurement,
                eveningMeasurement
            )
            GlobalScope.launch(Dispatchers.Main) {
                dbOperations.addUser(login, user) // Użycie dbOperations
            }
        }
    }


    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
