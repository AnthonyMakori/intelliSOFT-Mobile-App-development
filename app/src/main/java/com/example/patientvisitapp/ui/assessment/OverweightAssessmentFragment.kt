
package com.example.patientvisitapp.ui.assessment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.patientvisitapp.R
import com.example.patientvisitapp.viewmodel.PatientViewModel

class OverweightAssessmentFragment : Fragment() {
    private val vm: PatientViewModel by viewModels({ requireActivity() })
    private val args: OverweightAssessmentFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_overweight_assessment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val spHealth = view.findViewById<Spinner>(R.id.spGeneralHealth)
        val rgDrugs = view.findViewById<RadioGroup>(R.id.rgUsingDrugs)
        val etComments = view.findViewById<EditText>(R.id.etComments)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        val pid = args.patientId
        btnSave.setOnClickListener {
            val general = spHealth.selectedItem?.toString() ?: "Good"
            val usingDrugs = if (rgDrugs.checkedRadioButtonId == R.id.rbDrugsYes) "Yes" else "No"
            val comments = etComments.text.toString()
            vm.addAssessment(pid, System.currentTimeMillis(), "overweight", general, null, usingDrugs, comments)
            findNavController().navigate(OverweightAssessmentFragmentDirections.actionOverweightToList())
        }
    }
}
