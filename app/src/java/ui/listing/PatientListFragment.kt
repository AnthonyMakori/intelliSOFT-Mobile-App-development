package com.example.patientapp.ui.listing

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.patientapp.databinding.FragmentPatientListBinding
import com.example.patientapp.viewmodel.PatientViewModel
import java.util.*

class PatientListFragment : Fragment() {

    private var _binding: FragmentPatientListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PatientViewModel by lazy {
        androidx.lifecycle.ViewModelProvider(requireActivity()).get(PatientViewModel::class.java)
    }

    private lateinit var adapter: PatientListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPatientListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = PatientListAdapter()
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adapter

        // Use ViewModel to observe list of patients (you should expose LiveData/Flow in ViewModel)
        viewModel.patientList.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
        }

        binding.tvFilterDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val c = Calendar.getInstance()
        val dp = DatePickerDialog(requireContext(), { _, y, m, d ->
            val cal = Calendar.getInstance()
            cal.set(y, m, d, 0, 0, 0)
            // Ask ViewModel to filter by this date (viewModel.filterByVisitDate(cal.timeInMillis))
            viewModel.filterByVisitDate(cal.timeInMillis)
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
        dp.show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
