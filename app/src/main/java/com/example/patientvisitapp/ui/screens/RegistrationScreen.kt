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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(navController: NavController, vm: PatientViewModel) {
    val context = LocalContext.current

    // Form state
    var patientNumber by remember { mutableStateOf("") } // allow manual entry or auto-generate
    var registrationDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var dobMillis by remember { mutableStateOf(0L) }
    var gender by remember { mutableStateOf("") }

    // Gender dropdown
    val genderOptions = listOf("Male", "Female", "Other")
    var expanded by remember { mutableStateOf(false) }

    // Date pickers helpers
    fun showDatePicker(initialMillis: Long, onPick: (Long) -> Unit) {
        val cal = Calendar.getInstance()
        if (initialMillis > 0L) cal.timeInMillis = initialMillis
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(context, { _, y, m, d ->
            val newCal = Calendar.getInstance()
            newCal.set(y, m, d, 0, 0, 0)
            onPick(newCal.timeInMillis)
        }, year, month, day).show()
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Register Patient") })
    }) { padding ->
        Column(
            modifier = Modifier
                .padding(
                    PaddingValues(
                        start = 16.dp,
                        top = 46.dp,
                        end = 16.dp,
                        bottom = padding.calculateBottomPadding()
                    )
                )

                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = patientNumber,
                onValueChange = { patientNumber = it },
                label = { Text("Patient Number") },
                modifier = Modifier.fillMaxWidth()
            )

            // Registration date
            OutlinedTextField(
                value = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date(registrationDateMillis)),
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker(registrationDateMillis) { registrationDateMillis = it } },
                enabled = false,
                label = { Text("Registration Date (tap to change)") }
            )

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth()
            )

            // DOB date picker
            OutlinedTextField(
                value = if (dobMillis == 0L) "" else java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(dobMillis)),
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker(dobMillis.takeIf { it > 0L } ?: System.currentTimeMillis()) { dobMillis = it } },
                enabled = false,
                label = { Text("Date of Birth (tap to set)") }
            )

            // Gender dropdown
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = { gender = it },
                    readOnly = true,
                    label = { Text("Gender") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    genderOptions.forEach { selection ->
                        DropdownMenuItem(
                            text = { Text(selection) },
                            onClick = {
                                gender = selection
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Buttons row: Save and Close
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = {
                    // Basic validation
                    if (firstName.isBlank() || lastName.isBlank() || dobMillis == 0L) {
                        Toast.makeText(context, "Please fill first name, last name and date of birth", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Generate patient id if empty
                    val pid = if (patientNumber.isBlank()) {
                        UUID.randomUUID().toString().take(8) // short id
                    } else patientNumber

                    // call VM to insert patient
                    vm.addPatient(pid, registrationDateMillis, firstName.trim(), lastName.trim(), dobMillis, gender.trim()) {
                        // success callback - navigate to vitals screen for this patient
                        navController.navigate("vitals/${pid}")
                    }
                }) {
                    Text("Save")
                }

                OutlinedButton(onClick = { navController.popBackStack() }) {
                    Text("Close")
                }
            }
        }
    }
}
