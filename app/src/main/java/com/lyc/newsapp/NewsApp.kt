package com.lyc.newsapp

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.lyc.newsapp.ui.screens.auth.AuthScreen
import com.lyc.newsapp.ui.screens.news.NewsDetailScreen
import com.lyc.newsapp.ui.screens.bookmark.BookmarkScreen
import com.lyc.newsapp.ui.screens.home.HomeScreen
import com.lyc.newsapp.ui.screens.profile.ProfileScreen
import com.lyc.newsapp.ui.screens.search.SearchScreen
import com.lyc.newsapp.ui.screens.auth.AuthViewModel

/**
 * 导航目标
 */
sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Auth : Screen(
        route = "auth",
        title = "认证",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
    
    object Home : Screen(
        route = "home",
        title = "首页",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )
    
    object Search : Screen(
        route = "search",
        title = "搜索",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search
    )
    
    object Bookmark : Screen(
        route = "bookmark",
        title = "收藏",
        selectedIcon = Icons.Filled.Bookmark,
        unselectedIcon = Icons.Outlined.Bookmark
    )
    
    object Profile : Screen(
        route = "profile",
        title = "我的",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
    
    object NewsDetail : Screen(
        route = "newsDetail",
        title = "文章详情",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )
}

/**
 * 底部导航栏项目
 */
val bottomNavItems = listOf(
    Screen.Home,
    Screen.Search,
    Screen.Bookmark,
    Screen.Profile
)

/**
 * 主应用入口
 */
@Composable
fun NewsApp(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val authState by authViewModel.authState.collectAsState()
    
    // 检查用户是否已登录，如果已登录则跳过认证界面
    LaunchedEffect(Unit) {
        authViewModel.checkLoginStatus()
    }
    
    // 额外添加一个单独的导航处理，专门用于处理登出情况
    LaunchedEffect(authState.isLoggedIn) {
        android.util.Log.d("NewsApp", "登录状态变化: ${authState.isLoggedIn}")
        if (!authState.isLoggedIn) {
            // 如果状态变为未登录，直接导航到登录页面
            navController.navigate(Screen.Auth.route) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }
    
    // 根据登录状态决定起始目的地
    LaunchedEffect(authState.isLoggedIn) {
        if (authState.isLoggedIn) {
            // 如果当前在认证页面，则导航到首页
            if (currentDestination?.route == Screen.Auth.route) {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Auth.route) { inclusive = true }
                }
            }
        } else {
            // 如果未登录，则导航到认证页面
            navController.navigate(Screen.Auth.route) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }
    
    Scaffold(
        bottomBar = {
            // 仅在主导航页面显示底部导航栏
            val shouldShowBottomBar = currentDestination?.hierarchy?.any { 
                it.route in bottomNavItems.map { screen -> screen.route }
            } == true
            
            if (shouldShowBottomBar) {
                NavigationBar(
                    tonalElevation = 0.dp,
                    windowInsets = WindowInsets(0,0,0,0)
                ) {
                    bottomNavItems.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { 
                            it.route == screen.route 
                        } == true
                        
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.title
                                )
                            },
                            label = { Text(screen.title) },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets.navigationBars
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = if (authState.isLoggedIn) Screen.Home.route else Screen.Auth.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // 认证路由
            composable(Screen.Auth.route) {
                AuthScreen(mainNavController = navController)
            }
            
            // 主页路由
            composable(Screen.Home.route) {
                HomeScreen(
                    onArticleClick = { newsId ->
                        navController.navigate("${Screen.NewsDetail.route}?newsId=${newsId}")
                    }
                )
            }
            
            // 搜索路由
            composable(Screen.Search.route) {
                SearchScreen(
                    onArticleClick = { newsId ->
                        navController.navigate("${Screen.NewsDetail.route}?newsId=${newsId}")
                    }
                )
            }
            
            // 收藏路由
            composable(Screen.Bookmark.route) {
                BookmarkScreen(
                    onArticleClick = { newsId ->
                        navController.navigate("${Screen.NewsDetail.route}?newsId=${newsId}")
                    }
                )
            }
            
            // 个人资料路由
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onLogout = {
                        // 先执行登出逻辑
                        authViewModel.logout()
                        // 然后立即强制导航到登录界面
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }
            
            // 新闻详情路由
            composable(
                "${Screen.NewsDetail.route}?newsId={newsId}",
                arguments = listOf(navArgument("newsId") { type = NavType.StringType })
            ) { backStackEntry ->
                val newsId = backStackEntry.arguments?.getString("newsId") ?: ""
                NewsDetailScreen(
                    newsId = newsId,
                    onNavigateUp = { navController.navigateUp() }
                )
            }
        }
    }
} 