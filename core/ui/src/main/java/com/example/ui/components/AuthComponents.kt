package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/**
 * 邮箱输入框
 *
 * @param email 邮箱值
 * @param onEmailChange 邮箱变化回调
 * @param modifier 修饰符
 * @param isError 是否显示错误
 * @param errorText 错误文本
 */
@Composable
fun EmailTextField(
    email: String,
    onEmailChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorText: String? = null
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        placeholder = { Text("请输入您的邮箱地址", color = Color.White.copy(alpha = 0.7f)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        isError = isError,
        supportingText = if (isError && !errorText.isNullOrEmpty()) {
            { Text(errorText) }
        } else null,
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor =  Color(0xFFFF5A8A),
            unfocusedBorderColor = Color(0xFF33415E),
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            cursorColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            errorTextColor = Color.White
        ),
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * 密码输入框
 *
 * @param password 密码值
 * @param onPasswordChange 密码变化回调
 * @param modifier 修饰符
 * @param label 标签文本
 * @param isError 是否显示错误
 * @param errorText 错误文本
 * @param imeAction 输入法动作
 * @param onAction 动作回调
 */
@Composable
fun PasswordTextField(
    password: String,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "请输入您的密码",
    isError: Boolean = false,
    errorText: String? = null,
    imeAction: ImeAction = ImeAction.Done,
    onAction: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.7f)) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (passwordVisible) "隐藏密码" else "显示密码",
                        tint = Color(0xFFFFFFFF)
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                },
                onDone = {
                    focusManager.clearFocus()
                    onAction()
                }
            ),
            isError = isError,
            supportingText = if (isError && !errorText.isNullOrEmpty()) {
                { Text(errorText) }
            } else null,
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor =  Color(0xFFFF5A8A),
                unfocusedBorderColor = Color(0xFF33415E),
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                cursorColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                errorTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * 用户名输入框
 *
 * @param username 用户名值
 * @param onUsernameChange 用户名变化回调
 * @param modifier 修饰符
 * @param isError 是否显示错误
 * @param errorText 错误文本
 */
@Composable
fun UsernameTextField(
    username: String,
    onUsernameChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorText: String? = null
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = username,
        onValueChange = onUsernameChange,
        placeholder = { Text("请设置您的用户名", color = Color.White.copy(alpha = 0.7f)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        isError = isError,
        supportingText = if (isError && !errorText.isNullOrEmpty()) {
            { Text(errorText) }
        } else null,
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor =  Color(0xFFFF5A8A),
            unfocusedBorderColor = Color(0xFF33415E),
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            cursorColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            errorTextColor = Color.White
        ),
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * 认证提交按钮
 *
 * @param text 按钮文本
 * @param onClick 点击回调
 * @param modifier 修饰符
 * @param enabled 是否启用
 */
@Composable
fun AuthSubmitButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF5A8A),
            contentColor = Color.White,
            disabledContainerColor = Color(0xFFFF5A8A).copy(alpha = 0.5f)
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * 第三方登录按钮
 */
@Composable
fun SocialLoginButtons(
    onWechatLogin: () -> Unit,
    onQQLogin: () -> Unit,
    onGithubLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // 微信登录
        IconButton(
            onClick = onWechatLogin,
            modifier = Modifier
                .size(48.dp)
                .border(1.dp, Color(0xFF33415E), CircleShape)
        ) {
            Text("微", color = Color.White)
        }

        // QQ登录
        IconButton(
            onClick = onQQLogin,
            modifier = Modifier
                .size(48.dp)
                .border(1.dp, Color(0xFF33415E), CircleShape)
        ) {
            Text("Q", color = Color.White)
        }

        // Github登录
        IconButton(
            onClick = onGithubLogin,
            modifier = Modifier
                .size(48.dp)
                .border(1.dp, Color(0xFF33415E), CircleShape)
        ) {
            Text("G", color = Color.White)
        }
    }
}

/**
 * 切换认证模式按钮
 *
 * @param text 按钮文本
 * @param onClick 点击回调
 * @param modifier 修饰符
 */
@Composable
fun AuthToggleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(text, color = Color(0xFFFF5A8A))
    }
}