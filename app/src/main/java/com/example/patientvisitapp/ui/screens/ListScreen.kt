package com.example.patientvisitapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.patientvisitapp.viewmodel.PatientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(navController: NavController, vm: PatientViewModel = viewModel()) {
    val list by vm.patientList.observeAsState(emptyList())
    Scaffold(topBar = { TopAppBar(title = { Text("Patients") }) }) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(list) { item ->
                Card(modifier = Modifier.padding(8.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = item.name, style = MaterialTheme.typography.headlineSmall)
                        Text("Age: ${item.age}")
                        Text("BMI status: ${item.bmiStatus ?: "-"}")
                    }
                }
            }
        }
    }
}
