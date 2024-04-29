package com.example.myapplicationkardioasystent.cloudFirestore
import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Klasa FirestoreDatabaseOperations implementuje interfejs FirestoreInterface
 * i zawiera metody do dodawania, pobierania, aktualizowania i usuwania danych użytkownika w bazie danych Firestore.
 * @property db - Referencja do obiektu FirebaseFirestore, służąca do interakcji z bazą danych Firestore.
 */

class FirestoreDatabaseOperations(private val db: FirebaseFirestore) : FirestoreInterface {


    /**
     * Funkcja do dodawania nowego użytkownika do bazy danych Firestore.
     * Wykorzystuje mechanizm korutyn do wykonywania operacji asynchronicznych.
     *
     * suspend - oznacza, ze funkcja moze byc zawieszana w korutynie
     * @param userId Identyfikator nowego użytkownika.
     * @param user Obiekt klasy User, który ma zostać dodany do bazy danych.
     */

    override suspend fun addUser(userId: String, user: User) {
        try {
            db.collection("users").document(userId).set(user).await()
        }catch (e: Exception) {
            // Dodawanie użytkownika błąd
            Log.e("addUser", "An error occurred while adding a user: $e")
        }
    }

    /**
     * Funkcja do pobierania danych użytkowika z bazy danych Firestore.
     * Wykorzystuje mechanizm korutyn do wykonywania operacji asynchronicznych.
     *
     * @param userId Identyfikator użytkownika, którego dane mają zostać pobrane.
     * @return Obiekt klasy User odpowiadający danym użytkownika z bazy danych,
     * lub null, jeśli użytkownik o podanym identyfikatorze nie istnieje.
     */

    override suspend fun getUser(userId: String): User? {
        val snapshot = FirebaseFirestore.getInstance().collection("users")
            .whereEqualTo(FieldPath.documentId(), userId)
            .get()
            .await()

        return snapshot.documents.firstOrNull()?.toObject(User::class.java)
    }

    /**
     * Funkcja do aktualizowania danych użytkownika w bazie danych Firestore.
     * Wykorzystuje mechanizm korutyn do wykonywania operacji asynchronicznych.
     *
     * @param userId Identyfikator użytkownika, którego dane mają zostać zaktualizowane.
     * @param updatedUser Obiekt klasy User z zaktualizowanymi danymi.
     */

    override suspend fun updateUser(userId: String, updatedUser: User) {
        try {
            db.collection("users").document(userId).set(updatedUser).await()
        } catch (e: Exception) {
            Log.e("updateUser", "An error occurred while updating user data: $e")
        }
    }
    /**
     * Funkcja do usuwania danych użytkownika z bazy danych Firestore.
     * Wykorzystuje mechanizm korutyn do wykonywania operacji asynchronicznych.
     *
     * @param userId Identyfikator użytkownika, którego dane mają zostać usunięte.
     */

    override suspend fun deleteUser(userId: String) {
        try {
            // Pobierz referencję do kolekcji "measurements"
            val collectionRef = FirebaseFirestore.getInstance().collection("measurements")

            // Wykonaj zapytanie, aby uzyskać pomiary dla określonego użytkownika
            collectionRef.whereEqualTo("userID", userId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        // Usuń każdy znaleziony dokument
                        document.reference.delete()
                            .addOnSuccessListener {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!")
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error deleting document", e)
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
            db.collection("users").document(userId).delete().await()
            val user = FirebaseAuth.getInstance().currentUser
            user?.delete()
        } catch (e: Exception) {
            Log.e("deleteUser", "An error occurred while deleting a user: $e")
        }
    }

    /**
     * Funkcja do dodawania nowego pomiaru ciśnienia krwi i tętna do bazy danych Firestore.
     * Wykorzystuje mechanizm korutyn do wykonywania operacji asynchronicznych.
     *
     * suspend - oznacza, ze funkcja moze byc zawieszana w korutynie
     * @param userId Identyfikator użytkownika.
     * @param measurmentID Identyfikator nowego pomiaru ciśnienia i tętna.
     * @param measurment Obiekt klasy Measurment, który ma zostać dodany do bazy danych.
     */

    override suspend fun addMeasurment(userId: String, measurmentID:String,  measurment: Measurment) {
        try {
            db.collection("measurments").document(userId).collection("measurment").document(measurmentID).set(measurment).await()
        }catch (e: Exception) {
            // Dodawanie użytkownika błąd
            Log.e("addMeasurment", "An error occurred while adding a measurment: $e")
        }
    }

}