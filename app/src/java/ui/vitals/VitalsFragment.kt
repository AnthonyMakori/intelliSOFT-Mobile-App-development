package com.example.patientapp.ui.vitals

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.patientapp.databinding.FragmentVitalsBinding
import com.example.patientapp.viewmodel.PatientViewModel
import com.example.patientapp.util.Utils
import java.util.*

class VitalsFragment : Fragment() {

    private var _binding: FragmentVitalsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PatientViewModel by lazy {
        androidx.lifecycle.ViewModelProvider(requireActivity()).get(PatientViewModel::class.java)
    }

    // TODO: in production, receive patientId from nav args. For now, a placeholder:
    private var patientId: String = "P_DEFAULT"

    private var visitDateMillis: Long = System.currentTimeMillis()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {
        _binding = FragmentVitalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // choose visit date
        binding.tvVisitDate.setOnClickListener { showDatePicker() }

        binding.btnCalculate.setOnClickListener {
            val h = binding.etHeight.text.toString().toDoubleOrNull()
            val w = binding.etWeight.text.toString().toDoubleOrNull()
            if (h == null || w == null) {
                Toast.makeText(requireContext(), "Enter height and weight", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val bmi = Utils.calculateBMI(h, w)
            binding.tvBmi.text = "BMI: $bmi"
        }

        binding.btnSave.setOnClickListener {
            val h = binding.etHeight.text.toString().toDoubleOrNull()
            val w = binding.etWeight.text.toString().toDoubleOrNull()
            if (h == null || w == null) {
                Toast.makeText(requireContext(), "Enter height and weight", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.addVitals(patientId, visitDateMillis, h, w) { bmi ->
                if (bmi >= 25.0) {
                    findNavController().navigate(com.example.patientapp.R.id.action_vitals_to_overweightAssessment)
                } else {
                    findNavController().navigate(com.example.patientapp.R.id.action_vitals_to_generalAssessment)
                }
            }
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
