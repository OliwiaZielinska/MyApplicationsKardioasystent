package com.example.myapplicationkardioasystent.apps

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import com.example.myapplicationkardioasystent.R
import com.example.myapplicationkardioasystent.registation.BaseActivity


/**
 * Aktywność odpowiedzialna za zmianę ustawień dotyczących przyjmowanych leków i godzin wykonywania pomiarów.
 */
class Settings : BaseActivity() {
    private lateinit var editSettingsTakNieTextInput:EditText
    private lateinit var editNameSettingsInput:EditText
    private lateinit var editHourSettingsInput:EditText
    private lateinit var saveChangesButton: Button
    private lateinit var editMorningInput: EditText
    private lateinit var editAfternoonInput: EditText
    private lateinit var editNightInput: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        // Inicjalizacja elementów interfejsu użytkownika
        editSettingsTakNieTextInput = findViewById(R.id.editSettingsTakNieTextInput)
        editNameSettingsInput = findViewById(R.id.editNameSettingsInput)
        editHourSettingsInput = findViewById(R.id.editHourSettingsInput)
        saveChangesButton = findViewById(R.id.saveChangesButton)
        editMorningInput = findViewById(R.id.editMorningInput)
        editAfternoonInput = findViewById(R.id. editAfternoonInput)
        editNightInput = findViewById(R.id.editNightInput)

        // Obsługa kliknięcia przycisku "Zapisz zmiany"
        saveChangesButton.setOnClickListener {
            saveChanges()
        }
    }

    /**
     * Walidacja zmienianych danych dotyczących przyjmowanych leków
     * @return True, jeśli wszystkie pola zostały prawidłowo wypełnione, w przeciwnym razie zwraca False.
     */

    private fun validateDrugsDetails(): Boolean {
        val question = editSettingsTakNieTextInput.text.toString().trim()
        val drugsName = editNameSettingsInput.text.toString().trim()
        val timeOfTakingMedication = editHourSettingsInput.text.toString().trim()




        if (question.equals("Tak", ignoreCase = true)) {
            // Sprawdzamy tylko wtedy, gdy użytkownik deklaruje, że przyjmuje leki
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
            // Jeśli użytkownik nie przyjmuje leków, wyłącz możliwość edycji pól godziny i nazwy leku
            editHourSettingsInput.isEnabled = false
            editNameSettingsInput.isEnabled = false

            // Bezpośrednio zwracamy true, ponieważ nie ma potrzeby walidacji pól godziny i nazwy leku
            return true
        }


        return true
    }
    /**
     * Walidacja edytowanych godzin wykonywania pomiarów.
     * @return True, jeśli wszystkie pola zostały wypełnione poprawnie lub są puste, w przeciwnym razie False.
     */
    private fun validateTimeDetails(): Boolean {
        // Pobranie wartości wprowadzonych przez użytkownika
        val morningTime = editMorningInput.text.toString().trim()
        val afternoonTime = editAfternoonInput.text.toString().trim()
        val nightTime = editNightInput.text.toString().trim()

        // Tworzenie wyrażenia regularnego do sprawdzenia formatu godziny (np. "10:00")
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
    /**
     * Metoda wywoływana po poprawnej walidacjid danych dotyczących przyjmowanych leków
     * Po zapisaniu edytowanych danych umozliwia powrót do okna głównego aplikacji
     */
    private fun saveChanges() {
        val question = editSettingsTakNieTextInput.text.toString().trim()
        val drugsName = editNameSettingsInput.text.toString().trim()
        val timeOfTakingMedication = editHourSettingsInput.text.toString().trim()

        if (validateDrugsDetails()&& validateTimeDetails()) {
            openActivity(question, drugsName, timeOfTakingMedication)
        }
    }

    /**
     * Metoda do otwarcia aktywności głównej aplikacji i przekazania zmian.
     */
    private fun openActivity(question: String, drugsName: String, timeOfTakingMedication: String) {
        // Tworzenie nowego intentu
        val intent = Intent(this, MainViewApp::class.java)

        // Przekazanie informacji o zmianach za pomocą dodatków
        intent.putExtra("question", question)
        intent.putExtra("drugsName", drugsName)
        intent.putExtra("timeOfTakingMedication", timeOfTakingMedication)

        // Rozpoczęcie nowej aktywności
        startActivity(intent)
    }


}