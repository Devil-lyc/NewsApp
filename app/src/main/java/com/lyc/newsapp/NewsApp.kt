package com.lyc.newsapp

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.lyc.newsapp.ui.screens.news.NewsDetailScreen
import com.lyc.newsapp.ui.screens.bookmark.BookmarkScreen
import com.lyc.newsapp.ui.screens.home.HomeScreen
import com.lyc.newsapp.ui.screens.profile.ProfileScreen
import com.lyc.newsapp.ui.screens.search.SearchScreen

/**
 * 导航目标
 */
sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
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
fun NewsApp(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    Scaffold(
        bottomBar = {
            // 仅在主导航页面显示底部导航栏
            val shouldShowBottomBar = currentDestination?.hierarchy?.any { 
                it.route in bottomNavItems.map { screen -> screen.route }
            } == true
            
            if (shouldShowBottomBar) {
                NavigationBar {
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
                                    // 防止在底部导航栏中创建多个后退栈
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // 避免重复创建相同目标的多个副本
                                    launchSingleTop = true
                                    // 切换标签时恢复状态
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onArticleClick = { news ->
                        navController.navigate("${Screen.NewsDetail.route}?newsId=${news.id}")
                    }
                )
            }
            
            composable(Screen.Search.route) {
                SearchScreen(
                    onArticleClick = { news ->
                        navController.navigate("${Screen.NewsDetail.route}?newsId=${news.id}")
                    }
                )
            }
            
            composable(Screen.Bookmark.route) {
                BookmarkScreen(
                    onArticleClick = { news ->
                        navController.navigate("${Screen.NewsDetail.route}?newsId=${news.id}")
                    }
                )
            }
            
            composable(Screen.Profile.route) {
                ProfileScreen()
            }
            
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