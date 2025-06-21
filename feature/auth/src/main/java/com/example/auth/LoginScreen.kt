package com.example.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AuthSubmitButton
import com.example.ui.components.EmailTextField
import com.example.ui.components.PasswordTextField
import com.example.ui.components.SocialLoginButtons

/**
 * 登录屏幕
 *
 * @param authState 认证状态
 * @param onLogin 登录回调
 * @param onNavigateToRegister 导航到注册界面回调
 * @param snackbarHostState Snackbar主机状态
 */
@Composable
fun LoginScreen(
    authState: AuthState,
    onLogin: (String, String) -> Unit,
    onNavigateToRegister: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    
    val context = LocalContext.current
    
    // 表单验证
    fun isFormValid(): Boolean {
        return validateEmail(email) == null && validatePassword(password) == null
    }
    
    // 显示错误消息
    LaunchedEffect(authState.error) {
        authState.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A2237))
    ) {
        // 主内容
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            
            // Logo
            Image(
                imageVector = Icons.Default.Build,
                contentDescription = "应用Logo",
                modifier = Modifier.size(70.dp),
                contentScale = ContentScale.Fit
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 应用名称
            Text(
                text = "新闻快报",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            // 应用描述
            Text(
                text = "掌握全球热点，洞察时事脉搏",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 登录/注册选项卡
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFF283047),
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // 登录/注册标签行
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        // 登录按钮和指示器
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "登录",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF5A8A),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            // 登录按钮下方的指示器
                            Box(
                                modifier = Modifier
                                    .height(2.dp)
                                    .width(40.dp)
                                    .background(Color(0xFFFF5A8A))
                            )
                        }
                        
                        // 注册按钮
                        Text(
                            text = "注册",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White,
                            modifier = Modifier.clickable { onNavigateToRegister() }
                        )
                    }
                    
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 错误提示
                    AnimatedVisibility(visible = authState.error != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Color(0x33FF3B30),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = "错误",
                                tint = Color(0xFFFF3B30)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            authState.error?.let {
                                Text(
                                    text = it,
                                    color = Color(0xFFFF3B30),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    // 邮箱输入框
                    Text(
                        text = "邮箱",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 2.dp, bottom = 4.dp)
                    )
                    
                    EmailTextField(
                        email = email,
                        onEmailChange = { 
                            email = it
                            emailError = validateEmail(it)
                        },
                        isError = emailError != null,
                        errorText = emailError
                    )
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    // 密码输入框
                    Text(
                        text = "密码",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 2.dp,bottom = 4.dp)
                    )
                    
                    PasswordTextField(
                        password = password,
                        onPasswordChange = { 
                            password = it
                            passwordError = validatePassword(it)
                        },
                        isError = passwordError != null,
                        errorText = passwordError,
                        imeAction = ImeAction.Done,
                        onAction = { 
                            if (isFormValid()) {
                                onLogin(email, password)
                            }
                        },
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // 登录按钮
                    AuthSubmitButton(
                        text = "登录",
                        onClick = { 
                            emailError = validateEmail(email)
                            passwordError = validatePassword(password)
                            
                            if (isFormValid()) {
                                onLogin(email, password)
                            }
                        },
                        enabled = !authState.isLoading
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // 快速登录分隔线
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFF33415E)
                        )
                        
                        Text(
                            text = "快速登录",
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        
                        Divider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFF33415E)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // 社交媒体登录
                    SocialLoginButtons(
                        onWechatLogin = { /* 微信登录 */ },
                        onQQLogin = { /* QQ登录 */ },
                        onGithubLogin = { /* Github登录 */ }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
        
        // 加载指示器
        if (authState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFFF5A8A))
            }
        }
    }
}

/**
 * 验证邮箱
 *
 * @param email 邮箱
 * @return 错误消息，如果有效则返回null
 */
fun validateEmail(email: String): String? {
    if (email.isEmpty()) {
        return "邮箱不能为空"
    }
    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        return "邮箱格式不正确"
    }
    return null
}

/**
 * 验证密码
 *
 * @param password 密码
 * @return 错误消息，如果有效则返回null
 */
fun validatePassword(password: String): String? {
    if (password.isEmpty()) {
        return "密码不能为空"
    }
    if (password.length < 6) {
        return "密码长度至少为6位"
    }
    return null
} 