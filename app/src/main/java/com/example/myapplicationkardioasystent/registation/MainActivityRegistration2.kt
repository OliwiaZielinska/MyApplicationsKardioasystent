package com.example.myapplicationkardioasystent.registation

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplicationkardioasystent.R
import com.example.myapplicationkardioasystent.apps.NotificationWorker
import com.example.myapplicationkardioasystent.cloudFirestore.FirestoreDatabaseOperations
import com.example.myapplicationkardioasystent.cloudFirestore.User
import com.example.myapplicationkardioasystent.login.ActivityMainLogin
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Aktywność odpowiedzialna za drugi etap rejestracji użytkownika oraz zarządzanie danymi użytkownika.
 */
class MainActivityRegistration2 : BaseActivity() {
    private val db = Firebase.firestore
    private lateinit var emailInputRegistration: EditText
    private lateinit var inputPasswordRegistration: EditText
    private lateinit var morningRegistrationInput: TextView
    private lateinit var middayRegistrationInput: TextView
    private lateinit var eveningRegistrationInput: TextView
    private lateinit var creatingAccountButton: Button
    private lateinit var dbOperations: FirestoreDatabaseOperations // Deklaracja dbOperations

    /**
     * Metoda wywoływana przy tworzeniu aktywności.
     *
     * @param savedInstanceState Zapisany stan instancji aktywności.
     */
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

        morningRegistrationInput.setOnClickListener { showTimePickerDialog(morningRegistrationInput) }
        middayRegistrationInput.setOnClickListener { showTimePickerDialog(middayRegistrationInput) }
        eveningRegistrationInput.setOnClickListener { showTimePickerDialog(eveningRegistrationInput) }

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

    /**
     * Metoda wyświetlająca TimePickerDialog i ustawiająca wybrany czas w TextView.
     *
     * @param textView TextView, który ma być zaktualizowany.
     */
    private fun showTimePickerDialog(textView: TextView) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            textView.text = formattedTime
        }, hour, minute, true)

        timePickerDialog.show()
    }

    /**
     * Metoda walidująca wprowadzone dane rejestracyjne.
     *
     * @return True, jeśli dane są poprawne; w przeciwnym razie false.
     */
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

    /**
     * Metoda rejestracji użytkownika.
     *
     * @param name Imię użytkownika.
     * @param surname Nazwisko użytkownika.
     * @param sex Płeć użytkownika.
     * @param yearOfBirth Rok urodzenia użytkownika.
     * @param question Czy użytkownik ma pytania o leki.
     * @param drugsName Nazwa leku.
     * @param timeOfTakingMedication Czas przyjęcia leku.
     */
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

            scheduleDailyNotification(morningMeasurement, "Czas wykonać pomiar ciśnienia krwi i pulsu - rano o godzinie $morningMeasurement.")
            scheduleDailyNotification(middayMeasurement, "Czas wykonać pomiar ciśnienia krwi i pulsu - południe o godzinie $middayMeasurement.")
            scheduleDailyNotification(eveningMeasurement, "Czas wykonać pomiar ciśnienia krwi i pulsu - wieczór o godzinie $eveningMeasurement.")
        }
    }

    /**
     * Metoda planująca jednorazowe powiadomienie.
     *
     * @param time Czas planowanego powiadomienia.
     * @param message Wiadomość powiadomienia.
     */
    private fun scheduleNotification(time: String, message: String) {
        val delay = calculateDelay(time)
        if (delay > 0) {
            val data = Data.Builder().putString("message", message).build()
            val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build()
            WorkManager.getInstance(this).enqueue(notificationWork)
        } else {
        }
    }

    /**
     * Metoda obliczająca opóźnienie dla planowanego powiadomienia.
     *
     * @param time Czas planowanego powiadomienia.
     * @return Opóźnienie w milisekundach.
     */
    private fun calculateDelay(time: String): Long {
        val timeParts = time.split(":").map { it.toInt() }
        val now = System.currentTimeMillis()
        val targetTime = Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, timeParts[0])
            set(Calendar.MINUTE, timeParts[1])
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (targetTime.timeInMillis <= now) {
            targetTime.add(Calendar.DAY_OF_MONTH, 1)
        }
        return targetTime.timeInMillis - now
    }

    /**
     * Metoda planująca codzienne powiadomienia.
     *
     * @param time Czas planowanych powiadomień.
     * @param message Wiadomość powiadomienia.
     */
    private fun scheduleDailyNotification(time: String, message: String) {
        val initialDelay = calculateDelay(time)
        val data = Data.Builder().putString("message", message).build()
        val dailyWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()
        WorkManager.getInstance(this).enqueue(dailyWorkRequest)
    }

    /**
     * Metoda haszująca hasło użytkownika.
     *
     * @param password Hasło do zahashowania.
     * @return Zahashowane hasło.
     */
    private fun hashPassword(password: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val hashedBytes = messageDigest.digest(password.toByteArray())
        return hashedBytes.joinToString("") { "%02x".format(it) }
    }
}
