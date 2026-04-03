package com.lyc.newsapp

import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.lyc.newsapp.ui.feature.auth.AuthIntent
import com.lyc.newsapp.ui.feature.auth.AuthScreen
import com.lyc.newsapp.ui.feature.auth.AuthViewModel
import com.lyc.newsapp.ui.feature.bookmark.BookmarkScreen
import com.lyc.newsapp.ui.feature.home.HomeScreen
import com.lyc.newsapp.ui.feature.news.NewsDetailScreen
import com.lyc.newsapp.ui.feature.profile.ProfileScreen
import com.lyc.newsapp.ui.feature.search.SearchScreen
import com.lyc.newsapp.util.performance.StartupTracer

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
 * @param navController 导航控制器
 * @param onFirstContentRender 首次内容渲染完成回调
 * @param authViewModel 身份验证视图模型
 */
@Composable
fun NewsApp(
    navController: NavHostController,
    onFirstContentRender: () -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    
    // 标记是否已经调用过渲染完成回调
    val hasCalledRenderCallback = remember { mutableListOf(false) }
    
    // 避免应用启动时的导航跳转，仅在用户交互导致的登录状态变化时执行导航
    val isFirstNavigation = remember { mutableStateOf(true) }
    
    LaunchedEffect(authState.isLoggedIn) {
        // 首次加载时不执行导航，使用startDestination处理初始路由
        if (isFirstNavigation.value) {
            isFirstNavigation.value = false
            return@LaunchedEffect
        }
        
        // 后续的登录状态变化再执行导航
        if (!authState.isLoggedIn) {
            // 只有当前不在Auth页面时才导航
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
            startDestination = Screen.Home.route,
//            startDestination = if (authState.isLoggedIn) Screen.Home.route else Screen.Auth.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // 认证路由
            composable(Screen.Auth.route) {
                StartupTracer.markEvent("auth_screen_compose")
                AuthScreen(mainNavController = navController)
                
                // 调用首次渲染完成回调
                if (!hasCalledRenderCallback[0]) {
                    onFirstContentRender()
                    hasCalledRenderCallback[0] = true
                }
            }
            
            // 主页路由
            composable(Screen.Home.route) {
                StartupTracer.startStage(StartupTracer.Stages.HOME_SCREEN_INIT)
                
                HomeScreen(
                    onArticleClick = { newsId ->
                        navController.navigate("${Screen.NewsDetail.route}?newsId=${newsId}&isBookmarked=false")
                    }
                )
                
                // 记录首页加载完成
                LaunchedEffect(Unit) {
                    StartupTracer.endStage(StartupTracer.Stages.HOME_SCREEN_INIT)
                    
                    // 调用首次渲染完成回调
                    if (!hasCalledRenderCallback[0]) {
                        onFirstContentRender()
                        hasCalledRenderCallback[0] = true
                    }
                }
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
                        authViewModel.dispatch(AuthIntent.Logout)
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