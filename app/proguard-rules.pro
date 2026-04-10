# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Add additional ProGuard rules to protect sensitive information

# Keep the ApiKeyConfig class but obfuscate its methods and fields
-keep class com.lyc.newsapp.util.ApiKeyConfig { *; }
-keepclassmembers class com.lyc.newsapp.util.ApiKeyConfig {
    private <fields>; 
    private <methods>; 
}

# Obfuscate string constants that might contain API keys
-adaptclassstrings
-adaptresourcefilecontents **.properties

# Encrypt API-related class names
-obfuscationdictionary api-dictionary.txt

# 保留泛型签名与注解，避免 Retrofit 反射时 ClassCastException(Class -> ParameterizedType)
-keepattributes Signature,InnerClasses,EnclosingMethod,*Annotation*

# Retrofit API 接口依赖方法签名与注解，需保留
-keep interface com.lyc.newsapp.data.remote.** { *; }

# 保留 Kotlin 元数据，避免部分反射/泛型场景信息丢失
-keep class kotlin.Metadata { *; }

# Retrofit + R8（特别是 benchmark/release 变体）下，保留 HTTP 注解方法签名与接口信息
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Retrofit/OkHttp 运行时注解读取需要这些属性
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,AnnotationDefault

# [关键修复] suspend 函数返回类型由 Continuation<T> 携带；R8 full mode 会擦除其泛型签名
# 导致 Retrofit 反射时出现 ClassCastException: Class cannot be cast to ParameterizedType
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Retrofit suspend 函数的实际返回类型与 Call/Response 包装类需保留泛型签名
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# Gson 反序列化：保留网络响应数据类及其字段（含 List<T> 等泛型字段）
-keep class com.lyc.newsapp.data.remote.NewsResponse { *; }
-keep class com.lyc.newsapp.data.remote.NewsDto { *; }
-keep class com.lyc.newsapp.data.remote.SentimentStats { *; }

# Baseline Profile：ProfileInstallReceiver 必须在 dex 中与清单一致且可接收 SAVE_PROFILE 广播（benchmarkRelease 会跑 R8）
-keep class androidx.profileinstaller.** { *; }