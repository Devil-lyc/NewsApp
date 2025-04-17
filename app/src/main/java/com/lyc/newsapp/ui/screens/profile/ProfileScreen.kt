package com.lyc.newsapp.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lyc.newsapp.ui.utils.AsyncImageWithPlaceholder
import androidx.hilt.navigation.compose.hiltViewModel
import com.lyc.newsapp.ui.screens.auth.AuthState
import com.lyc.newsapp.ui.screens.auth.AuthViewModel

/**
 * 个人资料界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val authState by viewModel.authState.collectAsState()
    
    // 添加日志跟踪退出登录流程
    LaunchedEffect(Unit) {
        android.util.Log.d("ProfileScreen", "初始化: isLoggedIn=${authState.isLoggedIn}")
    }
    
    // 监听登录状态变化
    LaunchedEffect(authState.isLoggedIn) {
        android.util.Log.d("ProfileScreen", "登录状态变化: isLoggedIn=${authState.isLoggedIn}")
    }
    
    // 控制确认对话框的显示
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    // 退出登录确认对话框
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("退出登录") },
            text = { Text("确定要退出登录吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        android.util.Log.d("ProfileScreen", "用户确认退出登录")
                        onLogout()
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
    
    Scaffold(
//        topBar = {
//            CenterAlignedTopAppBar(
//                title = {
//                    Text(
//                        text = "个人中心",
//                        style = MaterialTheme.typography.headlineSmall.copy(
//                            fontWeight = FontWeight.Bold
//                        )
//                    )
//                },
//                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.background
//                ),
//                windowInsets = WindowInsets.statusBars
//            )
//        },
        contentWindowInsets = WindowInsets.statusBars
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // 用户资料卡片

            UserProfileCard(authState)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 设置列表
            SettingsList()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 应用信息
            AppInfoSection(
                onLogout = { showLogoutDialog = true }
            )

            Spacer(modifier = Modifier.height(20.dp))

        }
    }
}

/**
 * 用户资料卡片
 */
@Composable
fun UserProfileCard(
    authState: AuthState
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 用户头像
            AsyncImageWithPlaceholder(
                model = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?q=80&w=1780&auto=format&fit=crop",
                contentDescription = "用户头像",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 用户名
            Text(
                text = authState.user?.username ?:"未设置",
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 用户简介
            Text(
                text = authState.user?.email ?:"未设置",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 用户统计
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(title = "阅读量", value = "328")
                StatItem(title = "收藏", value = "46")
                StatItem(title = "关注", value = "12")
            }
        }
    }
}

/**
 * 统计项
 */
@Composable
fun StatItem(title: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 设置列表
 */
@Composable
fun SettingsList() {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "设置",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        SettingItem(
            icon = Icons.Outlined.Language,
            title = "语言设置",
            subtitle = "简体中文"
        )
        
        SettingItem(
            icon = Icons.Outlined.Notifications,
            title = "通知设置",
            subtitle = "接收重要通知"
        )
        
        SettingItem(
            icon = Icons.Outlined.DarkMode,
            title = "深色模式",
            subtitle = "跟随系统",
            hasSwitch = true
        )
        
        SettingItem(
            icon = Icons.Outlined.Settings,
            title = "阅读设置",
            subtitle = "字体大小、行间距等"
        )
        
        SettingItem(
            icon = Icons.Outlined.CloudDownload,
            title = "离线阅读",
            subtitle = "自动下载感兴趣的文章",
            hasSwitch = true
        )
        
        SettingItem(
            icon = Icons.Outlined.Favorite,
            title = "兴趣设置",
            subtitle = "个性化推荐"
        )
    }
}

/**
 * 设置项
 */
@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    hasSwitch: Boolean = false
) {
    var switchState by remember { mutableStateOf(false) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        if (hasSwitch) {
            Switch(
                checked = switchState,
                onCheckedChange = { switchState = it }
            )
        } else {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    
    Divider(
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
        thickness = 1.dp
    )
}

/**
 * 应用信息部分
 */
@Composable
fun AppInfoSection(
    onLogout: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "关于",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "新闻App",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Text(
                    text = "版本: 1.0.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Divider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
            thickness = 1.dp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 退出登录按钮
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = null
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(text = "退出登录")
        }
    }
} 