package com.example.myapplicationkardioasystent.cloudFirestore
/**
 * Interfejs FirestoreInterface służy do zdefiniowania operacji do interakcji z bazą danych Firestore.
 */
interface FirestoreInterface {

    /**
     * Suspend function do dodawania nowego rekordu (użytkownika) do bazy danych Firestore.
     *
     * @param userId Identyfikator nowego użytkownika.
     * @param user Obiekt klasy User, który ma zostać dodany do bazy danych.
     */
    suspend fun addUser(userId: String, user: User)

    /**
     * Suspend function do pobierania danych użytkownika z bazy danych na podstawie jego ID.
     *
     * @param userId Identyfikator użytkownika, którego dane mają zostać pobrane.
     * @return Obiekt klasy User odpowiadający danym użytkownika z bazy danych,
     * lub null, jeśli użytkownik o podanym identyfikatorze nie istnieje.
     */
    suspend fun getUser(userId: String): User?

    /**
     * Suspend function do aktualizacji istniejącego rekordu (użytkownika) w bazie danych Firestore.
     *
     * @param userId Identyfikator użytkownika, którego dane mają zostać zaktualizowane.
     * @param updatedUser Obiekt klasy User z zaktualizowanymi danymi.
     */
    suspend fun updateUser(userId: String, updatedUser: User)

    /**
     * Suspend function do usuwania istniejącego rekordu (użytkownika) z bazy danych na podstawie jego ID.
     *
     * @param userId Identyfikator użytkownika, którego dane mają zostać usunięte.
     */
    suspend fun deleteUser(userId: String)
}