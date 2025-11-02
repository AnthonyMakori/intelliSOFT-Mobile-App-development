
package com.example.patientvisitapp.ui.listing

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.patientvisitapp.R
import com.example.patientvisitapp.viewmodel.PatientViewModel
import java.util.*

class PatientListFragment : Fragment() {
    private val vm: PatientViewModel by viewModels({ requireActivity() })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_patient_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tvFilter = view.findViewById<TextView>(R.id.tvFilterDate)
        val rv = view.findViewById<RecyclerView>(R.id.rvPatients)
        val adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            private var items = listOf<com.example.patientvisitapp.viewmodel.PatientListItem>()
            fun setItems(list: List<com.example.patientvisitapp.viewmodel.PatientListItem>) { items = list; notifyDataSetChanged() }
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.item_patient, parent, false)
                return object : RecyclerView.ViewHolder(v) {}
            }
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val p = items[position]
                val tvName = holder.itemView.findViewById<TextView>(R.id.tvName)
                val tvAge = holder.itemView.findViewById<TextView>(R.id.tvAge)
                val tvStatus = holder.itemView.findViewById<TextView>(R.id.tvStatus)
                tvName.text = p.name
                tvAge.text = "Age: ${p.age}"
                tvStatus.text = p.bmiStatus ?: "-"
            }
            override fun getItemCount(): Int = items.size
        }
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        vm.patientList.observe(viewLifecycleOwner) { list ->
            adapter.setItems(list)
        }

        tvFilter.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d ->
                val cal = Calendar.getInstance(); cal.set(y,m,d,0,0,0)
                vm.filterByVisitDate(cal.timeInMillis)
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }
    }
}
