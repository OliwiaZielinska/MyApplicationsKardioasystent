package com.example.myapplicationkardioasystent.apps

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.work.Data
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplicationkardioasystent.R
import com.example.myapplicationkardioasystent.cloudFirestore.FirestoreDatabaseOperations
import com.example.myapplicationkardioasystent.cloudFirestore.User
import com.example.myapplicationkardioasystent.login.MainActivity
import com.example.myapplicationkardioasystent.registation.BaseActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Aktywność obsługująca ustawienia użytkownika, w tym harmonogram powiadomień i inne preferencje.
 */
class Settings : BaseActivity() {
    private lateinit var editSettingsTakNieSwitch: Switch
    private lateinit var editNameSettingsInput: EditText
    private lateinit var editHourSettingsInput: TextView
    private lateinit var saveChangesButton: Button
    private lateinit var editMorningInput: TextView
    private lateinit var editAfternoonInput: TextView
    private lateinit var editNightInput: TextView
    private lateinit var deleteAccountButton: Button
    private lateinit var settingsTakNieText: TextView

    // Inicjalizacja instancji Firestore
    val db = FirebaseFirestore.getInstance()
    private val dbOperations = FirestoreDatabaseOperations(db)

    /**
     * Metoda wywoływana podczas tworzenia aktywności.
     * @param savedInstanceState Zapisany stan aktywności, jeśli jest dostępny.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        val uID = intent.getStringExtra("userID").toString()

        editSettingsTakNieSwitch = findViewById(R.id.editSettingsTakNieSwitch)
        editNameSettingsInput = findViewById(R.id.editNameSettingsInput)
        editHourSettingsInput = findViewById(R.id.editHourSettingsInput)
        saveChangesButton = findViewById(R.id.saveChangesButton)
        editMorningInput = findViewById(R.id.editMorningInput)
        editAfternoonInput = findViewById(R.id.editAfternoonInput)
        editNightInput = findViewById(R.id.editNightInput)
        deleteAccountButton = findViewById(R.id.deleteAccountButton)
        settingsTakNieText = findViewById(R.id.settingsTakNieText)

        setData()

        editHourSettingsInput.setOnClickListener { showTimePickerDialog(editHourSettingsInput) }
        editMorningInput.setOnClickListener { showTimePickerDialog(editMorningInput) }
        editAfternoonInput.setOnClickListener { showTimePickerDialog(editAfternoonInput) }
        editNightInput.setOnClickListener { showTimePickerDialog(editNightInput) }

        saveChangesButton.setOnClickListener {
            val editSettingsTakNieSwitchValue = editSettingsTakNieSwitch.isChecked
            val editNameSettingsInputValue = editNameSettingsInput.text.toString()
            val editHourSettingsInputValue = editHourSettingsInput.text.toString()
            val editMorningInputValue = editMorningInput.text.toString()
            val editAfternoonInputValue = editAfternoonInput.text.toString()
            val editNightInputValue = editNightInput.text.toString()

            val userId = FirebaseAuth.getInstance().currentUser!!.email
            db.collection("users").document(userId.toString()).get()
                .addOnSuccessListener { document ->
                    document.toObject(User::class.java)?.let { currentUser ->
                        currentUser.question = editSettingsTakNieSwitchValue
                        currentUser.drugsName = editNameSettingsInputValue
                        currentUser.timeOfTakingMedication = editHourSettingsInputValue
                        currentUser.morningMeasurement = editMorningInputValue
                        currentUser.middayMeasurement = editAfternoonInputValue
                        currentUser.eveningMeasurement = editNightInputValue

                        db.collection("users").document(userId.toString()).set(currentUser)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Zmiana zakończona sukcesem", Toast.LENGTH_SHORT).show()
                                if (editSettingsTakNieSwitchValue && editNameSettingsInputValue.isNotEmpty() && editHourSettingsInputValue.isNotEmpty()
                                    && editMorningInputValue.isNotEmpty() && editAfternoonInputValue.isNotEmpty() && editNightInputValue.isNotEmpty()
                                ) {
                                    openActivity(
                                        editSettingsTakNieSwitchValue.toString(),
                                        editNameSettingsInputValue,
                                        editHourSettingsInputValue,
                                        editMorningInputValue,
                                        editAfternoonInputValue,
                                        editNightInputValue,
                                        uID
                                    )

                                    // Anulowanie wszystkich istniejących powiadomień
                                    cancelAllNotifications()

                                    // Planowanie nowych powiadomień po zapisaniu zmian
                                    scheduleDailyNotification(
                                        editMorningInputValue,
                                        "Czas wykonać pomiar ciśnienia krwi i pulsu - rano o godzinie $editMorningInputValue."
                                    )
                                    scheduleDailyNotification(
                                        editAfternoonInputValue,
                                        "Czas wykonać pomiar ciśnienia krwi i pulsu - południe o godzinie $editAfternoonInputValue."
                                    )
                                    scheduleDailyNotification(
                                        editNightInputValue,
                                        "Czas wykonać pomiar ciśnienia krwi i pulsu - wieczór o godzinie $editNightInputValue."
                                    )
                                    scheduleDailyNotification(editHourSettingsInputValue,
                                        "Przypomnienie o przyjęciu leku $editNameSettingsInputValue o godzinie $editHourSettingsInputValue.")

                                }
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(
                                    this,
                                    "Błąd podczas zapisu zmian: ${exception.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
        }

        deleteAccountButton.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser!!.email
            GlobalScope.launch(Dispatchers.Main) {
                dbOperations.deleteUser(userId.toString())
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        editSettingsTakNieSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            settingsTakNieText.text = if (isChecked) "Tak" else "Nie"
        }
    }

    /**
     * Metoda wyświetlająca dialog wyboru czasu.
     * @param textView Pole tekstowe, które zostanie zaktualizowane wybraną wartością czasu.
     */
    private fun showTimePickerDialog(textView: TextView) {
        val currentTime = textView.text.toString()
        val hour: Int
        val minute: Int

        if (currentTime.isNotEmpty()) {
            val parts = currentTime.split(":")
            hour = parts[0].toInt()
            minute = parts[1].toInt()
        } else {
            hour = 0
            minute = 0
        }

        val timePickerDialog = TimePickerDialog(this,
            { _, selectedHour, selectedMinute ->
                textView.text = String.format("%02d:%02d", selectedHour, selectedMinute)
            }, hour, minute, true
        )
        timePickerDialog.show()
    }

    /**
     * Metoda otwierająca główną aktywność aplikacji z przekazanymi danymi.
     * @param question Czy użytkownik ma pytanie.
     * @param drugsName Nazwa leku.
     * @param timeOfTakingMedication Czas przyjmowania leku.
     * @param morningMeasurement Pomiar rano.
     * @param middayMeasurement Pomiar w południe.
     * @param eveningMeasurement Pomiar wieczorem.
     * @param userId Identyfikator użytkownika, który jest przekazywany do nowej aktywności.
     */
    private fun openActivity(
        question: String,
        drugsName: String,
        timeOfTakingMedication: String,
        morningMeasurement: String,
        middayMeasurement: String,
        eveningMeasurement: String,
        userId: String
    ) {
        val intent = Intent(this, MainViewApp::class.java)
        intent.putExtra("uID", userId)
        intent.putExtra("question", question)
        intent.putExtra("drugsName", drugsName)
        intent.putExtra("timeOfTakingMedication", timeOfTakingMedication)
        intent.putExtra("morningMeasurement", morningMeasurement)
        intent.putExtra("middayMeasurement", middayMeasurement)
        intent.putExtra("eveningMeasurement", eveningMeasurement)
        startActivity(intent)
    }

    /**
     * Metoda ustawiająca dane użytkownika na podstawie informacji z bazy danych Firestore.
     */
    private fun setData() {
        val userId = FirebaseAuth.getInstance().currentUser!!.email
        val ref = db.collection("users").document(userId.toString())
        ref.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val drugsName = document.getString("drugsName")
                val eveningMeasurement = document.getString("eveningMeasurement")
                val middayMeasurement = document.getString("middayMeasurement")
                val morningMeasurement = document.getString("morningMeasurement")
                val question = document.getBoolean("question")?.let { if (it) "Tak" else "Nie" }
                val timeOfTakingMedication = document.getString("timeOfTakingMedication")

                settingsTakNieText.text = question

                editNameSettingsInput.setText(drugsName)
                editHourSettingsInput.text = timeOfTakingMedication
                editAfternoonInput.text = middayMeasurement
                editNightInput.text = eveningMeasurement
                editMorningInput.text = morningMeasurement
                editSettingsTakNieSwitch.isChecked = question.equals("Tak", ignoreCase = true)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Wystąpił błąd!", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Metoda planująca codzienne powiadomienia.
     * @param time Godzina, o której mają zostać zaplanowane powiadomienia.
     * @param message Treść powiadomienia.
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
     * Metoda anulująca wszystkie zaplanowane powiadomienia.
     */
    private fun cancelAllNotifications() {
        WorkManager.getInstance(this).cancelAllWork()
    }

    /**
     * Metoda obliczająca opóźnienie dla harmonogramu powiadomień.
     * @param time Docelowa godzina powiadomienia.
     * @return Obliczone opóźnienie w milisekundach.
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
}