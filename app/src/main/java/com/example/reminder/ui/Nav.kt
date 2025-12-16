package com.example.reminder.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import java.time.LocalDate

@Composable
fun AppNav(vm: AppViewModel, modifier: Modifier = Modifier) {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = "calendar", modifier = modifier) {
        composable("calendar") {
            CalendarScreen(
                vm = vm,
                onOpenDay = { date -> nav.navigate("day/${date}") }
            )
        }
        composable(
            route = "day/{date}",
            arguments = listOf(navArgument("date") { type = NavType.StringType })
        ) { backStack ->
            val dateStr = backStack.arguments?.getString("date")!!
            val date = LocalDate.parse(dateStr)
            DayDetailsScreen(
                vm = vm,
                date = date,
                onBack = { nav.popBackStack() }
            )
        }

    }
}
