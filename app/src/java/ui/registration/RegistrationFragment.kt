package com.example.patientVisitapp.ui.registration

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.patientVisitapp.databinding.FragmentRegistrationBinding
import com.example.patientVisitapp.viewmodel.PatientViewModel
import java.util.*

class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    // TODO: obtain ViewModel using your factory / DI
    private val viewModel: PatientViewModel by lazy {
        // Replace with proper ViewModelProvider if you have a factory
        androidx.lifecycle.ViewModelProvider(requireActivity()).get(PatientViewModel::class.java)
    }

    private var dobMillis: Long = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tvDob.setOnClickListener {
            showDobPicker()
        }

        binding.btnRegister.setOnClickListener {
            val pid = binding.etPatientId.text.toString().trim()
            val first = binding.etFirstName.text.toString().trim()
            val last = binding.etLastName.text.toString().trim()
            val gender = if (binding.rbMale.isChecked) "Male" else "Female"

            if (pid.isEmpty() || first.isEmpty() || last.isEmpty() || dobMillis == 0L) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.registerPatient(pid, first, last, dobMillis, gender)
            viewModel.registrationState.observe(viewLifecycleOwner) { result ->
                result.onSuccess {
                    // Navigate to vitals with patient id as argument (use SafeArgs for production)
                    val action = com.example.patientVisitapp.ui.registration.RegistrationFragmentDirections.actionRegistrationToVitals(pid)
                    // if you want to pass patientId, add an argument to nav_graph and to action above
                    findNavController().navigate(action)
                }
                result.onFailure { ex ->
                    Toast.makeText(requireContext(), ex.message ?: "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showDobPicker() {
        val calendar = Calendar.getInstance()
        val dp = DatePickerDialog(requireContext(), { _, y, m, d ->
            val c = Calendar.getInstance()
            c.set(y, m, d, 0, 0, 0)
            dobMillis = c.timeInMillis
            binding.tvDob.text = android.text.format.DateFormat.format("yyyy-MM-dd", c)
        }, calendar.get(Calendar.YEAR) - 25, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        dp.show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
