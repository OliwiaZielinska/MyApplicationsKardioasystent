package com.example.myapplicationkardioasystent.apps

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationkardioasystent.R
import com.google.firebase.auth.FirebaseAuth

/**
 * Aktywność do wyświetlania raportu z pomiarów tętna.
 */
class Raports : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.raports)

        val returnFromRaportsButton = findViewById<Button>(R.id.returnFromRaportsButton)
        val userId = FirebaseAuth.getInstance().currentUser!!.email
        returnFromRaportsButton.setOnClickListener {
            openMainActivity(userId.toString())
        }

        val sendRaportsButton = findViewById<Button>(R.id.sendRaportsButton)
        sendRaportsButton.setOnClickListener {
            openMainActivity(userId.toString())
        }
    }

    /**
     * Otwiera główną aktywność aplikacji.
     */
    private fun openMainActivity(userID : String) {
        val intent = Intent(this, MainViewApp::class.java)
        intent.putExtra("uID", userID)
        startActivity(intent)
    }
}