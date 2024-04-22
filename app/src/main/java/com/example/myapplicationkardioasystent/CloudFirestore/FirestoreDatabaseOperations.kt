package com.example.myapplicationkardioasystent.CloudFirestore
import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
/**
 * Klasa FirestoreDatabaseOperations implementuje interfejs FirestoreInterface
 * i zawiera metody do dodawania, pobierania, aktualizowania i usuwania danych użytkownika w bazie danych Firestore.
 *
 * @property db - Referencja do obiektu FirebaseFirestore, służąca do interakcji z bazą danych Firestore.
 */

class FirestoreDatabaseOperations(private val db: FirebaseFirestore) : FirestoreInterface {


    /**
     *  funkcja do dodawania nowego użytkownika do bazy danych Firestore.
     * Wykorzystuje mechanizm korutyn do wykonywania operacji asynchronicznych.
     * suspend - oznacza, ze funkcja moze byc zawieszana w korutynie
     * @param studentId Identyfikator nowego użytkownika.
     * @param student Obiekt klasy User, który ma zostać dodany do bazy danych.
     */

    override suspend fun addUser(userId: String, user: User) {
        try {
            db.collection("users").document(userId).set(user).await()
        } catch (e: Exception) {
            // tu trzeba dodać obsługęb błędów, np. logowanie, zwracanie wartości lub propagacja wyjątku
        }
    }

    /**
     *      funkcja do pobierania danych studenta z bazy danych Firestore.
     *      * Wykorzystuje mechanizm korutyn do wykonywania operacji asynchronicznych.
     *      *
     *      * @param userId Identyfikator użytkownika, którego dane mają zostać pobrane.
     *      * @return Obiekt klasy User odpowiadający danym użytkownika z bazy danych,
     *      * lub null, jeśli student o podanym identyfikatorze nie istnieje.
     *      */

    override suspend fun getUser(userId: String): User? {
        val snapshot = FirebaseFirestore.getInstance().collection("users")
            .whereEqualTo(FieldPath.documentId(), userId)
            .get()
            .await()

        return snapshot.documents.firstOrNull()?.toObject(User::class.java)
    }

    /**
     * funkcja do aktualizowania danych studenta w bazie danych Firestore.
     * Wykorzystuje mechanizm korutyn do wykonywania operacji asynchronicznych.
     *
     * @param userId Identyfikator użytkownika, którego dane mają zostać zaktualizowane.
     * @param updatedUser Obiekt klasy User z zaktualizowanymi danymi.
     */

    override suspend fun updateUser(userId: String, updatedUser: User) {
        try {
            db.collection("students").document(userId).set(updatedUser).await()
        } catch (e: Exception) {
            // tu trzeba dodać obsługę błędów
        }
    }
    /**
     * funkcja do usuwania danych użytkownika z bazy danych Firestore.
     * Wykorzystuje mechanizm korutyn do wykonywania operacji asynchronicznych.
     *
     * @param userId Identyfikator użytkownika, którego dane mają zostać usunięte.
     */

    override suspend fun deleteUser(userId: String) {
        try {
            db.collection("users").document(userId).delete().await()
        } catch (e: Exception) {
            // tu trzeba dodać obsługę błędów
        }
    }
}