package com.example.myapplicationkardioasystent.registation
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import com.example.myapplicationkardioasystent.R
/**
 * Aktywność obsługująca rejestrację użytkownika - krok 1.
 * Ta aktywność zbiera podstawowe informacje o użytkowniku oraz informacje o przyjmowanych lekach.
 */

class MainActivityRegistration1 : BaseActivity() {
    private lateinit var nameInput: EditText
    private lateinit var surnameInput: EditText
    private lateinit var sexInput: EditText
    private lateinit var yearOfBirthInput: EditText
    private lateinit var medicationQuestionsSwitch: Switch
    private lateinit var medicationNamesInput: EditText
    private lateinit var timeOfTakingMedicineInput: EditText
    private lateinit var signInButtonRegistration: Button
    private lateinit var setTakNieText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_registration1)
        // Inicjalizacja pól widoku
        nameInput = findViewById(R.id.nameInput)
        surnameInput = findViewById(R.id.surnameInput)
        sexInput = findViewById(R.id.sexInput)
        yearOfBirthInput = findViewById(R.id.yearOfBirthInput)
        medicationQuestionsSwitch = findViewById(R.id.medicationQuestionsSwitch)
        medicationNamesInput = findViewById(R.id.medicationNamesInput)
        timeOfTakingMedicineInput = findViewById(R.id.timeOfTakingMedicineInput)
        signInButtonRegistration = findViewById(R.id.signInButtonRegistration)
        setTakNieText = findViewById(R.id.setTakNieText)
        // Ustawienie nasłuchiwacza kliknięcia na przycisk rejestracji
        signInButtonRegistration.setOnClickListener {
            registerUser()
        }
// Ustawienie nasłuchiwacza zmiany stanu przełącznika pytań o leki
        medicationQuestionsSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            // Zmiana tekstu na "Tak" lub "Nie" w zależności od stanu przełącznika
            setTakNieText.text = if (isChecked) "Tak" else "Nie"
            // Logika włączania/wyłączania pól związanych z lekami
            medicationNamesInput.isEnabled = isChecked
            timeOfTakingMedicineInput.isEnabled = isChecked
        }

    }
    /**
     * Walidacja wprowadzonych danych rejestracyjnych.
     * Sprawdza, czy pola zostały wypełnione poprawnie.
     * @return true jeśli dane są poprawne, w przeciwnym razie false.
     */

    private fun validateRegisterDetails(): Boolean {
        val name = nameInput.text.toString().trim()
        val surname = surnameInput.text.toString().trim()
        val sex = sexInput.text.toString().trim()
        val yearOfBirth = yearOfBirthInput.text.toString().trim()
        val question = medicationQuestionsSwitch.isChecked

        if (TextUtils.isEmpty(name)) {
            showErrorSnackBar(getString(R.string.err_msg_enter_name), true)
            return false
        }

        if (TextUtils.isEmpty(surname)) {
            showErrorSnackBar(getString(R.string.err_msg_enter_surname), true)
            return false
        }

        if (TextUtils.isEmpty(sex)) {
            showErrorSnackBar(getString(R.string.err_msg_enter_gender), true)
            return false
        }

        if (TextUtils.isEmpty(yearOfBirth)) {
            showErrorSnackBar(getString(R.string.err_msg_enter_year_of_birth), true)
            return false
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
    /**
     * Metoda wywoływana po wciśnięciu przycisku rejestracji.
     * Sprawdza poprawność danych i przechodzi do kolejnego etapu rejestracji.
     */
    private fun registerUser() {
        if (validateRegisterDetails()) {
            val name = nameInput.text.toString().trim()
            val surname = surnameInput.text.toString().trim()
            val sex = sexInput.text.toString().trim()
            val yearOfBirth = yearOfBirthInput.text.toString().trim()
            val question = medicationQuestionsSwitch.isChecked
            val drugsName = medicationNamesInput.text.toString().trim()
            val timeOfTakingMedication = timeOfTakingMedicineInput.text.toString().trim()
// Przekazanie danych do kolejnej aktywności
            openActivity(name, surname, sex, yearOfBirth, question, drugsName, timeOfTakingMedication)
        }
    }
    /**
     * Metoda otwierająca kolejną aktywność rejestracji i przekazująca dane.
     * @param name Imię użytkownika.
     * @param surname Nazwisko użytkownika.
     * @param sex Płeć użytkownika.
     * @param yearOfBirth Rok urodzenia użytkownika.
     * @param question Informacja czy użytkownik przyjmuje leki.
     * @param drugsName Nazwa leku.
     * @param timeOfTakingMedication Czas przyjmowania leku.
     */
    private fun openActivity(name: String, surname: String, sex:String, yearOfBirth: String, question: Boolean, drugsName: String, timeOfTakingMedication: String){
        val intent = Intent(this, MainActivityRegistration2::class.java)
        intent.putExtra("name", name)
        intent.putExtra("surname", surname)
        intent.putExtra("sex", sex)
        intent.putExtra("yearOfBirth", yearOfBirth)
        intent.putExtra("question", question)
        intent.putExtra("drugsName", drugsName)
        intent.putExtra("timeOfTakingMedication", timeOfTakingMedication)
        startActivity(intent)
    }
}
