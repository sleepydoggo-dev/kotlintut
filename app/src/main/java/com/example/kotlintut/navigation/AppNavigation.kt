package com.example.kotlintut.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kotlintut.ui.components.DrawerHeader
import com.example.kotlintut.ui.components.DrawerItem
import com.example.kotlintut.ui.screens.*
import com.example.kotlintut.viewmodel.AppViewModel
import com.example.kotlintut.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object AuthGateway : Screen("auth_gateway")
    object Login : Screen("login")
    object Register : Screen("register")
    object Categories : Screen("categories")
    object Products : Screen("products/{category}") {
        fun createRoute(category: String) = "products/$category"
    }
    object ProductDetail : Screen("product_detail")
    object Cart : Screen("cart")
    object Payment : Screen("payment")
    object Success : Screen("success")
    object OrderHistory : Screen("order_history")
}

@Composable
fun AppNavigation(
    appViewModel: AppViewModel,
    productViewModel: ProductViewModel = viewModel()
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val productUiState by productViewModel.uiState.collectAsStateWithLifecycle()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerHeader(username = appViewModel.loggedUser.value ?: "Ospite")
                Spacer(Modifier.height(12.dp))
                DrawerItem("Home", Icons.Default.Home, true) {
                    scope.launch { drawerState.close() }
                    navController.navigate(Screen.Categories.route)
                }
                if (appViewModel.loggedUser.value == null) {
                    DrawerItem("Login", Icons.Default.Login, false) {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.AuthGateway.route)
                    }
                } else {
                    DrawerItem("Carrello", Icons.Default.ShoppingCart, false) {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Cart.route)
                    }
                    DrawerItem("Storico Ordini", Icons.Default.History, false) {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.OrderHistory.route)
                    }
                    Spacer(Modifier.weight(1f))
                    DrawerItem("Logout", Icons.Default.Logout, false) {
                        scope.launch { drawerState.close() }
                        appViewModel.logout()
                        navController.navigate(Screen.AuthGateway.route)
                    }
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = if (appViewModel.loggedUser.value != null) Screen.Categories.route else Screen.AuthGateway.route
        ) {
            composable(Screen.AuthGateway.route) {
                AuthGatewayScreen(
                    onLoginClick = { navController.navigate(Screen.Login.route) },
                    onRegisterClick = { navController.navigate(Screen.Register.route) },
                    onCancelClick = { navController.navigate(Screen.Categories.route) }
                )
            }
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { username ->
                        appViewModel.login(username)
                        navController.navigate(Screen.Categories.route) {
                            popUpTo(Screen.AuthGateway.route) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = { username ->
                        appViewModel.login(username)
                        navController.navigate(Screen.Categories.route) {
                            popUpTo(Screen.AuthGateway.route) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Categories.route) {
                CategoriesScreen(
                    categories = productUiState.categories,
                    onCategoryClick = { category ->
                        productViewModel.selectCategory(category)
                        navController.navigate(Screen.Products.createRoute(category))
                    },
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            }
            composable(
                route = Screen.Products.route,
                arguments = listOf(navArgument("category") { type = NavType.StringType })
            ) {
                ProductsScreen(
                    category = productUiState.selectedCategory ?: "Panini",
                    products = productUiState.products,
                    onProductClick = { product ->
                        productViewModel.selectProduct(product)
                        navController.navigate(Screen.ProductDetail.route)
                    },
                    onBack = { 
                        productViewModel.clearCategorySelection()
                        navController.popBackStack() 
                    }
                )
            }
            composable(Screen.ProductDetail.route) {
                productUiState.selectedProduct?.let { product ->
                    ProductDetailScreen(
                        product = product,
                        attributes = productUiState.productAttributes,
                        isFavorite = false,
                        onFavoriteToggle = { /* TODO */ },
                        onAddToCart = { qty, attrs ->
                            appViewModel.addToCart(product, qty, attrs)
                            navController.popBackStack()
                        },
                        onBack = { 
                            productViewModel.clearProductSelection()
                            navController.popBackStack() 
                        }
                    )
                }
            }
            composable(Screen.Cart.route) {
                CartScreen(
                    items = appViewModel.cartItems,
                    total = appViewModel.getCartTotal(),
                    onQuantityChange = { item, delta -> appViewModel.updateCartItemQuantity(item, delta) },
                    onRemoveItem = { item -> appViewModel.removeFromCart(item) },
                    onCheckoutClick = {
                        if (appViewModel.loggedUser.value != null) {
                            navController.navigate(Screen.Payment.route)
                        } else {
                            navController.navigate(Screen.AuthGateway.route)
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Payment.route) {
                PaymentScreen(
                    total = appViewModel.getCartTotal(),
                    onConfirm = {
                        appViewModel.confirmOrder()
                        navController.navigate(Screen.Success.route) {
                            popUpTo(Screen.Categories.route) { inclusive = false }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Success.route) {
                SuccessScreen(
                    onFinish = {
                        navController.navigate(Screen.Categories.route) {
                            popUpTo(Screen.Categories.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.OrderHistory.route) {
                OrderHistoryScreen(
                    orders = emptyList(),
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
