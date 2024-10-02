package com.example.myapplicationkardioasystent.apps

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplicationkardioasystent.R
import com.example.myapplicationkardioasystent.cloudFirestore.Measurment
import com.example.myapplicationkardioasystent.recyclerView.MeasurementAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
