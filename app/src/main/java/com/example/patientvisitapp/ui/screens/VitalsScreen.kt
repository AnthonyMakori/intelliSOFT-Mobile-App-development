package com.example.patientvisitapp.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.patientvisitapp.viewmodel.PatientViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitalsScreen(navController: NavController, vm: PatientViewModel, patientId: String) {
    val context = LocalContext.current

    // Collect patient details to show their name
    val patient = vm.currentPatient.collectAsState(initial = null)

    var visitDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

    // Load patient data when screen opens
    LaunchedEffect(patientId) {
        vm.loadPatientById(patientId)
    }

    // Date Picker function
    fun showDatePicker(initialDate: Long, onPick: (Long) -> Unit) {
        val calendar = Calendar.getInstance()
        if (initialDate > 0) calendar.timeInMillis = initialDate

        DatePickerDialog(
            context,
            { _, y, m, d ->
                calendar.set(y, m, d, 0, 0)
                onPick(calendar.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Vitals Form") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(46.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Show Patient Full Name
            Text(
                text = patient.value?.let { "${it.firstName} ${it.lastName}" } ?: "Loading patient...",
                style = MaterialTheme.typography.titleMedium
            )

            // Visit Date (with DatePicker)
            OutlinedTextField(
                value = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date(visitDateMillis)),
                onValueChange = {},
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker(visitDateMillis) { visitDateMillis = it } },
                label = { Text("Visit Date (tap to set)") }
            )

            // Height
            OutlinedTextField(
                value = height,
                onValueChange = { height = it },
                label = { Text("Height (cm)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Weight
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Weight (kg)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Save Button → Follow PDF Instructions Strictly
            Button(
                onClick = {
                    val h = height.toDoubleOrNull() ?: 0.0
                    val w = weight.toDoubleOrNull() ?: 0.0
                    val bmi = if (h > 0.0) (w / ((h / 100) * (h / 100))) else 0.0

                    if (h <= 0 || w <= 0) {
                        Toast.makeText(context, "Please enter valid height & weight", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    vm.addVitals(patientId, visitDateMillis, h, w) {
                        if (bmi <= 25.0) {
                            navController.navigate("generalAssessment/$patientId")
                        } else {
                            navController.navigate("overweightAssessment/$patientId")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save & Continue")
            }

            // Close Button
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Close")
            }
        }
    }
}
