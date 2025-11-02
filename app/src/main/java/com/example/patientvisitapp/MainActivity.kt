package com.example.patientvisitapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.example.patientvisitapp.ui.screens.AssessmentScreen
import com.example.patientvisitapp.ui.screens.ListScreen
import com.example.patientvisitapp.ui.screens.RegistrationScreen
import com.example.patientvisitapp.ui.screens.VitalsScreen
import com.example.patientvisitapp.ui.theme.PatientVisitAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PatientVisitAppTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "registration") {
                        composable("registration") { RegistrationScreen(navController) }

                        // vitals expects a patientId argument: vitals/{patientId}
                        composable(
                            route = "vitals/{patientId}",
                            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
                            VitalsScreen(navController, patientId)
                        }

                        // general assessment accepts patientId
                        composable(
                            route = "general/{patientId}",
                            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
                            AssessmentScreen(navController, type = "general", patientId = patientId)
                        }

                        // overweight assessment accepts patientId
                        composable(
                            route = "overweight/{patientId}",
                            arguments = listOf(navArgument("patientId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
                            AssessmentScreen(navController, type = "overweight", patientId = patientId)
                        }

                        // listing doesn't need args
                        composable("list") { ListScreen(navController) }
                    }
                }
            }
        }
    }
}
