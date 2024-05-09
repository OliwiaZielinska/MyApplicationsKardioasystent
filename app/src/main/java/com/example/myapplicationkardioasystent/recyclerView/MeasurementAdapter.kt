package com.example.myapplicationkardioasystent.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplicationkardioasystent.cloudFirestore.Measurment
import com.example.myapplicationkardioasystent.R

class MeasurementAdapter(val measurements: MutableList<Measurment>, val currentUserUid: String) : RecyclerView.Adapter<MeasurementAdapter.MeasurementViewHolder>() {
    class MeasurementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateOfMeasurementInputText)
        val timeTextView: TextView = itemView.findViewById(R.id.hourInputText)
        val bloodPressureTextView: TextView = itemView.findViewById(R.id.bloodPressureInputText)
        val pulseTextView: TextView = itemView.findViewById(R.id.pulseInputText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasurementViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.enter_measurment_app, parent, false)
        return MeasurementViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MeasurementViewHolder, position: Int) {
        val currentMeasurement = measurements[position]

        // Sprawdź, czy pomiar należy do aktualnie zalogowanego użytkownika
        if (currentMeasurement.userID == currentUserUid) {
            // Jeśli pomiar należy do zalogowanego użytkownika, wyświetl go
            holder.dateTextView.text = currentMeasurement.date
            holder.timeTextView.text = currentMeasurement.hour
            holder.bloodPressureTextView.text = currentMeasurement.bloodPressure
            holder.pulseTextView.text = currentMeasurement.pulse
        } else {
            // Jeśli pomiar nie należy do zalogowanego użytkownika, ukryj ten element listy
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        }
    }

    override fun getItemCount(): Int {
        // Zwróć liczbę pomiarów należących do aktualnie zalogowanego użytkownika
        return measurements.count { it.userID == currentUserUid }
    }
}