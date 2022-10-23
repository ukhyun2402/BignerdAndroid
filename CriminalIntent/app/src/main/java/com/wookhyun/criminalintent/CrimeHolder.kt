package com.wookhyun.criminalintent

import android.text.format.DateFormat
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.wookhyun.criminalintent.databinding.ListItemCrimeBinding
import java.util.*

class CrimeHolder(
    private val binding: ListItemCrimeBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(crime: Crime, onCrimeClicked: (crimeId: UUID) -> Unit, onDelete: (crime:Crime) -> Unit) {
        binding.crimeTitle.text = crime.title
        binding.crimeDate.text = DateFormat.format("yyyy-MM-dd hh:mm:ss", crime.date)

        binding.root.setOnClickListener {
            onCrimeClicked(crime.id)
        }

        binding.crimeDelete.setOnClickListener {
            onDelete(crime)
        }
        binding.crimeSolved.visibility = if (crime.isSolved) View.VISIBLE else View.GONE
    }
}