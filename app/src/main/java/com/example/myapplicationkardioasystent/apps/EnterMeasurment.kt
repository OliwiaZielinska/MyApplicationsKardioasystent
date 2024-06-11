package com.example.myapplicationkardioasystent.apps

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myapplicationkardioasystent.R
import com.example.myapplicationkardioasystent.cloudFirestore.Measurment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Aktywność umożliwiająca wprowadzenie wyniku pomiaru.
 * Umożliwia użytkownikowi zapisanie pomiaru i powrót do głównego widoku aplikacji.
 */
class EnterMeasurment : AppCompatActivity() {
    private lateinit var dateOfMeasurementInputText: TextInputEditText
    private lateinit var hourInputText: TextInputEditText
    private lateinit var bloodPressureInputText: TextInputEditText
    private lateinit var pulseInputText: TextInputEditText

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.enter_measurment_app)

        createNotificationChannel()

        // Inicjalizacja elementów interfejsu użytkownika
        dateOfMeasurementInputText = findViewById(R.id.dateOfMeasurementInputText)
        hourInputText = findViewById(R.id.hourInputText)
        bloodPressureInputText = findViewById(R.id.bloodPressureInputText)
        pulseInputText = findViewById(R.id.pulseInputText)

        // Ustawienie dzisiejszej daty w polu tekstowym
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        dateOfMeasurementInputText.setText(currentDate)

        // Ustawienie aktualnej godziny w polu tekstowym
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        hourInputText.setText(currentTime)

        // Obsługa przycisku "Zapisz wynik pomiaru"
        val recordMeasurementResultButton = findViewById<Button>(R.id.recordMeasurementResultButton)
        recordMeasurementResultButton.setOnClickListener {
            val userID = intent.getStringExtra("userID").toString()
            val date = dateOfMeasurementInputText.text.toString()
            val hour = hourInputText.text.toString()
            val bloodPressure = bloodPressureInputText.text.toString()
            val pulse = pulseInputText.text.toString()

            if (bloodPressure.isNotEmpty() && pulse.isNotEmpty()) {
                val measurment = Measurment(
                    userID,
                    date,
                    hour,
                    bloodPressure,
                    pulse
                )

                GlobalScope.launch(Dispatchers.Main) {
                    val collectionRef = FirebaseFirestore.getInstance().collection("measurements")
                    val documentRef = collectionRef.document()

                    documentRef.set(measurment)
                        .addOnSuccessListener {
                            Log.d(TAG, "DocumentSnapshot added with ID: ${documentRef.id}")
                            showNotification() // Wywołaj powiadomienie
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                        }
                }

                openActivity(userID)
            } else {
                Toast.makeText(this, "Empty values!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("ChannelId", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification() {
        val builder = NotificationCompat.Builder(this, "ChannelId")
            .setSmallIcon(R.drawable.running_heart)
            .setContentTitle("Measurement Recorded")
            .setContentText("Your measurement has been successfully recorded.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(this@EnterMeasurment, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Consider requesting the permission here
                return
            }
            notify(1, builder.build())
        }
    }

    /**
     * Metoda do otwarcia głównego widoku aplikacji.
     */
    private fun openActivity(userID: String) {
        val intent = Intent(this, MainViewApp::class.java)
        intent.putExtra("uID", userID)
        startActivity(intent)
    }
}
