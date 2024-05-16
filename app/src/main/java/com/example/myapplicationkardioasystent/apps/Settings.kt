package com.example.myapplicationkardioasystent.apps

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
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
/**
 * Aktywność ustawień aplikacji.
 * Pozwala użytkownikowi na zmianę ustawień, takich jak przyjmowane leki i godziny pomiarów.
 */
class Settings : BaseActivity() {
    private lateinit var editSettingsTakNieSwitch: Switch
    private lateinit var editNameSettingsInput: EditText
    private lateinit var editHourSettingsInput: EditText
    private lateinit var saveChangesButton: Button
    private lateinit var editMorningInput: EditText
    private lateinit var editAfternoonInput: EditText
    private lateinit var editNightInput: EditText
    private lateinit var deleteAccountButton: Button
    private lateinit var settingsTakNieText: TextView

    val db = FirebaseFirestore.getInstance()
    private val dbOperations = FirestoreDatabaseOperations(db)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        val uID = intent.getStringExtra("uID")

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

        saveChangesButton.setOnClickListener {
            val editSettingsTakNieSwitchValue = editSettingsTakNieSwitch.isChecked
            val editNameSettingsInputValue = editNameSettingsInput.text.toString()
            val editHourSettingsInputValue = editHourSettingsInput.text.toString()
            val editMorningInputValue = editMorningInput.text.toString()
            val editAfternoonInputValue = editAfternoonInput.text.toString()
            val editNightInputValue = editNightInput.text.toString()

            val uID = intent.getStringExtra("uID")

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
                                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                                if (editSettingsTakNieSwitchValue && editNameSettingsInputValue.isNotEmpty() && editHourSettingsInputValue.isNotEmpty()
                                    && editMorningInputValue.isNotEmpty() && editAfternoonInputValue.isNotEmpty() && editNightInputValue.isNotEmpty()) {
                                    openActivity(
                                        editSettingsTakNieSwitchValue.toString(),
                                        editNameSettingsInputValue,
                                        editHourSettingsInputValue,
                                        editMorningInputValue,
                                        editAfternoonInputValue,
                                        editNightInputValue,
                                    )
                                }
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(
                                    this,
                                    "Failed to save changes: ${exception.message}",
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
     * Waliduje wprowadzone szczegóły dotyczące leków.
     * Sprawdza, czy nazwa leku i czas przyjmowania zostały wprowadzone.
     * @return true jeśli dane są poprawne, w przeciwnym razie false.
     */
    private fun validateDrugsDetails(): Boolean {
        val question = editSettingsTakNieSwitch.isChecked.toString()
        val drugsName = editNameSettingsInput.text.toString().trim()
        val timeOfTakingMedication = editHourSettingsInput.text.toString().trim()

        if (question.equals("true", ignoreCase = true)) {
            if (TextUtils.isEmpty(drugsName)) {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_doctor_name), true)
                return false
            }
            if (TextUtils.isEmpty(timeOfTakingMedication)) {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_time), true)
                return false
            }
            return true
        }

        if (question.equals("Nie", ignoreCase = true)) {
            editHourSettingsInput.isEnabled = false
            editNameSettingsInput.isEnabled = false
            return true
        }
        return true
    }
    /**
     * Waliduje wprowadzone szczegóły dotyczące godzin pomiarów.
     * Sprawdza poprawność formatu godzin.
     * @return true jeśli dane są poprawne, w przeciwnym razie false.
     */

    private fun validateTimeDetails(): Boolean {
        val morningTime = editMorningInput.text.toString().trim()
        val afternoonTime = editAfternoonInput.text.toString().trim()
        val nightTime = editNightInput.text.toString().trim()

        val timeRegex = Regex("^$|^([01]?[0-9]|2[0-3]):[0-5][0-9]$")

        if (!morningTime.matches(timeRegex)) {
            if (morningTime.isNotEmpty()) {
                showErrorSnackBar(resources.getString(R.string.err_msg_invalid_morning_time), true)
                return false
            }
        }

        if (!afternoonTime.matches(timeRegex)) {
            if (afternoonTime.isNotEmpty()) {
                showErrorSnackBar(resources.getString(R.string.err_msg_invalid_afternoon_time), true)
                return false
            }
        }

        if (!nightTime.matches(timeRegex)) {
            if (nightTime.isNotEmpty()) {
                showErrorSnackBar(resources.getString(R.string.err_msg_invalid_night_time), true)
                return false
            }
        }

        return true
    }

    private fun saveChanges() {
        val question = editSettingsTakNieSwitch.isChecked.toString()
        val drugsName = editNameSettingsInput.text.toString().trim()
        val timeOfTakingMedication = editHourSettingsInput.text.toString().trim()
        val morningMeasurement = editMorningInput.text.toString().trim()
        val middayMeasurement = editAfternoonInput.text.toString().trim()
        val eveningMeasurement = editNightInput.text.toString().trim()

        if (validateDrugsDetails() && validateTimeDetails()) {
            openActivity(question, drugsName, timeOfTakingMedication, morningMeasurement, middayMeasurement, eveningMeasurement)
        }
    }

    private fun openActivity(question: String, drugsName: String, timeOfTakingMedication: String, morningMeasurement: String, middayMeasurement: String, eveningMeasurement: String) {
        val intent = Intent(this, MainViewApp::class.java)
        intent.putExtra("question", question)
        intent.putExtra("drugsName", drugsName)
        intent.putExtra("timeOfTakingMedication", timeOfTakingMedication)
        intent.putExtra("morningMeasurement", morningMeasurement)
        intent.putExtra("middayMeasurement", middayMeasurement)
        intent.putExtra("eveningMeasurement", eveningMeasurement)
        startActivity(intent)
    }

    private fun setData(){
        val userId = FirebaseAuth.getInstance().currentUser!!.email
        val ref = db.collection("users").document(userId.toString())
        ref.get().addOnSuccessListener { document ->
            if(document != null && document.exists()) {
                val drugsName = document.getString("drugsName")
                val eveningMeasurement = document.getString("eveningMeasurement")
                val middayMeasurement = document.getString("middayMeasurement")
                val morningMeasurement = document.getString("morningMeasurement")
                val question = document.getBoolean("question")?.let { if (it) "Tak" else "Nie" }
                val timeOfTakingMedication = document.getString("timeOfTakingMedication")

                settingsTakNieText.text = question

                editNameSettingsInput.setText(drugsName)
                editHourSettingsInput.setText(timeOfTakingMedication)
                editAfternoonInput.setText(middayMeasurement)
                editNightInput.setText(eveningMeasurement)
                editMorningInput.setText(morningMeasurement)
                editSettingsTakNieSwitch.isChecked = question.equals("Tak", ignoreCase = true)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
        }
    }
}
