package com.example.patientvisitapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import com.example.patientvisitapp.ui.screens.AssessmentScreen
import com.example.patientvisitapp.ui.screens.ListScreen
import com.example.patientvisitapp.ui.screens.RegistrationScreen
import com.example.patientvisitapp.ui.screens.VitalsScreen
import com.example.patientvisitapp.ui.theme.PatientVisitAppTheme
import com.example.patientvisitapp.viewmodel.PatientViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PatientVisitAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {

                    //  Navigation controller
                    val navController = rememberNavController()

                    //  Shared ViewModel across all screens
                    val vm: PatientViewModel = viewModel()

                    //  Navigation graph
                    NavHost(navController = navController, startDestination = "registration") {

                        // 🔹 Registration screen
                        composable("registration") {
                            RegistrationScreen(navController = navController, vm = vm)
                        }

                        // 🔹 Vitals screen (patientId passed as argument)
                        composable(
                            route = "vitals/{patientId}",
                            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
                            VitalsScreen(navController = navController, vm = vm, patientId = patientId)
                        }

                        // 🔹 General assessment screen
                        composable(
                            route = "general/{patientId}",
                            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
                            AssessmentScreen(
                                navController = navController,
                                vm = vm,
                                type = "general",
                                patientId = patientId
                            )
                        }

                        // 🔹 Overweight assessment screen
                        composable(
                            route = "overweight/{patientId}",
                            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
                            AssessmentScreen(
                                navController = navController,
                                vm = vm,
                                type = "overweight",
                                patientId = patientId
                            )
                        }

                        // 🔹 List screen
                        composable("list") {
                            ListScreen(navController = navController, vm = vm)
                        }

    

                    }
                }
            }
        }
    }
}