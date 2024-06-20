package com.example.myapplicationkardioasystent.apps

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myapplicationkardioasystent.R

/**
 * BroadcastReceiver odpowiedzialny za wysyłanie powiadomień.
 */
class ReminderBroadcast : BroadcastReceiver() {

    /**
     *
     * @param context Kontekst aplikacji.
     * @param intent Intent, który spowodował wywołanie odbiornika.
     */

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        // Konfiguracja powiadomienia
        val notificationBuilder = NotificationCompat.Builder(context, "ChannelId")
        notificationBuilder
            .setSmallIcon(R.drawable.running_heart)
            .setContentTitle("KARDIOASYSTENT")
            .setContentText("Wprowadzono wynik pomiaru")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Utworzenie kanału powiadomień
        val channel = NotificationChannel("ChannelId", "ChannelId", NotificationManager.IMPORTANCE_DEFAULT)
        NotificationManagerCompat.from(context).createNotificationChannel(channel)

        val manager: NotificationManagerCompat = NotificationManagerCompat.from(context)

        // Sprawdzenie uprawnień do wysyłania powiadomień
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

            return
        }

        // Wyślij powiadomienie
        manager.notify(200, notificationBuilder.build())
    }
}