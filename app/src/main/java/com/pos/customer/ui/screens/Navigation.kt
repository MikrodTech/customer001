package com.pos.customer.ui.screens

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pos.customer.viewmodel.HomeViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object TableSelection : Screen("table_selection")
    object Menu : Screen("menu")
    object Cart : Screen("cart")
    object Checkout : Screen("checkout")
    object OrderStatus : Screen("order_status/{orderId}") {
        fun createRoute(orderId: String) = "order_status/$orderId"
    }
}

@Composable
fun POSNavigation(
    navController: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToTableSelection = {
                    navController.navigate(Screen.TableSelection.route)
                },
                onNavigateToMenu = {
                    navController.navigate(Screen.Menu.route)
                },
                onNavigateToOrderStatus = { orderId ->
                    navController.navigate(Screen.OrderStatus.createRoute(orderId))
                },
                onNavigateToCart = {
                    navController.navigate(Screen.Cart.route)
                },
                viewModel = homeViewModel
            )
        }

        composable(Screen.TableSelection.route) {
            TableSelectionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onTableSelected = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Menu.route) {
            MenuScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCart = {
                    navController.navigate(Screen.Cart.route)
                }
            )
        }

        composable(Screen.Cart.route) {
            CartScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCheckout = {
                    navController.navigate(Screen.Checkout.route)
                }
            )
        }

        composable(Screen.Checkout.route) {
            CheckoutScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToOrderStatus = { orderId ->
                    navController.navigate(Screen.OrderStatus.createRoute(orderId)) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.OrderStatus.route,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderStatusScreen(
                orderId = orderId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
