package com.example.patientvisitapp.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.patientvisitapp.viewmodel.PatientViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ListScreen(navController: NavController, vm: PatientViewModel = viewModel()) {
    val list by vm.patientList.observeAsState(emptyList())
    Scaffold(topBar = { TopAppBar(title = { Text("Patients") }) }) { padding ->
        LazyColumn(modifier = Modifier.padding(8.dp)) {
            items(list) { item ->
                Card(modifier = Modifier.padding(8.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = item.name, style = MaterialTheme.typography.h6)
                        Text("Age: ${'$'}{item.age}")
                        Text("BMI status: ${'$'}{item.bmiStatus ?: "-"}")
                    }
                }
            }
        }
    }
}
