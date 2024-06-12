package com.example.myapplicationkardioasystent.registation

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplicationkardioasystent.R
import com.example.myapplicationkardioasystent.apps.NotificationWorker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivityRegistration1 : BaseActivity() {
    private lateinit var nameInput: EditText
    private lateinit var surnameInput: EditText
    private lateinit var sexSpinner: Spinner
    private lateinit var yearOfBirthInput: EditText
    private lateinit var medicationQuestionsSwitch: Switch
    private lateinit var medicationNamesInput: EditText
    private lateinit var timeOfTakingMedicineInput: TextView
    private lateinit var signInButtonRegistration: Button
    private lateinit var setTakNieText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_registration1)

        nameInput = findViewById(R.id.nameInput)
        surnameInput = findViewById(R.id.surnameInput)
        sexSpinner = findViewById(R.id.sexSpinner)
        yearOfBirthInput = findViewById(R.id.yearOfBirthInput)
        medicationQuestionsSwitch = findViewById(R.id.medicationQuestionsSwitch)
        medicationNamesInput = findViewById(R.id.medicationNamesInput)
        timeOfTakingMedicineInput = findViewById(R.id.timeOfTakingMedicineInput)
        signInButtonRegistration = findViewById(R.id.signInButtonRegistration)
        setTakNieText = findViewById(R.id.setTakNieText)

        val genderAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.gender_array,
            android.R.layout.simple_spinner_item
        )
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sexSpinner.adapter = genderAdapter

        signInButtonRegistration.setOnClickListener {
            registerUser()
        }

        medicationQuestionsSwitch.setOnCheckedChangeListener { _, isChecked ->
            setTakNieText.text = if (isChecked) "Tak" else "Nie"
            medicationNamesInput.isEnabled = isChecked
            timeOfTakingMedicineInput.isEnabled = isChecked
        }

        timeOfTakingMedicineInput.setOnClickListener {
            showTimePickerDialog()
        }
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                timeOfTakingMedicineInput.text = time
            }, hour, minute, true
        )
        timePickerDialog.show()
    }

    private fun validateRegisterDetails(): Boolean {
        val name = nameInput.text.toString().trim()
        val surname = surnameInput.text.toString().trim()
        val sexString = sexSpinner.selectedItem.toString()
        val yearOfBirth = yearOfBirthInput.text.toString().trim()
        val question = medicationQuestionsSwitch.isChecked
        val drugsName = medicationNamesInput.text.toString().trim()
        val timeOfTakingMedication = timeOfTakingMedicineInput.text.toString().trim()

        if (TextUtils.isEmpty(name)) {
            showErrorSnackBar(getString(R.string.err_msg_enter_name), true)
            return false
        }

        if (TextUtils.isEmpty(surname)) {
            showErrorSnackBar(getString(R.string.err_msg_enter_surname), true)
            return false
        }

        if (TextUtils.isEmpty(sexString)) {
            showErrorSnackBar(getString(R.string.err_msg_enter_gender), true)
            return false
        }

        val sex = when (sexString) {
            "Mężczyzna" -> Gender.MALE
            "Kobieta" -> Gender.FEMALE
            else -> Gender.OTHER
        }

        if (TextUtils.isEmpty(yearOfBirth)) {
            showErrorSnackBar(getString(R.string.err_msg_enter_year_of_birth), true)
            return false
        }

        if (setTakNieText.text == "Tak") {
            scheduleMedicationNotification(timeOfTakingMedication, "Przypomnienie o przyjęciu leku $drugsName o godzinie $timeOfTakingMedication")
        }

        if (question) {
            val drugsName = medicationNamesInput.text.toString().trim()
            val timeOfTakingMedication = timeOfTakingMedicineInput.text.toString().trim()

            if (TextUtils.isEmpty(drugsName)) {
                showErrorSnackBar(getString(R.string.err_msg_enter_doctor_name), true)
                return false
            }

            if (TextUtils.isEmpty(timeOfTakingMedication)) {
                showErrorSnackBar(getString(R.string.err_msg_enter_time), true)
                return false
            }
        }

        return true
    }

    private fun scheduleMedicationNotification(timeOfTakingMedication: String, message: String) {
        val delay = calculateInitialDelay(timeOfTakingMedication)
        if (delay > 0) {
            val data = Data.Builder().putString("message", message).build()
            val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build()
            WorkManager.getInstance(this).enqueue(notificationWork)
        }
    }

    private fun calculateInitialDelay(time: String): Long {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val targetTime = Calendar.getInstance().apply {
            val timeParts = time.split(":").map { it.toInt() }
            set(Calendar.HOUR_OF_DAY, timeParts[0])
            set(Calendar.MINUTE, timeParts[1])
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val now = Calendar.getInstance().timeInMillis
        return if (targetTime > now) {
            targetTime - now
        } else {
            targetTime + TimeUnit.DAYS.toMillis(1) - now
        }
    }

    private fun registerUser() {
        if (validateRegisterDetails()) {
            val name = nameInput.text.toString().trim()
            val surname = surnameInput.text.toString().trim()
            val sexString = sexSpinner.selectedItem.toString()
            val yearOfBirth = yearOfBirthInput.text.toString().trim()
            val question = medicationQuestionsSwitch.isChecked
            val drugsName = medicationNamesInput.text.toString().trim()
            val timeOfTakingMedication = timeOfTakingMedicineInput.text.toString().trim()
            val sex = when (sexString) {
                "Mężczyzna" -> Gender.MALE
                "Kobieta" -> Gender.FEMALE
                else -> Gender.OTHER
            }
            openActivity(
                name,
                surname,
                sex,
                yearOfBirth,
                question,
                drugsName,
                timeOfTakingMedication
            )
        }
    }

    private fun openActivity(
        name: String,
        surname: String,
        sex: Gender,
        yearOfBirth: String,
        question: Boolean,
        drugsName: String,
        timeOfTakingMedication: String
    ) {
        val intent = Intent(this, MainActivityRegistration2::class.java)
        intent.putExtra("name", name)
        intent.putExtra("surname", surname)
        intent.putExtra("sex", sex.toString())
        intent.putExtra("yearOfBirth", yearOfBirth)
        intent.putExtra("question", if (question) "Tak" else "Nie")
        intent.putExtra("drugsName", drugsName)
        intent.putExtra("timeOfTakingMedication", timeOfTakingMedication)
        startActivity(intent)
    }
}
