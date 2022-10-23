package com.wookhyun.criminalintent

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.wookhyun.criminalintent.challenge.PictureZoomInFragment
import com.wookhyun.criminalintent.databinding.FragmentCrimeDetailBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.math.log

private const val TAG = "CrimeDetailFragment"
private const val DATE_FORMAT = "EEE, MMM, dd"

class CrimeDetailFragment : Fragment() {
    private val args: CrimeDetailFragmentArgs by navArgs()
    private val crimeDetailViewModel: CrimeDetailViewModel by viewModels {
        CrimeDetailViewModelFactory(args.crimeId)
    }
    private val selectSuspect = registerForActivityResult(
        ActivityResultContracts.PickContact()
    )
    { uri: Uri? ->
        uri?.let { parseContentSelection(uri) }
    }

    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto: Boolean ->
        if (didTakePhoto && photoName != null) {
            crimeDetailViewModel.updateCrime { oldCrime ->
                oldCrime.copy(photoFileName = photoName)
            }
        }
    }

    private var photoName: String? = null
    private var _binding: FragmentCrimeDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Can not access binding because it is null"
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 3번쨰 파라미터인 attachToParent는 inflate를 할 때 적용할 지 안할지에 대한 파라미터
        _binding = FragmentCrimeDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val backButtonDispatcher = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Toast.makeText(requireActivity(), "Please Enter a Ttile", Toast.LENGTH_SHORT).show()
                return
            }
        }
        binding.apply {
            crimeTitle.doOnTextChanged { text, _, _, _ ->
                requireActivity().onBackPressedDispatcher.addCallback(
                    requireActivity(),
                    backButtonDispatcher
                )
                backButtonDispatcher.isEnabled = text?.isEmpty() ?: false
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(title = text.toString())
                }
            }

            crimeSolved.setOnCheckedChangeListener { _, isChecked ->
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(isSolved = isChecked)
                }
            }

            crimeSuspect.setOnClickListener {
                selectSuspect.launch(null)
            }

            val selectSuspectIntent = selectSuspect.contract.createIntent(requireContext(), null)
            crimeSuspect.isEnabled = canResolveIntent(selectSuspectIntent)

            crimeCamera.setOnClickListener {
                Log.d(TAG, "onViewCreated: Click CrimeCamera Button ")
                photoName = "IMG_${Date()}.JPG"
                val photoFile = File(requireContext().applicationContext.filesDir, photoName)
                val photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.wookhyun.criminalintent.fileprovider",
                    photoFile
                )
                takePhoto.launch(photoUri)
            }

            val captureImageIntent = takePhoto.contract.createIntent(
                requireContext(),
                Uri.parse("")
            )
//            Log.d(TAG, "canResolveIntent: ${canResolveIntent(captureImageIntent)}\n checkCamera: ${checkCameraHardware(requireContext())}")
            crimeCamera.isEnabled = checkCameraHardware(requireContext())

            // Set Event to Image View
            binding.crimePhoto.setOnClickListener { view ->
                if(view.tag != null) {
                    PictureZoomInFragment(view.tag as String).show(
                        childFragmentManager, "ZoomInPicture"
                    )
                }
            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                crimeDetailViewModel.crime.collect { crime ->
                    crime?.let {
                        updateUi(crime)
                    }
                }
            }
        }

        setFragmentResultListener(DatePickerFragment.REQUEST_KEY_DATE) { _, bundle ->
            val newDate = bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
            crimeDetailViewModel.updateCrime { it.copy(date = newDate) }
        }

        setFragmentResultListener(TimePickerFragment.REQUEST_KEY) { _, bundle ->
            val (hour, minute) = bundle.getSerializable(TimePickerFragment.RESPONSE_KEY) as List<Int>
            crimeDetailViewModel.updateCrime { crime ->
                Log.d(TAG, "onViewCreated: ${crime.date}")
                crime.copy(date = crime.date.let { date ->
                    val cal = Calendar.getInstance()
                    cal.time = date
                    cal.set(Calendar.HOUR, 13)
                    cal.set(Calendar.MINUTE, 24)
                    cal.time
                })
            }
        }
    }

    private fun updateUi(crime: Crime) {
        binding.apply {
            if (crimeTitle.text.toString() != crime.title) {
                crimeTitle.setText(crime.title)
            }
            crimeDate.text = crime.date.toString()
            crimeDate.setOnClickListener {
                findNavController().navigate(
                    CrimeDetailFragmentDirections.selectDate(crime.date)
                )
            }
            crimeTime.setOnClickListener {
                findNavController().navigate(
                    CrimeDetailFragmentDirections.selectTime(crime.date)
                )
            }

            crimeSolved.isChecked = crime.isSolved
            crimeReport.setOnClickListener {
                val reportIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, getCrimeReport(crime))
                    putExtra(
                        Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject)
                    )
                }
                val chooseIntent = Intent.createChooser(
                    reportIntent,
                    getString(R.string.send_report)
                )
                startActivity(chooseIntent)
            }
            crimeSuspect.text = crime.suspect.ifEmpty {
                getString(R.string.crime_suspect_text)
            }

            updatePhoto(crime.photoFileName)
        }
    }

    private fun getCrimeReport(crime: Crime): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }
        val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()
        val suspectText = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_subject)
        } else {
            getString(R.string.crime_report_subject, crime.suspect)
        }
        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspectText)
    }

    private fun parseContentSelection(contactUri: Uri) {
        val queryField = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
        val queryCursor =
            requireActivity().contentResolver.query(contactUri, queryField, null, null, null)

        queryCursor?.use { cursor ->
            if (cursor.moveToFirst()) {
                val suspect = cursor.getString(0)
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(suspect = suspect)
                }
            }
        }
    }

    private fun canResolveIntent(intent: Intent): Boolean {
        intent.addCategory(Intent.CATEGORY_HOME)
        val packageManager = requireActivity().packageManager
        val resolveActivity: ResolveInfo? = packageManager.resolveActivity(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        return resolveActivity != null
    }

    private fun checkCameraHardware(context: Context): Boolean {
        if(context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA))
        {
            return true
        }
        return false
    }
    private fun updatePhoto(photoFileNmae: String?) {
        if (binding.crimePhoto.tag != photoFileNmae) {
            val photoFile = photoFileNmae?.let {
                File(requireContext().applicationContext.filesDir, it)
            }

            if (photoFile?.exists() == true) {
                binding.crimePhoto.doOnLayout { measuredView ->
                    val scaledBitmap =
                        getScaledBitmap(photoFile.path, measuredView.width, measuredView.height)
                    binding.crimePhoto.setImageBitmap(scaledBitmap)
                    binding.crimePhoto.tag = photoFileNmae
                }
            } else {
                 binding.crimePhoto.setImageBitmap(null)
                binding.crimePhoto.tag = null
            }
        }
    }
}
