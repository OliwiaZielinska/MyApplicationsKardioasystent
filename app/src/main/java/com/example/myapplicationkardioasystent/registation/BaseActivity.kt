package com.example.myapplicationkardioasystent.registation

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myapplicationkardioasystent.R
import com.google.android.material.snackbar.Snackbar

/**
 * Klasa bazowa dla aktywności w aplikacji, która udostępnia funkcjonalność wyświetlania komunikatów Snackbar.
 */
open class BaseActivity : AppCompatActivity() {

    /**
     * Metoda do wyświetlania komunikatów Snackbar.
     * @param message Treść komunikatu do wyświetlenia.
     * @param errorMessage Określa, czy komunikat jest błędem (true) czy sukcesem (false).
     */
    fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        // Tworzenie Snackbar z przekazaną wiadomością
        val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view

        // Ustawienie koloru tła Snackbar w zależności od rodzaju komunikatu
        if (errorMessage) {
            snackbarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.colorSnackBarError
                )
            )
        } else {
            snackbarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.colorSnackBarSuccess
                )
            )
        }
        // Wyświetlenie Snackbar
        snackbar.show()
    }
}