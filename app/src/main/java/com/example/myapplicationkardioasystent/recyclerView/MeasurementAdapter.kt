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

/**
 * Adapter dla RecyclerView, służący do wyświetlania listy pomiarów.
 *
 * @param measurements Lista pomiarów do wyświetlenia.
 * @param intent Intent przechowujący informacje o aktualnie zalogowanym użytkowniku.
 */
class MeasurementAdapter(val measurements: MutableList<Measurment>, val intent: Intent) : RecyclerView.Adapter<MeasurementAdapter.MeasurementViewHolder>() {

    /**
     * ViewHolder przechowujący widoki elementów listy.
     *
     * @param itemView Widok elementu listy.
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
     * Tworzenie nowego ViewHoldera (inicjalizacja widoku).
     *
     * @param parent Grupa, do której ViewHolder zostanie dołączony po utworzeniu.
     * @param viewType Typ widoku.
     * @return MeasurementViewHolder nowy obiekt MeasurementViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasurementViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.measurment_item, parent, false)
        return MeasurementViewHolder(itemView)
    }

    /**
     * Wiązanie danych z konkretnym elementem listy.
     *
     * @param holder ViewHolder do którego dane mają być przypisane.
     * @param position Pozycja elementu na liście.
     */
    override fun onBindViewHolder(holder: MeasurementViewHolder, position: Int) {
        val currentMeasurement = measurements[position]

        // Sprawdzenie, czy pomiar należy do aktualnie zalogowanego użytkownika
        if (currentMeasurement.userID == intent.getStringExtra("userID").toString()) {
            // Jeśli pomiar należy do zalogowanego użytkownika, wyświetl go
            holder.dateTextView.text = currentMeasurement.date
            holder.timeTextView.text = currentMeasurement.hour
            holder.bloodPressureTextView.text = currentMeasurement.bloodPressure
            holder.pulseTextView.text = currentMeasurement.pulse

            // Sprawdzenie wartości ciśnienia krwi i ustawienie odpowiedniego obrazka
            val bloodPressureParts = currentMeasurement.bloodPressure.split("/")
            if (bloodPressureParts.size == 2) {
                val systolic = bloodPressureParts[0].toIntOrNull()
                val diastolic = bloodPressureParts[1].toIntOrNull()

                if (systolic != null && diastolic != null) {
                    if (systolic in 110..130 && diastolic in 60..80) {
                        holder.imageViewBloodPressure.setImageResource(R.drawable.w_normie_cisnienie)
                    } else {
                        holder.imageViewBloodPressure.setImageResource(R.drawable.poza_norma_cisnienie)
                    }
                }
            }

            // Sprawdzenie wartości pulsu i ustawienie odpowiedniego obrazka
            val pulseValue = currentMeasurement.pulse.toIntOrNull()
            if (pulseValue != null) {
                if (pulseValue < 100) {
                    holder.imageViewPulse.setImageResource(R.drawable.w_normie_tetno)
                } else {
                    holder.imageViewPulse.setImageResource(R.drawable.poza_norma_tetno)
                }
            }
        } else {
            // Jeśli pomiar nie należy do zalogowanego użytkownika, ukryj ten element listy
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        }
    }

    /**
     * Zwrócenie liczby elementów na liście.
     *
     * @return Liczba elementów na liście.
     */
    override fun getItemCount(): Int {
        // Zliczenie pomiarów należących do aktualnie zalogowanego użytkownika
        return measurements.count { it.userID == intent.getStringExtra("userID").toString() }
    }
}