package com.example.myapplicationkardioasystent.recyclerView

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplicationkardioasystent.R
import com.example.myapplicationkardioasystent.cloudFirestore.Measurment

class MeasurementAdapter(val measurements: MutableList<Measurment>, val intent: Intent) : RecyclerView.Adapter<MeasurementAdapter.MeasurementViewHolder>() {
    class MeasurementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val bloodPressureTextView: TextView = itemView.findViewById(R.id.bloodPressureTextView)
        val pulseTextView: TextView = itemView.findViewById(R.id.pulseTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasurementViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.measurment_item, parent, false)
        return MeasurementViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MeasurementViewHolder, position: Int) {
        val currentMeasurement = measurements[position]

        // Sprawdź, czy pomiar należy do aktualnie zalogowanego użytkownika
        if (currentMeasurement.userID == intent.getStringExtra("userID").toString()) {
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
        return measurements.count { it.userID == intent.getStringExtra("userID").toString() }
    }
}