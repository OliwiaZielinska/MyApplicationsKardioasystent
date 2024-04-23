package com.example.myapplicationkardioasystent.cloudFirestore
import com.google.firebase.firestore.PropertyName

/**
 * Klasa Measurment reprezentuje dane pomiaru ciśnienia krwi i tętna.
 *
 * @property userID unikatowy numer użytkownika.
 * @property date data pomiaru.
 * @property hour godzina pomiaru.
 * @property bloodPressure ciśnienie krwi.
 * @property pulse tętno.
 * @constructor Tworzy obiekt klasy Measurment z podanymi wartościami lub inicjuje je.
 *
 * Puste łańcuchy znaków (String) dla date, hour, bloodPressure i pulse.
 * Konstruktor ten jest wymagany do prawidłowej deserializacji obiektu przez Firestore.
 */
data class Measurment(
    @get:PropertyName("userID") @set:PropertyName("userID") var userID: String = "",
    @get:PropertyName("date") @set:PropertyName("date") var date: String = "",
    @get:PropertyName("hour") @set:PropertyName("hour") var hour: String = "",
    @get:PropertyName("bloodPressure") @set:PropertyName("bloodPressure") var bloodPressure: String = "",
    @get:PropertyName("pulse") @set:PropertyName("pulse") var pulse: String = ""
)