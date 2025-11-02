
package com.example.patientvisitapp.ui.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.patientvisitapp.R
import com.example.patientvisitapp.viewmodel.PatientViewModel

class RegistrationFragment : Fragment() {
    private val vm: PatientViewModel by viewModels({ requireActivity() })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val etPatientId = view.findViewById<EditText>(R.id.etPatientId)
        val etFirst = view.findViewById<EditText>(R.id.etFirstName)
        val etLast = view.findViewById<EditText>(R.id.etLastName)
        val rgGender = view.findViewById<RadioGroup>(R.id.rgGender)
        val btn = view.findViewById<Button>(R.id.btnRegister)

        btn.setOnClickListener {
            val pid = etPatientId.text.toString().trim()
            val first = etFirst.text.toString().trim()
            val last = etLast.text.toString().trim()
            val gender = if (rgGender.checkedRadioButtonId == R.id.rbFemale) "Female" else "Male"
            if (pid.isEmpty() || first.isEmpty() || last.isEmpty()) return@setOnClickListener
            vm.registerPatient(pid, first, last, System.currentTimeMillis() - 1000L*60*60*24*365*25, gender)
            val action = com.example.patientvisitapp.ui.registration.RegistrationFragmentDirections.actionRegistrationToVitals(pid)
            findNavController().navigate(action)
        }
    }
}
