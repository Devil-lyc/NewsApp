package com.lyc.newsapp.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/**
 * 认证子导航路由
 */
sealed class AuthRoute(val route: String) {
    object Login : AuthRoute("login")
    object Register : AuthRoute("register")
}

/**
 * 认证主屏幕 - 管理登录和注册界面之间的导航
 *
 * @param mainNavController 主导航控制器，用于在登录成功后跳转到主界面
 * @param viewModel 认证视图模型
 */
@Composable
fun AuthScreen(
    mainNavController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val authNavController = rememberNavController()
    val context = LocalContext.current
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }
    
    // 处理登录和注册成功的逻辑
    LaunchedEffect(authState.isLoggedIn) {
        if (authState.isLoggedIn) {
            // 导航到主界面
            mainNavController.navigate("home") {
                popUpTo("auth") { inclusive = true }
            }
        }
    }
    
    // 登录成功后重置状态
    LaunchedEffect(authState.isLoginSuccessful) {
        if (authState.isLoginSuccessful) {
            viewModel.resetLoginSuccess()
        }
    }
    
    // 注册成功后重置状态
    LaunchedEffect(authState.isRegisterSuccessful) {
        if (authState.isRegisterSuccessful) {
            viewModel.resetRegisterSuccess()
        }
    }
    
    // 子导航视图
    NavHost(
        navController = authNavController,
        startDestination = AuthRoute.Login.route
    ) {
        // 登录界面
        composable(AuthRoute.Login.route) {
            LoginScreen(
                authState = authState,
                onLogin = { email, password ->
                    viewModel.login(email, password)
                },
                onNavigateToRegister = {
                    authNavController.navigate(AuthRoute.Register.route)
                },
                snackbarHostState = snackbarHostState
            )
        }
        
        // 注册界面
        composable(AuthRoute.Register.route) {
            RegisterScreen(
                authState = authState,
                onRegister = { username, email, password ->
                    viewModel.register(username, email, password)
                },
                onNavigateToLogin = {
                    authNavController.navigate(AuthRoute.Login.route) {
                        popUpTo(AuthRoute.Register.route) { inclusive = true }
                    }
                },
                snackbarHostState = snackbarHostState
            )
        }
    }
} 