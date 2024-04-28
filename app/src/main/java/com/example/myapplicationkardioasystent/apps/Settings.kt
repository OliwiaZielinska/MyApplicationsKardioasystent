package com.example.myapplicationkardioasystent.apps

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.myapplicationkardioasystent.R
import com.example.myapplicationkardioasystent.cloudFirestore.FirestoreDatabaseOperations
import com.example.myapplicationkardioasystent.cloudFirestore.User
import com.example.myapplicationkardioasystent.registation.BaseActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


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
    // Referencja do obiektu FirebaseFirestore do interakcji z bazą danych Firestore
    val db = FirebaseFirestore.getInstance()

    // Obiekt do obsługi operacji na bazie danych Firestore
    private val dbOperations = FirestoreDatabaseOperations(db)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
        // Pobranie uID z intentu
        val uID = intent.getStringExtra("uID")
        // Inicjalizacja elementów interfejsu użytkownika
        editSettingsTakNieTextInput = findViewById(R.id.editSettingsTakNieTextInput)
        editNameSettingsInput = findViewById(R.id.editNameSettingsInput)
        editHourSettingsInput = findViewById(R.id.editHourSettingsInput)
        saveChangesButton = findViewById(R.id.saveChangesButton)
        editMorningInput = findViewById(R.id.editMorningInput)
        editAfternoonInput = findViewById(R.id.editAfternoonInput)
        editNightInput = findViewById(R.id.editNightInput)
        // Ustawienie danych użytkownika na widoku
        setData()
        saveChangesButton.setOnClickListener {
            // Pobranie wartości z pól EditText
            val editSettingsTakNieTextInputValue = editSettingsTakNieTextInput.text.toString()
            val editNameSettingsInputValue = editNameSettingsInput.text.toString()
            val editHourSettingsInputValue = editHourSettingsInput.text.toString()
            val editMorningInputValue = editMorningInput.text.toString()
            val editAfternoonInputValue = editAfternoonInput.text.toString()
            val editNightInputValue = editNightInput.text.toString()

            // Pobranie uID z intentu
            val uID = intent.getStringExtra("uID")

            // Pobranie aktualnego obiektu użytkownika z Firestore
            val userId = FirebaseAuth.getInstance().currentUser!!.email
            db.collection("users").document(userId.toString()).get()
                .addOnSuccessListener { document ->
                    document.toObject(User::class.java)?.let { currentUser ->
                        // Aktualizacja tylko tych pól, które faktycznie się zmieniły
                        currentUser.question = editSettingsTakNieTextInputValue
                        currentUser.drugsName = editNameSettingsInputValue
                        currentUser.timeOfTakingMedication = editHourSettingsInputValue
                        // Jeśli pole jest puste, zachowaj istniejącą wartość
                        if (editMorningInputValue.isNotEmpty()) {
                            currentUser.morningMeasurement = editMorningInputValue
                        }
                        if (editAfternoonInputValue.isNotEmpty()) {
                            currentUser.middayMeasurement = editAfternoonInputValue
                        }
                        if (editNightInputValue.isNotEmpty()) {
                            currentUser.eveningMeasurement = editNightInputValue
                        }

                        // Zapisanie zaktualizowanego obiektu użytkownika w Firestore
                        db.collection("users").document(userId.toString()).set(currentUser)
                            .addOnSuccessListener {
                                // Wyświetlenie komunikatu o sukcesie
                                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                                // Jeśli wszystkie pola są wypełnione, otwarcie nowej aktywności
                                if (editSettingsTakNieTextInputValue.isNotEmpty() && editNameSettingsInputValue.isNotEmpty() && editHourSettingsInputValue.isNotEmpty()) {
                                    openActivity(
                                        editSettingsTakNieTextInputValue,
                                        editNameSettingsInputValue,
                                        editHourSettingsInputValue
                                    )
                                }
                            }
                            .addOnFailureListener { exception ->
                                // Obsługa błędów podczas zapisywania danych
                                Toast.makeText(
                                    this,
                                    "Failed to save changes: ${exception.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
        }
    }

        /**
     * Metoda do walidacji zmienianych danych dotyczących przyjmowanych leków.
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
    /**
     * Metoda do ustawienia danych użytkownika na widoku.
     */
    private fun setData(){
        // Pobranie danych użytkownika z Firestore
        val userId = FirebaseAuth.getInstance().currentUser!!.email
        val ref = db.collection("users").document(userId.toString())
        ref.get().addOnSuccessListener {
            if(it != null) {
                val drugsName = it.data?.get("drugsName")?.toString()
                val eveningMeasurement = it.data?.get("eveningMeasurment")?.toString()
                val middayMeasurement = it.data?.get("middayMeasurment")?.toString()
                val morningMeasurement = it.data?.get("morningMeasurment")?.toString()
                val question = it.data?.get("question")?.toString()
                val timeOfTakingMedication = it.data?.get("timeOfTakingMedication")?.toString()

                editNameSettingsInput.setText(drugsName)
                editHourSettingsInput.setText(timeOfTakingMedication)
                editAfternoonInput.setText(middayMeasurement)
                editNightInput.setText(eveningMeasurement)
                editMorningInput.setText(morningMeasurement)
                editSettingsTakNieTextInput.setText(question)

            }
        }
            .addOnFailureListener {
                Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
            }



    }

}