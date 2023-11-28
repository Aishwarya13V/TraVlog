package com.example.travlog

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.travlog.detail.DetailScreen
import com.example.travlog.detail.DetailViewModel
import com.example.travlog.home.Home
import com.example.travlog.home.HomeViewModel
import com.example.travlog.login.LoginScreen
import com.example.travlog.login.LoginViewModel
import com.example.travlog.login.SignUpScreen

enum class LoginRoutes{
    SignUp,
    SignIn
}

enum class HomeRoutes{
    Home,
    Detail
}

enum class NestedRoutes{
    Main,
    Login
}

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel,
    detailViewModel: DetailViewModel,
    homeViewModel: HomeViewModel
){
    NavHost(
        navController = navController,
        startDestination = NestedRoutes.Main.name
    ){
        authGraph(navController, loginViewModel)
        homeGraph(navController, detailViewModel, homeViewModel)
    }

}

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel
){
    navigation(
        startDestination = LoginRoutes.SignIn.name,
        route = NestedRoutes.Login.name
    ){
        composable(route = LoginRoutes.SignIn.name){
            LoginScreen(onNavToHomepage = {
                navController.navigate(HomeRoutes.Home.name){
                    launchSingleTop = true
                    popUpTo(route = LoginRoutes.SignIn.name){
                        inclusive = true
                    }
                }
            },
                loginViewModel = loginViewModel
            ) {
                navController.navigate(NestedRoutes.Main.name){
                    launchSingleTop = true
                    popUpTo(LoginRoutes.SignIn.name){
                        inclusive = true
                    }
                }
            }
        }

        composable(route = LoginRoutes.SignUp.name){
            SignUpScreen(onNavToHomepage = {
                navController.navigate(NestedRoutes.Main.name){
                    popUpTo(LoginRoutes.SignUp.name){
                        inclusive = true
                    }
                }
            },
                loginViewModel = loginViewModel
            ) {
                navController.navigate(LoginRoutes.SignIn.name)
            }
        }
    }
}

fun NavGraphBuilder.homeGraph(
    navController: NavHostController,
    detailViewModel: DetailViewModel,
    homeViewModel: HomeViewModel
){
    navigation(
        startDestination = HomeRoutes.Home.name,
        route = NestedRoutes.Main.name
    ){
        composable(HomeRoutes.Home.name){
            Home(
                homeViewModel = homeViewModel,
                onTravlogClick = {id ->
                    navController.navigate(
                        HomeRoutes.Detail.name + "?id=$id"
                    ){
                        launchSingleTop = true
                    }

                },
                navToDetailPage = {
                    navController.navigate(HomeRoutes.Detail.name)
                }
            ) {
                navController.navigate(NestedRoutes.Login.name){
                    launchSingleTop = true
                    popUpTo(0){
                        inclusive = true
                    }
                }
                
            }
        }

        composable(
            route = HomeRoutes.Detail.name + "?id={id}",
            arguments = listOf(navArgument("id"){
                type = NavType.StringType
                defaultValue = ""
            })
        ){entry ->
            DetailScreen(
                detailViewModel = detailViewModel,
                travlogId = entry.arguments?.getString("id") as String
            ) {
                navController.navigateUp()
            }
        }
    }
}