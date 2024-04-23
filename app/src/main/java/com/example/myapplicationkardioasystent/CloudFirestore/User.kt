package com.example.myapplicationkardioasystent.CloudFirestore
import com.google.firebase.firestore.PropertyName

/**
 * Klasa User reprezentuje dane użytkownika aplikacji.
 *
 * @property name imię użytkownika
 * @property surname nazwisko użytkownika.
 * @property sex płeć użytkownika.
 * @property age wiek użytkownika.
 * @property userId Numer identyfikacyjny użytkownika //MUSIMY POMYŚLEĆ JAKA DANA UŻYTKOWNIKA U NAS MA BYĆ ID - CO JEST UNIKATOWE, MOŻE LOGIN?
 *
 * @constructor Tworzy obiekt klasy User z podanymi wartościami lub inicjuje
 * puste łańcuchy znaków (String) dla name, surname, sex, userId oraz zero dla age.
 * Konstruktor ten jest wymagany do prawidłowej deserializacji obiektu przez Firestore.
 * Standardowa implementacja klasy User wyglądałaby tak: - musimy pomyśleć jak to zastosować - ja ogólnie na razie wpisałam tak plus minus, bo my mamy jeszcze login itd.

 * //    val name: String,
 * //    val surname: String,
 * //    val sex: String,
 * //    val age: Int,
 * //    val userId: String // tu musimy pomyśleć
 *
 */
data class User(
    @get:PropertyName("imie") @set:PropertyName("imie") var imie: String = "",
    @get:PropertyName("nazwisko") @set:PropertyName("nazwisko") var nazwisko: String = "",
    @get:PropertyName("plec") @set:PropertyName("plec") var plec: String = "",
    @get:PropertyName("wiek") @set:PropertyName("wiek") var wiek: String = "",
    @get:PropertyName("pyt") @set:PropertyName("pyt") var pyt: String = "",
    @get:PropertyName("nazwaLeku") @set:PropertyName("nazwaLeku") var nazwaLeku: String = "",
    @get:PropertyName("godzina") @set:PropertyName("godzina") var godzina: String = "",
    @get:PropertyName("login") @set:PropertyName("login") var login: String = "",
    @get:PropertyName("haslo") @set:PropertyName("haslo") var haslo: String = "",
    @get:PropertyName("rano") @set:PropertyName("rano") var rano: String = "",
    @get:PropertyName("poludnie") @set:PropertyName("poludnie") var poludnie: String = "",
    @get:PropertyName("wieczor") @set:PropertyName("wieczor") var wieczor: String = ""
)