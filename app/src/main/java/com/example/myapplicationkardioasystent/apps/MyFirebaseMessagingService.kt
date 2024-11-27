package com.example.myapplicationkardioasystent.apps

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myapplicationkardioasystent.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Serwis obsługujący wiadomości Firebase Cloud Messaging (FCM).
 * Otrzymuje wiadomości push i wyświetla je jako powiadomienia w aplikacji.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Wywoływana, gdy zostanie odebrana wiadomość z Firebase Cloud Messaging.
     * Przetwarza treść wiadomości i wyświetla odpowiednie powiadomienie.
     *
     * @param remoteMessage Wiadomość otrzymana z Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.notification?.let {
            showNotification(it.title, it.body)
        }
    }

    /**
     * Wyświetla powiadomienie na podstawie otrzymanej treści wiadomości.
     *
     * @param title Tytuł powiadomienia.
     * @param body Treść powiadomienia.
     */
    private fun showNotification(title: String?, body: String?) {
        val channelId = "PushChannelId"

        // Tworzenie kanału powiadomień
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Push Notifications", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Kanał dla powiadomień push"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        // Tworzenie powiadomienia
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.running_heart)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Sprawdzanie uprawnień POST_NOTIFICATIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        // Wyświetlanie powiadomienia za pomocą NotificationManagerCompat
        with(NotificationManagerCompat.from(this)) {
            notify(0, notification)
        }
    }
}
