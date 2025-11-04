package com.example.patientvisitapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.patientvisitapp.viewmodel.PatientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(navController: NavController, vm: PatientViewModel = viewModel()) {
    val context = LocalContext.current
    var pid by remember { mutableStateOf("") }
    var first by remember { mutableStateOf("") }
    var last by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }

    Scaffold(topBar = { TopAppBar(title = { Text("Register Patient") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = pid,
                onValueChange = { pid = it },
                label = { Text("Patient ID") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = first,
                onValueChange = { first = it },
                label = { Text("First name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = last,
                onValueChange = { last = it },
                label = { Text("Last name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Button(onClick = { gender = "Male" }) { Text("Male") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { gender = "Female" }) { Text("Female") }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (pid.isBlank() || first.isBlank() || last.isBlank()) {
                    Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                vm.registerPatient(
                    pid,
                    first,
                    last,
                    System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 25,
                    gender
                )
                // Navigate passing patientId
                navController.navigate("vitals/${pid}")
            }) { Text("Register") }
        }
    }
}
