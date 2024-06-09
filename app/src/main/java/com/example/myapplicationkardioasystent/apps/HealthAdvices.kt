package com.example.myapplicationkardioasystent.apps
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationkardioasystent.R
import com.example.myapplicationkardioasystent.cloudFirestore.FirestoreDatabaseOperations
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Aktywność wyświetlająca poradnik zdrowia pomagający zadbać o kondycję serca.
 * Umożliwia użytkownikowi powrót do głównego widoku aplikacji.
 */
class HealthAdvices : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.health_advices)

        val userId = FirebaseAuth.getInstance().currentUser!!.email

        // Obsługa przycisku "Powrót"
        val backGuideButton = findViewById<Button>(R.id.backGuideButton)
        backGuideButton.setOnClickListener {
            // Powrót do głównego okna aplikacji
            openMainActivity(userId.toString())
        }
    }

    /**
     * Metoda do otwarcia głównego widoku aplikacji.
     */
    private fun openMainActivity(userID : String) {
        val intent = Intent(this, MainViewApp::class.java)
        intent.putExtra("uID", userID)
        startActivity(intent)
    }
}
