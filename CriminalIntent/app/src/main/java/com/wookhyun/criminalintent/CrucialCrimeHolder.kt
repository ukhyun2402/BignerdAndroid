package com.wookhyun.criminalintent

import android.text.format.DateFormat
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.wookhyun.criminalintent.databinding.ListItemCrimeBinding
import com.wookhyun.criminalintent.databinding.ListItemCrucialCrimeBinding

class CrucialCrimeHolder(
    private val binding: ListItemCrucialCrimeBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(crime: Crime) {
        binding.crucialCrimeTitle.text = crime.title
        binding.crucialCrimeDate.text =  DateFormat.format("yyyy-mm-dd",crime.date)

        binding.root.setOnClickListener {
            Toast.makeText(it.context, "${crime.title} clicked!", Toast.LENGTH_SHORT).show()
        }

        binding.crucialCrimeSolved.visibility = if (crime.isSolved) View.VISIBLE else View.GONE
    }
}