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
fun AssessmentScreen(navController: NavController, type: String, patientId: String, vm: PatientViewModel = viewModel()) {
    val context = LocalContext.current
    var comments by remember { mutableStateOf("") }
    var generalHealth by remember { mutableStateOf("Good") }
    var extraYesNo by remember { mutableStateOf("No") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Assessment - $type")
        Spacer(modifier = Modifier.height(8.dp))
        Text("Patient ID: $patientId")
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = comments, onValueChange = { comments = it }, label = { Text("Comments") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (comments.isBlank()) { Toast.makeText(context, "Please add comments", Toast.LENGTH_SHORT).show(); return@Button }
            vm.addAssessment(patientId, System.currentTimeMillis(), type, generalHealth,
                if (type == "general") "Yes" else null,
                if (type == "overweight") extraYesNo else null,
                comments)
            navController.navigate("list") // after saving go to listing
        }) { Text("Save Assessment") }
    }
}
