package com.wookhyun.criminalintent

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import java.util.*

private const val TAG = "TimePickerFragment"
class TimePickerFragment : DialogFragment() {
    private val args: DatePickerFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val listener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            Log.d(TAG, "onCreateDialog: HELLO")
            setFragmentResult(REQUEST_KEY, bundleOf(RESPONSE_KEY to listOf<Int>(hourOfDay, minute)))
        }
        val calendar = Calendar.getInstance()
        calendar.time = args.crimeDate
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)

        return TimePickerDialog(
            requireContext(),
            listener,
            hour,
            minute,
            true
        )
    }

    companion object {
        const val REQUEST_KEY = "REQUEST_TIME_KEY"
        const val RESPONSE_KEY = "RESPONSE_TIME_KEY"
    }
}