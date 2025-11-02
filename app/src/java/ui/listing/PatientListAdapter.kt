package com.example.patientapp.ui.listing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.patientapp.databinding.ItemPatientBinding
import com.example.patientapp.viewmodel.PatientListItem

class PatientListAdapter : ListAdapter<PatientListItem, PatientListAdapter.VH>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<PatientListItem>() {
            override fun areItemsTheSame(oldItem: PatientListItem, newItem: PatientListItem) = oldItem.patientId == newItem.patientId
            override fun areContentsTheSame(oldItem: PatientListItem, newItem: PatientListItem) = oldItem == newItem
        }
    }

    inner class VH(private val b: ItemPatientBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: PatientListItem) {
            b.tvName.text = item.name
            b.tvAge.text = "Age: ${item.age}"
            b.tvStatus.text = item.bmiStatus ?: "-"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemPatientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }
}
