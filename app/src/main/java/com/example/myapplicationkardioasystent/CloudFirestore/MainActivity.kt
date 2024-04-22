package com.example.myapplicationkardioasystent.CloudFirestore
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationkardioasystent.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * MainActivity to główna aktywność aplikacji. Zarządza interfejsem użytkownika
 * oraz obsługuje logikę związaną z interakcją z bazą danych Firestore.
 */
class MainActivity : AppCompatActivity() {

    // Referencja do obiektu FirebaseFirestore do interakcji z bazą danych Firestore
    val db = Firebase.firestore

    // Obiekt do obsługi operacji na bazie danych Firestore
    private val dbOperations = FirestoreDatabaseOperations(db)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_firebase)

        // Inicjalizacja wszystkich widżetów interfejsu użytkownika
        val buttonDownload = findViewById<Button>(R.id.buttonDownload)
        val buttonAdd = findViewById<Button>(R.id.buttonAdd)
        val buttonUpdate = findViewById<Button>(R.id.buttonUpdate)
        val buttonDelete = findViewById<Button>(R.id.buttonDelete)
        val editTextName = findViewById<EditText>(R.id.editTextName)
        val editTextSurname = findViewById<EditText>(R.id.editTextSurname)
        val editTextAge = findViewById<EditText>(R.id.editTextAge)
        val editTextSex = findViewById<EditText>(R.id.editTextSex)
        val editTextUserId = findViewById<EditText>(R.id.editTextUserId)
        val userDetailsTV = findViewById<TextView>(R.id.userDetailsTV)

        // Ustawienie nasłuchiwaczy kliknięć dla przycisków

        buttonDownload.setOnClickListener {
            // Pobieranie identyfikatora użytkownika z pola tekstowego
            val userId = editTextUserId.text.toString()

            // Uruchomienie korutyny w wątku głównym
            GlobalScope.launch(Dispatchers.Main) {
                // Pobranie danych użytkownika z bazy danych Firestore
                val user = dbOperations.getUser(userId)
                if (user != null) {
                    println("pobrano użytkownika")
                    // Aktualizacja interfejsu użytkownika
                    updateUI(user, userDetailsTV)
                } else {
                    println("nie pobrano użytkownika")
                }
            }
        }

        buttonAdd.setOnClickListener {
            // Pobranie danych użytkownika z pól tekstowych
            val userId = editTextUserId.text.toString()
            val name = editTextName.text.toString()
            val surname = editTextSurname.text.toString()
            val sex = editTextSex.text.toString()
            val age = editTextAge.text.toString().toIntOrNull()

            if (userId.isNotEmpty() && name.isNotEmpty() && surname.isNotEmpty() && sex.isNotEmpty() && age != null) {
                // Utworzenie obiektu user
                val user = User(name, surname, sex, age, userId)
                // Uruchomienie korutyny w wątku głównym
                GlobalScope.launch(Dispatchers.Main) {
                    // Dodanie studenta do bazy danych Firestore
                    dbOperations.addUser(userId, user)
                }
            } else {
                // tu trzeba dodać obsługę błędów np. pustych pól
            }
        }

        buttonUpdate.setOnClickListener {
            // Pobranie danych użytkownika z pól tekstowych
            val userId = editTextUserId.text.toString()
            val name = editTextName.text.toString()
            val surname = editTextSurname.text.toString()
            val sex = editTextSex.text.toString()
            val age = editTextAge.text.toString().toIntOrNull()

            if (userId.isNotEmpty() && name.isNotEmpty() && surname.isNotEmpty() && sex.isNotEmpty() && age != null) {
                // Utworzenie zaktualizowanego obiektu User
                val updatedUser = User(name, surname, sex, age, userId)
                // Uruchomienie korutyny w wątku głównym
                GlobalScope.launch(Dispatchers.Main) {
                    // Aktualizacja danych użytkownika w bazie danych Firestore
                    dbOperations.updateUser(userId, updatedUser)
                }
            } else {
                // tu trzeba dodać obsługę błędów np. pustych pól
            }
        }

        buttonDelete.setOnClickListener {
            // Pobranie identyfikatora użytkownika z pola tekstowego
            val userId = editTextUserId.text.toString()
            // Uruchomienie korutyny w wątku głównym
            GlobalScope.launch(Dispatchers.Main) {
                // Usunięcie użytkownika z bazy danych Firestore
                dbOperations.deleteUser(userId)
            }
        }
    }

    /**
     * Metoda aktualizuje interfejs użytkownika, wyświetlając dane użytkownika.
     *
     * @param user Obiekt klasy User, którego dane mają zostać wyświetlone.
     * @param textView TextView, w którym mają zostać wyświetlone dane użytkownika.
     */
    private fun updateUI(user: User, textView: TextView) {
        val userDetails = "User details:\n" +
                "Name: ${user.name}\n" +
                "Surname: ${user.surname}\n" +
                "Age: ${user.age}"
        textView.text = userDetails
    }
}