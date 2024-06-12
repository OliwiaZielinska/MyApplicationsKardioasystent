package com.example.myapplicationkardioasystent.apps
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.myapplicationkardioasystent.R
/**
 * Worker odpowiadający za wysyłanie powiadomień związanych z pomiarami ciśnienia krwi i pulsu.
 *
 * @param context Kontekst aplikacji.
 * @param params Parametry pracy dla workera.
 */
class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    /**
     * Metoda doWork() wykonuje zadanie workera, czyli wysyłanie powiadomienia.
     *
     * @return Wynik pracy workera.
     */
    override fun doWork(): Result {
        val message = inputData.getString("message") ?: "Czas wykonać pomiar ciśnienia krwi i pulsu"
        sendNotification(message)
        return Result.success()
    }
    /**
     * Metoda sendNotification() wysyła powiadomienie o zadanym komunikacie.
     *
     * @param message Treść powiadomienia.
     */
    private fun sendNotification(message: String) {
        val channelId = "measurement_channel"
        val channelName = "Measurement Notifications"
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Przypomnienie")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(1, notification)
    }
}

