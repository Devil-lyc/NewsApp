package com.lyc.newsapp.ui.screens.auth

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
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lyc.newsapp.R
import com.lyc.newsapp.ui.components.AuthSubmitButton
import com.lyc.newsapp.ui.components.AuthToggleButton
import com.lyc.newsapp.ui.components.EmailTextField
import com.lyc.newsapp.ui.components.PasswordTextField
import com.lyc.newsapp.ui.components.UsernameTextField

/**
 * 注册屏幕
 *
 * @param authState 认证状态
 * @param onRegister 注册回调
 * @param onNavigateToLogin 导航到登录界面回调
 * @param snackbarHostState Snackbar主机状态
 */
@Composable
fun RegisterScreen(
    authState: AuthState,
    onRegister: (String, String, String) -> Unit,
    onNavigateToLogin: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var usernameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    
    // 显示错误消息
    LaunchedEffect(authState.error) {
        authState.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }
    
    // 验证重复密码
    fun validateConfirmPassword(confirmPassword: String): String? {
        if (confirmPassword.isEmpty()) {
            return "确认密码不能为空"
        }
        if (confirmPassword != password) {
            return "两次输入的密码不一致"
        }
        return null
    }
    
    // 检查表单是否有效
    fun isFormValid(): Boolean {
        return validateUsername(username) == null &&
                validateEmail(email) == null &&
                validatePassword(password) == null &&
                validateConfirmPassword(confirmPassword) == null
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
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
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
            
            // 登录/注册选项卡区域
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
                        // 登录按钮
                        Text(
                            text = "登录",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White,
                            modifier = Modifier.clickable { onNavigateToLogin() }
                        )

                        // 注册按钮和指示器
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "注册",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF5A8A),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            // 注册按钮下方的指示器
                            Box(
                                modifier = Modifier
                                    .height(2.dp)
                                    .width(40.dp)
                                    .background(Color(0xFFFF5A8A))
                            )
                        }
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
                        
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                    
                    // 用户名输入框
                    Text(
                        text = "用户名",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 2.dp, bottom = 4.dp)
                    )
                    
                    UsernameTextField(
                        username = username,
                        onUsernameChange = { 
                            username = it
                            usernameError = validateUsername(it)
                        },
                        isError = usernameError != null,
                        errorText = usernameError
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
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
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // 密码输入框
                    Text(
                        text = "密码",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 2.dp, bottom = 4.dp)
                    )
                    
                    PasswordTextField(
                        password = password,
                        onPasswordChange = { 
                            password = it
                            passwordError = validatePassword(it)
                            // 当密码改变时，重新验证确认密码
                            if (confirmPassword.isNotEmpty()) {
                                confirmPasswordError = validateConfirmPassword(confirmPassword)
                            }
                        },
                        isError = passwordError != null,
                        errorText = passwordError,
                        imeAction = ImeAction.Next
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // 确认密码输入框
                    Text(
                        text = "确认密码",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 2.dp, bottom = 4.dp)
                    )
                    
                    PasswordTextField(
                        password = confirmPassword,
                        onPasswordChange = { 
                            confirmPassword = it
                            confirmPasswordError = validateConfirmPassword(it)
                        },
                        placeholder = "请再次输入您的密码",
                        isError = confirmPasswordError != null,
                        errorText = confirmPasswordError,
                        imeAction = ImeAction.Done,
                        onAction = { 
                            if (isFormValid()) {
                                onRegister(username, email, password)
                            }
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // 注册按钮
                    AuthSubmitButton(
                        text = "注册",
                        onClick = { 
                            usernameError = validateUsername(username)
                            emailError = validateEmail(email)
                            passwordError = validatePassword(password)
                            confirmPasswordError = validateConfirmPassword(confirmPassword)
                            
                            if (isFormValid()) {
                                onRegister(username, email, password)
                            }
                        },
                        enabled = !authState.isLoading
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // 登录链接
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        AuthToggleButton(
                            text = "已有账号？点击登录",
                            onClick = onNavigateToLogin
                        )
                    }
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
 * 验证用户名
 *
 * @param username 用户名
 * @return 错误消息，如果有效则返回null
 */
private fun validateUsername(username: String): String? {
    if (username.isEmpty()) {
        return "用户名不能为空"
    }
    if (username.length < 3) {
        return "用户名长度至少为3位"
    }
    if (username.length > 20) {
        return "用户名长度不能超过20位"
    }
    return null
} 