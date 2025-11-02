package com.example.patientvisitapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.patientvisitapp.viewmodel.PatientViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun VitalsScreen(navController: NavController, patientId: String, vm: PatientViewModel = viewModel()) {
    val context = LocalContext.current
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var bmi by remember { mutableStateOf(0.0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Patient ID: $patientId")
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = height, onValueChange = { height = it }, label = { Text("Height (cm)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight (kg)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            val h = height.toDoubleOrNull()
            val w = weight.toDoubleOrNull()
            if (h == null || w == null) { Toast.makeText(context, "Enter numbers", Toast.LENGTH_SHORT).show(); return@Button }
            val hm = h / 100.0
            bmi = if (hm > 0) kotlin.math.round((w / (hm * hm)) * 100) / 100.0 else 0.0
        }) { Text("Calculate BMI") }
        Spacer(modifier = Modifier.height(8.dp))
        Text("BMI: $bmi")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val h = height.toDoubleOrNull() ?: 0.0
            val w = weight.toDoubleOrNull() ?: 0.0
            // Use patientId passed via nav argument
            vm.addVitals(patientId, System.currentTimeMillis(), h, w) { calculatedBmi ->
                if (calculatedBmi >= 25.0) {
                    navController.navigate("overweight/${patientId}")
                } else {
                    navController.navigate("general/${patientId}")
                }
            }
        }) { Text("Save Vitals") }
    }
}
