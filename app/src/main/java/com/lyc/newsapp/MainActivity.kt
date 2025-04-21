package com.lyc.newsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.lyc.newsapp.ui.theme.NewsAppTheme
import com.lyc.newsapp.ui.theme.ThemeViewModel
import com.lyc.newsapp.util.performance.StartupTracer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // 注入ThemeViewModel
    private val themeViewModel: ThemeViewModel by viewModels()
    
    // 控制SplashScreen显示的标志
    private var isThemeReady = false
    private var isContentReady = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        StartupTracer.startStage(StartupTracer.Stages.MAIN_ACTIVITY_INIT)
        
        // 安装SplashScreen，必须在super.onCreate之前调用
        val splashScreen = installSplashScreen()
        // 只等待内容准备好，主题加载可以异步进行
        splashScreen.setKeepOnScreenCondition { !isContentReady }
        
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // 在super.onCreate之后，在主线程中预加载主题设置
        lifecycleScope.launch {
            Timber.d("开始预加载主题设置")
            themeViewModel.preloadThemeSettings()
            Timber.d("主题设置加载完成")
            isThemeReady = true
        }
        
        setContent {
            StartupTracer.markEvent("setContent_started")
            
            // 获取当前深色模式状态
            val isDarkMode by themeViewModel.isDarkMode.collectAsState()
            Timber.d("当前主题模式: ${if (isDarkMode) "深色" else "浅色"}")
            
            // 使用深色模式设置应用主题
            NewsAppTheme(
                darkTheme = isDarkMode
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    // 记录首次渲染完成事件
                    DisposableEffect(Unit) {
                        val duration = StartupTracer.endStage(StartupTracer.Stages.MAIN_ACTIVITY_INIT)
                        Timber.d("MainActivity初始化完成，耗时: ${duration}ms")
                        
                        StartupTracer.startStage(StartupTracer.Stages.FIRST_RENDER)
                        
                        // 将内容状态标记为准备完成
                        isContentReady = true
                        
                        onDispose {
                            // 不做任何操作
                        }
                    }
                    
                    NewsApp(
                        navController = navController,
                        onFirstContentRender = {
                            // 主界面首次渲染完成
                            val renderTime = StartupTracer.endStage(StartupTracer.Stages.FIRST_RENDER)
                            Timber.d("首次内容渲染完成，耗时: ${renderTime}ms")
                            
                            // 打印当前的阶段性启动报告
                            Timber.i(StartupTracer.generateReport())
                        }
                    )
                }
            }
        }
    }
    
    override fun onStop() {
        super.onStop()
        if (isFinishing) {
            // 应用关闭前打印完整启动报告
            StartupTracer.printReport()
        }
    }
}

