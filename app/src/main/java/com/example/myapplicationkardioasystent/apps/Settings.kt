package com.example.myapplicationkardioasystent.apps

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import com.example.myapplicationkardioasystent.R
import com.example.myapplicationkardioasystent.registation.BaseActivity


/**
 * Aktywność odpowiedzialna za zmianę ustawień dotyczących przyjmowanych leków przez użytkownika aplikacji
 */
class Settings : BaseActivity() {
    private lateinit var editSettingsTakNieTextInput:EditText
    private lateinit var editNameSettingsInput:EditText
    private lateinit var editHourSettingsInput:EditText
    private lateinit var saveChangesButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        // Inicjalizacja elementów interfejsu użytkownika
        editSettingsTakNieTextInput = findViewById(R.id.editSettingsTakNieTextInput)
        editNameSettingsInput = findViewById(R.id.editNameSettingsInput)
        editHourSettingsInput = findViewById(R.id.editHourSettingsInput)
        saveChangesButton = findViewById(R.id.saveChangesButton)

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


        if (TextUtils.isEmpty(question)) {
            showErrorSnackBar(resources.getString(R.string.err_msg_enter_question), true)
            return false
        }

        if (question.equals("Tak", ignoreCase = true)) {
            showErrorSnackBar(resources.getString(R.string.err_msg_enter_doctor_name), true)
            return false
        }

        if (question.equals("Nie", ignoreCase = true)) {
            // Jeśli użytkownik nie przyjmuje leków, wyłącz możliwość edycji pól godziny i nazwy leku
            editHourSettingsInput.isEnabled = false
            editNameSettingsInput.isEnabled = false

            // Bezpośrednio zwracamy true, ponieważ nie ma potrzeby walidacji pól godziny i nazwy leku
            return true
        }

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

    /**
     * Metoda wywoływana po poprawnej walidacjid danych dotyczących przyjmowanych leków
     * Po zapisaniu edytowanych danych umozliwia powrót do okna głównego aplikacji
     */
    private fun saveChanges() {
        val question = editSettingsTakNieTextInput.text.toString().trim()
        val drugsName = editNameSettingsInput.text.toString().trim()
        val timeOfTakingMedication = editHourSettingsInput.text.toString().trim()

        if (validateDrugsDetails()) {
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