package com.example.myapplicationkardioasystent.apps

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
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
            }, hour, minute, true)
        timePickerDialog.show()
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
                editHourSettingsInput.text = timeOfTakingMedication
                editAfternoonInput.text = middayMeasurement
                editNightInput.text = eveningMeasurement
                editMorningInput.text = morningMeasurement
                editSettingsTakNieSwitch.isChecked = question.equals("Tak", ignoreCase = true)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
        }
    }
}
