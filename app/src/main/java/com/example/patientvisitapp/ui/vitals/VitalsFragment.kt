
package com.example.patientvisitapp.ui.vitals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.patientvisitapp.R
import com.example.patientvisitapp.viewmodel.PatientViewModel

class VitalsFragment : Fragment() {
    private val vm: PatientViewModel by viewModels({ requireActivity() })
    private val args: VitalsFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_vitals, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tvPatient = view.findViewById<TextView>(R.id.tvPatientId)
        val etHeight = view.findViewById<EditText>(R.id.etHeight)
        val etWeight = view.findViewById<EditText>(R.id.etWeight)
        val tvBmi = view.findViewById<TextView>(R.id.tvBmi)
        val btnCalc = view.findViewById<Button>(R.id.btnCalculate)
        val btnSave = view.findViewById<Button>(R.id.btnSaveVitals)

        val patientId = args.patientId
        tvPatient.text = "Patient ID: $patientId"

        var bmi = 0.0
        btnCalc.setOnClickListener {
            val h = etHeight.text.toString().toDoubleOrNull() ?: 0.0
            val w = etWeight.text.toString().toDoubleOrNull() ?: 0.0
            val hm = h/100.0
            bmi = if (hm > 0) kotlin.math.round((w/(hm*hm))*100)/100.0 else 0.0
            tvBmi.text = "BMI: $bmi"
        }

        btnSave.setOnClickListener {
            val h = etHeight.text.toString().toDoubleOrNull() ?: 0.0
            val w = etWeight.text.toString().toDoubleOrNull() ?: 0.0
            vm.addVitals(patientId, System.currentTimeMillis(), h, w) { calculatedBmi ->
                if (calculatedBmi >= 25.0) {
                    val action = VitalsFragmentDirections.actionVitalsToOverweightAssessment(patientId)
                    findNavController().navigate(action)
                } else {
                    val action = VitalsFragmentDirections.actionVitalsToGeneralAssessment(patientId)
                    findNavController().navigate(action)
                }
            }
        }
    }
}
