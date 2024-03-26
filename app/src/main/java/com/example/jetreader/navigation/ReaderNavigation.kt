package com.example.jetreader.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jetreader.screens.SplashScreen
import com.example.jetreader.screens.details.BookDetailsScreen
import com.example.jetreader.screens.home.HomeScreen
import com.example.jetreader.screens.home.HomeScreenViewModel
import com.example.jetreader.screens.login.LoginScreen
import com.example.jetreader.screens.search.SearchScreen
import com.example.jetreader.screens.search.SearchScreenViewModelV2
import com.example.jetreader.screens.statsScreen.StatsScreen
import com.example.jetreader.screens.update.UpdateScreen

@Composable
fun ReaderNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = ReaderScreens.SplashScreen.name){
        composable(ReaderScreens.SplashScreen.name){
            SplashScreen(navController)
        }
        composable(ReaderScreens.LoginScreen.name){
            LoginScreen(navController)
        }
        composable(ReaderScreens.HomeScreen.name){
            val homeViewModel = hiltViewModel<HomeScreenViewModel>()
            HomeScreen(navController, homeViewModel)
        }

        val detailName = ReaderScreens.BookDetailsScreen.name
        composable("$detailName/{bookId}", arguments = listOf(navArgument("bookId"){
            type = NavType.StringType
        })){ backStackEntry ->
            backStackEntry.arguments?.getString("bookId").let{
                BookDetailsScreen(navController, it.toString())
            }
        }
        composable(ReaderScreens.SearchScreen.name){
            val searchScreenViewModel = hiltViewModel<SearchScreenViewModelV2>()
            SearchScreen(navController,searchScreenViewModel)
        }
        val updateName = ReaderScreens.UpdateScreen.name
        composable("$updateName/{bookItemId}", arguments = listOf(navArgument("bookItemId"){
            type = NavType.StringType
        })){
            it.arguments?.getString("bookItemId").let {id ->
                val homeViewModel = hiltViewModel<HomeScreenViewModel>()
                UpdateScreen(navController,id,homeViewModel)
            }

        }
        composable(ReaderScreens.StatsScreen.name){
            StatsScreen(navController)
        }
    }
}