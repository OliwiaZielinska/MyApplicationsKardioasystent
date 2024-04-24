package com.example.myapplicationkardioasystent.registation

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import com.example.myapplicationkardioasystent.R

/**
 * Aktywność odpowiedzialna za rejestrację użytkownika do aplikacji - wprowadza dane użytownika oraz
 * przekierowuje do kolejnego etapu rejestracji
 */
class MainActivityRegistration1 : BaseActivity() {
    private lateinit var nameInput:EditText
    private lateinit var surnameInput:EditText
    private lateinit var sexInput:EditText
    private lateinit var ageInput:EditText
    private lateinit var medicationQuestionsInput:EditText
    private lateinit var medicationNamesInput:EditText
    private lateinit var timeOfTakingMedicineInput:EditText
    private lateinit var signInButtonRegistration: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_registration1)

        // Inicjalizacja elementów interfejsu użytkownika
        nameInput = findViewById(R.id.nameInput)
        surnameInput = findViewById(R.id.surnameInput)
        sexInput = findViewById(R.id.sexInput)
        ageInput = findViewById(R.id.ageInput)
        medicationQuestionsInput = findViewById(R.id.medicationQuestionsInput)
        medicationNamesInput = findViewById(R.id.medicationNamesInput)
        timeOfTakingMedicineInput = findViewById(R.id.timeOfTakingMedicineInput)
        signInButtonRegistration = findViewById(R.id.signInButtonRegistration)

        // Obsługa kliknięcia przycisku "Zarejestruj się"
        signInButtonRegistration.setOnClickListener {
            registerUser()
        }
    }

    /**
     * Walidacja danych rejestracyjnych wprowadzonych przez użytkownika
     * @return True, jeśli wszystkie pola zostały prawidłowo wypełnione, w przeciwnym razie zwraca False.
     */

    private fun validateRegisterDetails(): Boolean {
        val name = nameInput.text.toString().trim()
        val surname = surnameInput.text.toString().trim()
        val sex = sexInput.text.toString().trim()
        val age = ageInput.text.toString().trim()
        val question = medicationQuestionsInput.text.toString().trim()
        val drugsName = medicationNamesInput.text.toString().trim()
        val timeOfTakingMedication = timeOfTakingMedicineInput.text.toString().trim()

        if (TextUtils.isEmpty(name)) {
            showErrorSnackBar(resources.getString(R.string.err_msg_enter_name), true)
            return false
        }

        if (TextUtils.isEmpty(surname)) {
            showErrorSnackBar(resources.getString(R.string.err_msg_enter_surname), true)
            return false
        }

        if (TextUtils.isEmpty(sex)) {
            showErrorSnackBar(resources.getString(R.string.err_msg_enter_gender), true)
            return false
        }

        if (TextUtils.isEmpty(age)) {
            showErrorSnackBar(resources.getString(R.string.err_msg_enter_age), true)
            return false
        }

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
            timeOfTakingMedicineInput.isEnabled = false
            medicationNamesInput.isEnabled = false

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
     * Metoda wywoływana po poprawnej walidacji danych rejestracji.
     * Otwiera drugą aktywność rejestracji.
     */
    private fun registerUser() {
        val name = nameInput.text.toString().trim()
        val surname = surnameInput.text.toString().trim()
        val sex = sexInput.text.toString().trim()
        val age = ageInput.text.toString().trim()
        val question = medicationQuestionsInput.text.toString().trim()
        val drugsName = medicationNamesInput.text.toString().trim()
        val timeOfTakingMedication = timeOfTakingMedicineInput.text.toString().trim()

        if (validateRegisterDetails()) {
            openActivity(name, surname, sex, age, question, drugsName, timeOfTakingMedication)
        }
    }

    /**
     * Metoda służąca otwarciu drugiej aktywności rejestracji i przekazaniu do niej parametrów.
     */
    private fun openActivity(name: String, surname: String, sex:String, age: String, question: String, drugsName: String, timeOfTakingMedication: String){
        val intent = Intent(this, MainActivityRegistration2::class.java)
        intent.putExtra("name", name)
        intent.putExtra("surname", surname)
        intent.putExtra("sex", sex)
        intent.putExtra("age", age)
        intent.putExtra("question", question)
        intent.putExtra("drugsName", drugsName)
        intent.putExtra("timeOfTakingMedication", timeOfTakingMedication)
        startActivity(intent)
    }
}