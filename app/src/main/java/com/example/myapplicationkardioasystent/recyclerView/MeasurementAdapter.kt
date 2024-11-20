package com.example.myapplicationkardioasystent.recyclerView

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplicationkardioasystent.R
import com.example.myapplicationkardioasystent.cloudFirestore.Measurment
import com.example.myapplicationkardioasystent.cloudFirestore.User
import com.example.myapplicationkardioasystent.registation.Gender
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

/**
 * Adapter RecyclerView odpowiedzialny za wyświetlanie pomiarów użytkownika.
 * @property measurements Lista pomiarów do wyświetlenia.
 * @property intent Intent zawierający dodatkowe informacje, takie jak identyfikator użytkownika.
 */
class MeasurementAdapter(val measurements: MutableList<Measurment>, val intent: Intent) : RecyclerView.Adapter<MeasurementAdapter.MeasurementViewHolder>() {

    /**
     * ViewHolder do przechowywania widoków elementów pomiarów.
     * @param itemView Widok pojedynczego elementu pomiaru.
     */
    class MeasurementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val bloodPressureTextView: TextView = itemView.findViewById(R.id.bloodPressureTextView)
        val pulseTextView: TextView = itemView.findViewById(R.id.pulseTextView)
        val imageViewPulse: ImageView = itemView.findViewById(R.id.imageViewPulse)
        val imageViewBloodPressure: ImageView = itemView.findViewById(R.id.imageViewBloodPressure)
    }
    /**
     * Tworzy nowy widok pojedynczego elementu pomiaru w RecyclerView.
     * @param parent Widok rodzica, do którego nowy widok będzie dołączony.
     * @param viewType Typ widoku, który jest używany w przypadku, gdy RecyclerView ma różne typy widoków.
     * @return Zwraca nowy obiekt `MeasurementViewHolder`, który zawiera widok pojedynczego elementu.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasurementViewHolder {
        // Tworzenie nowego widoku pojedynczego elementu pomiaru.
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.measurment_item, parent, false)
        return MeasurementViewHolder(itemView)
    }
    /**
     * Wiąże dane pomiaru z odpowiednim widokiem (ViewHolder) w RecyclerView.
     * @param holder ViewHolder, który zawiera widoki pojedynczego elementu w RecyclerView.
     * @param position Pozycja elementu w danych, które są wyświetlane w RecyclerView.
     */
    override fun onBindViewHolder(holder: MeasurementViewHolder, position: Int) {
        // Wiązanie danych pomiaru z widokiem ViewHoldera.
        val currentMeasurement = measurements[position]
        val userIdFromIntent = intent.getStringExtra("userID").toString()

        // Sprawdzenie, czy pomiar należy do bieżącego użytkownika.
        if (currentMeasurement.userID == userIdFromIntent) {
            // Ustawienie danych pomiaru w widoku ViewHoldera.
            holder.dateTextView.text = currentMeasurement.date
            holder.timeTextView.text = currentMeasurement.hour
            holder.bloodPressureTextView.text = currentMeasurement.bloodPressure
            holder.pulseTextView.text = currentMeasurement.pulse

            val bloodPressureParts = currentMeasurement.bloodPressure.split("/")
            if (bloodPressureParts.size == 2) {
                val systolic = bloodPressureParts[0].toIntOrNull()
                val diastolic = bloodPressureParts[1].toIntOrNull()
                if (systolic != null && diastolic != null) {
                    calculateAgeAndGender { age, gender ->
                        val systolicRange = when (age) {
                            in 18..39 -> if (gender == Gender.MALE.toString()) 114..124 else 105..115
                            in 40..59 -> if (gender == Gender.MALE.toString()) 119..129 else 117..127
                            else -> if (gender == Gender.MALE.toString()) 128..138 else 134..144
                        }
                        val diastolicRange = when (age) {
                            in 18..39 -> if (gender == Gender.MALE.toString()) 65..75 else 63..73
                            in 40..59 -> if (gender == Gender.MALE.toString()) 72..82 else 69..79
                            else -> if (gender == Gender.MALE.toString()) 64..74 else 63..73
                        }
                        if (systolic in systolicRange && diastolic in diastolicRange) {
                            holder.imageViewBloodPressure.setImageResource(R.drawable.w_normie_cisnienie)
                        } else {
                            holder.imageViewBloodPressure.setImageResource(R.drawable.poza_norma_cisnienie)
                        }
                    }
                }
            }

            val pulseValue = currentMeasurement.pulse.toIntOrNull()
            if (pulseValue != null) {
                calculateAgeAndGender { age, gender ->
                    val bpmRange = when (age) {
                        in 18..25 -> if (gender == Gender.MALE.toString()) 62..73 else 64..80
                        in 26..35 -> if (gender == Gender.MALE.toString()) 62..73 else 64..81
                        in 36..45 -> if (gender == Gender.MALE.toString()) 63..75 else 65..82
                        in 46..55 -> if (gender == Gender.MALE.toString()) 64..76 else 66..83
                        in 56..65 -> if (gender == Gender.MALE.toString()) 62..75 else 64..82
                        else -> if (gender == Gender.MALE.toString()) 62..73 else 64..81
                    }

                    if (pulseValue in bpmRange) {
                        holder.imageViewPulse.setImageResource(R.drawable.w_normie_tetno)
                        print(gender)
                    } else {
                        holder.imageViewPulse.setImageResource(R.drawable.poza_norma_tetno)
                        print(gender)
                    }
                }
            }
        } else {
            // Ukrycie widoku, jeśli pomiar nie należy do bieżącego użytkownika.
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        }
    }

    /**
     * Zwraca liczbę elementów (pomiarów), które należą do bieżącego użytkownika.
     * @return Liczba elementów w zbiorze danych, które należą do bieżącego użytkownika.
     */
    override fun getItemCount(): Int {
        // Zwrócenie liczby pomiarów należących do bieżącego użytkownika.
        return measurements.count { it.userID == intent.getStringExtra("userID").toString() }
    }

    /**
     * Oblicza wiek użytkownika oraz jego płeć na podstawie danych zapisanych w bazie danych Firebase.
     * @param callback Funkcja, która otrzymuje dwa parametry: `Int` - wiek użytkownika. String` - płeć użytkownika.
     */
    private fun calculateAgeAndGender(callback: (Int, String) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.email
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val currentUser = document.toObject(User::class.java)
                    if (currentUser != null) {
                        val birthYear = currentUser.yearOfBirth.toIntOrNull()
                        val gender = currentUser.sex
                        if (birthYear != null && gender != null) {
                            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                            val age = currentYear - birthYear
                            callback(age, gender)

                        } else {
                            callback(-1, "unknown")
                        }
                    } else {
                        callback(-1, "unknown")
                    }
                }
                .addOnFailureListener {
                    callback(-1, "unknown")
                }
        } else {
            callback(-1, "unknown")
        }
    }
}