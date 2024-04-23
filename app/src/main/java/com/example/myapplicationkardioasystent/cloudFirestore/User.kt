package com.example.myapplicationkardioasystent.cloudFirestore
import com.google.firebase.firestore.PropertyName

/**
 * Klasa User reprezentuje dane użytkownika aplikacji.
 *
 * @property name imię użytkownika
 * @property surname nazwisko użytkownika.
 * @property sex płeć użytkownika.
 * @property age wiek użytkownika.
 * @property question odpowiedź na pytanie o przyjmowanie leków.
 * @property drugsName nazwy leków przyjmowanych przez użytkowników "na stałe".
 * @property timeOfTakingMedication godziny przyjmowania leków.
 * @property login login użytkownika.
 * @property password hasło użytkownika.
 * @property morningMeasurement godziny pomiaru ciśnienia i tętna rano.
 * @property middayMeasurement godziny pomiaru ciśnienia i tętna w południe.
 * @property eveningMeasurement godziny pomiaru ciśnienia i tętna wieczorem.
 * @constructor Tworzy obiekt klasy User z podanymi wartościami lub inicjuje je.
 *
 * Puste łańcuchy znaków (String) dla name, surname, sex, age, question, drugsName,
 * timeOfTakingMedication, login, password, morningMeasurement, middayMeasurement,
 * eveningMeasurement.
 * Konstruktor ten jest wymagany do prawidłowej deserializacji obiektu przez Firestore.
 */
data class User(
    @get:PropertyName("name") @set:PropertyName("name") var name: String = "",
    @get:PropertyName("surname") @set:PropertyName("surname") var surname: String = "",
    @get:PropertyName("sex") @set:PropertyName("sex") var sex: String = "",
    @get:PropertyName("age") @set:PropertyName("age") var age: String = "",
    @get:PropertyName("question") @set:PropertyName("question") var question: String = "",
    @get:PropertyName("drugsName") @set:PropertyName("drugsName") var drugsName: String = "",
    @get:PropertyName("timeOfTakingMedication") @set:PropertyName("timeOfTakingMedication") var timeOfTakingMedication: String = "",
    @get:PropertyName("login") @set:PropertyName("login") var login: String = "",
    @get:PropertyName("password") @set:PropertyName("password") var password: String = "",
    @get:PropertyName("morningMeasurement") @set:PropertyName("morningMeasurement") var morningMeasurement: String = "",
    @get:PropertyName("middayMeasurement") @set:PropertyName("middayMeasurement") var middayMeasurement: String = "",
    @get:PropertyName("eveningMeasurement") @set:PropertyName("eveningMeasurement") var eveningMeasurement: String = ""
)