package com.example.patientapp.ui.assessment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.patientapp.databinding.FragmentOverweightAssessmentBinding
import com.example.patientapp.viewmodel.PatientViewModel
import java.util.*

class OverweightAssessmentFragment : Fragment() {
    private var _binding: FragmentOverweightAssessmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PatientViewModel by lazy {
        androidx.lifecycle.ViewModelProvider(requireActivity()).get(PatientViewModel::class.java)
    }

    private var visitDateMillis: Long = System.currentTimeMillis()
    private var patientId: String = "P_DEFAULT"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOverweightAssessmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tvVisitDate.setOnClickListener { showDatePicker() }

        binding.btnSave.setOnClickListener {
            val generalHealth = if (binding.rbGood.isChecked) "Good" else "Poor"
            val usingDrugs = if (binding.rbDrugYes.isChecked) "Yes" else "No"
            val comments = binding.etComments.text.toString().trim()

            if (comments.isEmpty()) {
                Toast.makeText(requireContext(), "Please add comments", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.addAssessment(patientId, visitDateMillis, "overweight", generalHealth, null, usingDrugs, comments)
            findNavController().navigate(com.example.patientapp.R.id.action_overweight_to_list)
        }
    }

    private fun showDatePicker() {
        val c = Calendar.getInstance()
        val dp = DatePickerDialog(requireContext(), { _, y, m, d ->
            val cal = Calendar.getInstance()
            cal.set(y, m, d, 0, 0, 0)
            visitDateMillis = cal.timeInMillis
            binding.tvVisitDate.text = android.text.format.DateFormat.format("yyyy-MM-dd", cal)
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
        dp.show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
