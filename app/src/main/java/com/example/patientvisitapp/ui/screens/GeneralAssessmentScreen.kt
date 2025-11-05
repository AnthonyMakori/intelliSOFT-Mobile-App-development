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
import androidx.navigation.NavController
import com.example.patientvisitapp.viewmodel.PatientViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralAssessmentScreen(
    navController: NavController,
    vm: PatientViewModel,
    patientId: String
) {
    val context = LocalContext.current

    var visitDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var generalHealth by remember { mutableStateOf("Good") }    // Good / Poor
    var everOnDiet by remember { mutableStateOf("No") }         // Yes / No
    var comments by remember { mutableStateOf("") }

    // Date picker helper
    fun showDatePicker(initial: Long, onPick: (Long) -> Unit) {
        val cal = Calendar.getInstance()
        if (initial > 0) cal.timeInMillis = initial

        DatePickerDialog(
            context,
            { _, y, m, d ->
                cal.set(y, m, d, 0, 0)
                onPick(cal.timeInMillis)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("General Assessment") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Visit date (tap to change)
            OutlinedTextField(
                value = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(visitDateMillis)),
                onValueChange = {},
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker(visitDateMillis) { visitDateMillis = it } },
                label = { Text("Visit Date (tap to set)") }
            )

            // General Health (Good / Poor)
            Text(text = "General Health", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                listOf("Good", "Poor").forEach { opt ->
                    FilterChip(
                        selected = (generalHealth == opt),
                        onClick = { generalHealth = opt },
                        label = { Text(opt) }
                    )
                }
            }

            // Ever on a diet? (Yes / No)
            Text(text = "Have you ever been on a diet to lose weight?", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                listOf("Yes", "No").forEach { opt ->
                    FilterChip(
                        selected = (everOnDiet == opt),
                        onClick = { everOnDiet = opt },
                        label = { Text(opt) }
                    )
                }
            }

            // Comments (mandatory)
            OutlinedTextField(
                value = comments,
                onValueChange = { comments = it },
                label = { Text("Comments") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 4
            )

            // Save button: validate, save through existing ViewModel.addAssessment, then go to patient list
            Button(
                onClick = {
                    // Validation per PDF: all fields mandatory
                    if (comments.isBlank()) {
                        Toast.makeText(context, "Comments are required", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Use the existing addAssessment() in your ViewModel (type = "general")
                    vm.addAssessment(
                        patientId = patientId,
                        visitDate = visitDateMillis,
                        type = "general",
                        generalHealth = generalHealth,
                        usedDiet = everOnDiet,
                        usingDrugs = null,
                        comments = comments
                    )

                    Toast.makeText(context, "General assessment saved", Toast.LENGTH_SHORT).show()

                    // Redirect to patient listing per PDF requirement
                    navController.navigate("patients") {
                        // remove vitals/general from back stack so user lands on listing cleanly
                        popUpTo("patients") { inclusive = false }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save & Finish")
            }

            // Cancel/back
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back")
            }
        }
    }
}
