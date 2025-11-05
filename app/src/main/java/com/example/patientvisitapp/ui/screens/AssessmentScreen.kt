package com.example.patientvisitapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
fun AssessmentScreen(
    navController: NavController,
    type: String,
    patientId: String,
    vm: PatientViewModel = viewModel()
) {
    val context = LocalContext.current
    var comments by remember { mutableStateOf("") }
    val generalHealth by remember { mutableStateOf("Good") }
    val extraYesNo by remember { mutableStateOf("No") }

    Scaffold(topBar = { TopAppBar(title = { Text("Assessment - $type") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Patient ID: $patientId")
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = comments,
                onValueChange = { comments = it },
                label = { Text("Comments") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (comments.isBlank()) {
                    Toast.makeText(context, "Please add comments", Toast.LENGTH_SHORT).show(); return@Button
                }
                vm.addAssessment(
                    patientId, System.currentTimeMillis(), type, generalHealth,
                    if (type == "general") "Yes" else null,
                    if (type == "overweight") extraYesNo else null,
                    comments
                )
                navController.navigate("list") // after saving go to listing
            }) { Text("Save Assessment") }
        }
    }
}