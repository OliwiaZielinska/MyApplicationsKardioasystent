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
    @get:PropertyName("name") @set:PropertyName("name") var name: String = "",
    @get:PropertyName("surname") @set:PropertyName("surname") var surname: String = "",
    @get:PropertyName("sex") @set:PropertyName("sex") var sex: String = "",
    @get:PropertyName("age") @set:PropertyName("age") var age: Int = 0,
    @get:PropertyName("userId") @set:PropertyName("userId") var userId: String = ""

)