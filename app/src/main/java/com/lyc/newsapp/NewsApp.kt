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
    val authState by authViewModel.authState.collectAsState()
    
    // 检查认证状态，如果未登录且当前页面不是Auth页面，则导航到Auth页面
    LaunchedEffect(authState.isLoggedIn) {
        if (!authState.isLoggedIn) {
            val currentDestination = navController.currentDestination?.route
            if (currentDestination != Screen.Auth.route) {
                navController.navigate(Screen.Auth.route) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }
        }
    }
    
    // 获取当前导航状态
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // 主应用脚手架
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
        contentWindowInsets = WindowInsets(0,0,0,0)
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
                        navController.navigate("${Screen.NewsDetail.route}?newsId=${newsId}&isBookmarked=false")
                    }
                )
            }
            
            // 搜索路由
            composable(Screen.Search.route) {
                SearchScreen(
                    onArticleClick = { newsId ->
                        navController.navigate("${Screen.NewsDetail.route}?newsId=${newsId}&isBookmarked=false")
                    }
                )
            }
            
            // 收藏路由
            composable(Screen.Bookmark.route) {
                BookmarkScreen(
                    onArticleClick = { newsId ->
                        // 从收藏页面导航到详情页时，传递isBookmarked=true参数
                        navController.navigate("${Screen.NewsDetail.route}?newsId=${newsId}&isBookmarked=true")
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Auth.route)
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
                "${Screen.NewsDetail.route}?newsId={newsId}&isBookmarked={isBookmarked}",
                arguments = listOf(
                    navArgument("newsId") { type = NavType.StringType },
                    navArgument("isBookmarked") { 
                        type = NavType.BoolType
                        defaultValue = false 
                    }
                )
            ) { backStackEntry ->
                val newsId = backStackEntry.arguments?.getString("newsId") ?: ""
                val isBookmarked = backStackEntry.arguments?.getBoolean("isBookmarked") ?: false
                
                NewsDetailScreen(
                    newsId = newsId,
                    onNavigateUp = { navController.navigateUp() },
                    initialBookmarkState = isBookmarked
                )
            }
        }
    }
} 