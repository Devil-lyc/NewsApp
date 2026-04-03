# NewsApp 项目文档

本文档描述 **NewsApp**（包名 `com.lyc.newsapp`）的产品定位、**MVI 界面架构**、模块划分、数据与网络、构建配置及**后续扩展方向**，供开发与维护使用。

---

## 1. 文档说明

| 项目 | 说明 |
|------|------|
| 读者 | 新成员上手、功能迭代、Code Review |
| 与旧版差异 | 界面层以 **MVI** 为约定：单向数据流、`Intent` 驱动、`UiState` 只读 |
| 术语 | **Intent**：用户或系统意图；**UiState**：界面快照；**Side Effect**：网络/存储/导航等副作用（建议在 ViewModel 内执行，必要时用 `SharedFlow<Effect>` 通知 UI 一次性事件） |

---

## 2. 项目概述

### 2.1 产品定位

NewsApp 是一款 **Android 新闻阅读客户端**：

- 从 **newsdata.io** 拉取新闻：分类浏览、分页、关键词搜索。
- 通过自建 **Node.js 后端** 完成注册/登录、会话持久化、**云端收藏**（增删查、按 `newsId` 判断是否已收藏）。

UI：**Jetpack Compose + Material 3**；路由：**Navigation Compose**；DI：**Hilt**；异步：**Kotlin 协程 + Flow**。

### 2.2 技术栈摘要

| 类别 | 选型 |
|------|------|
| 语言 / JVM | Kotlin，目标 JVM 11 |
| UI | Compose、Material 3 |
| 导航 | Navigation Compose |
| DI | Dagger Hilt + KSP |
| 网络 | Retrofit 2 + OkHttp + Gson |
| 异步 | kotlinx-coroutines、StateFlow |
| 本地存储 | MMKV（会话）、DataStore（主题，见 `ThemePreference`） |
| 图片 | Coil（Compose） |
| Web | Accompanist WebView |
| 日志 | Timber |

### 2.3 版本与构建（摘自 Gradle）

- **Application ID**：`com.lyc.newsapp`
- **minSdk**：25 / **targetSdk**：34 / **compileSdk**：35  
- **versionName**：1.0 / **versionCode**：1  
- 根工程单模块：`include(":app")`

---

## 3. 架构总览（MVI + 分层）

### 3.1 MVI 原则

1. **单向数据流**：UI 调用 `dispatch(Intent)`，ViewModel 更新 `UiState`，UI 仅订阅 `uiState`。  
2. **单一状态源**：避免在 Composable 内重复维护与 `UiState` 等价的业务状态。  
3. **Intent 表意**：所有用户操作与显式系统事件（如「屏幕重新可见刷新」）尽量建模为 `sealed class` 子类，便于扩展与单测。  
4. **副作用边界**：Repository / 系统服务调用放在 ViewModel 协程中；**一次性事件**（如「导航到登录」）优先用 `SharedFlow<Effect>` 或在导航层响应 `UiState` 中的显式标志位（用完即清），避免与持久状态混淆。

### 3.2 UI 契约：`MviHost`

路径：`ui/mvi/MviHost.kt`

```text
interface MviHost<UiState : Any, Intent : Any> {
    val uiState: StateFlow<UiState>
    fun dispatch(intent: Intent)
}
```

各功能 **ViewModel** 实现 `MviHost`，Composables 只依赖 `uiState` 与 `dispatch`。

### 3.3 源码包结构大纲（重构后）

根包：`com.lyc.newsapp`（路径：`app/src/main/java/com/lyc/newsapp/`）。Gradle 仍为单模块 `:app`。

```
com.lyc.newsapp
├── MainActivity.kt
├── NewsApp.kt                  # 底部导航 + NavHost
├── NewsApplication.kt
│
├── core/                       # 与业务无关的横切能力
│   ├── config/                 # ApiKeyConfig（assets 密钥读取）
│   ├── result/                 # Resource（Loading / Success / Error）
│   └── util/                   # formatDate 等纯工具
│
├── di/                         # Hilt 装配（唯一入口）
│   ├── AppModule.kt            # ThemePreference、ApiKeyConfig（如需要显式 @Provides）
│   ├── DataModule.kt           # @NewsClient、NewsApi、newsdata.io
│   ├── NetworkModule.kt        # Auth / Favorite Retrofit（业务后端）
│   └── RepositoryModule.kt     # NewsRepository、FavoriteRepository 的 @Binds
│
├── domain/
│   ├── model/                  # News、Category、Favorite…
│   └── repository/             # 仓库接口：NewsRepository、FavoriteRepository
│
├── data/
│   ├── mapper/                 # DTO → Domain（如 NewsMapper.kt / toNews）
│   ├── remote/                 # Retrofit Api、拦截器、响应 DTO
│   ├── local/                  # SessionManager（MMKV）
│   ├── preferences/            # ThemePreference（DataStore）
│   ├── model/                  # 网络请求/响应专用数据类
│   └── repository/             # *RepositoryImpl、AuthRepository 等实现
│
├── ui/
│   ├── mvi/                    # MviHost
│   ├── theme/
│   ├── components/             # 复用组件（含 AsyncImageWithPlaceholder 等）
│   └── feature/                # 按功能竖切
│       ├── home/
│       ├── search/
│       ├── news/
│       ├── bookmark/
│       ├── auth/
│       └── profile/
│           # 典型文件：*Screen.kt、*ViewModel.kt、*UiState.kt、*Intent.kt
│
└── util/
    └── performance/            # StartupTracer 等启动与性能埋点
```

- **新闻**：`domain.repository.NewsRepository` + `NewsRepositoryImpl` + newsdata.io（`DataModule` / `NewsApi`）。  
- **账号与收藏**：`AuthRepository`、`FavoriteRepositoryImpl` + 业务后端（`NetworkModule` 基址）。  
- **绑定**：`NewsRepository` 与 `FavoriteRepository` 均在 `di.RepositoryModule` 中 `@Binds`，勿重复绑定同一接口。

### 3.4 按模块的 MVI 约定

以下 **Intent / State** 为推荐形态；实现时以仓库中 `*Intent.kt`、`*ViewModel.kt` 为准。

| 模块 | UiState 要点 | Intent（示例） | 主要依赖 |
|------|----------------|------------------|----------|
| **Home** | 多分类列表、加载、错误、分页游标 | `Refresh(category)`、`LoadNextPage(page, category)` | `NewsRepository` |
| **Search** | 查询、结果、错误 | `SubmitSearch(query)`、`ClearError` | `NewsRepository` |
| **NewsDetail** | 详情、收藏态、错误 | `LoadNewsDetail`、`ToggleBookmark`、`SetInitialBookmarkState`、`CheckBookmarkStatus` | `NewsRepository`、`FavoriteRepository` |
| **Bookmark** | 收藏列表、加载、错误 | `Refresh`、`ScreenBecameVisible`、`DeleteFavorite(id)`、`ClearAll` | `FavoriteRepository` |
| **Auth** | 登录态、loading、错误、成功标记 | `Login`、`Register`、`Logout`、`SyncSession`、`ClearError`、重置成功标记 | `AuthRepository`、`SessionManager` |
| **Theme** | `ThemeUiState(mode)` | `SetMode`、`SetDarkEnabled` | `ThemePreference` |

### 3.5 MVI 实现约定（落地状态）

- 下列 **ViewModel** 均实现 `MviHost<UiState, Intent>`，UI 侧通过 `uiState` + `dispatch(Intent)` 交互，**不再**暴露零散的 `onEvent` / 业务方法作为公共入口：`HomeViewModel`、`SearchViewModel`、`NewsDetailViewModel`、`BookmarkViewModel`、`AuthViewModel`、`ThemeViewModel`。  
- **首页** 的初始拉取仍在 `HomeViewModel.init` 中触发（冷启动性能），用户操作一律走 `HomeIntent`。  
- **认证**：`AuthViewModel.authState` 与 `uiState` 指向同一 `StateFlow<AuthState>`，便于现有 Composable 少改包名。  
- **一次性副作用**（如导航、Snackbar）：仍可按 `MviHost.kt` 注释使用 `SharedFlow<Effect>` 扩展；当前以 `UiState` 中的成功/错误标记 + 导航层 `LaunchedEffect` 为主。

---

## 4. 双后端与导航

### 4.1 新闻 API（newsdata.io）

- 基址：`NewsApi.BASE_URL` → `https://newsdata.io/api/1/`  
- `ApiKeyInterceptor` 追加 `apikey`；密钥：`assets/api-keys.properties` 中 `NEWS_API_KEY`（`core.config.ApiKeyConfig`）。  
- 独立 OkHttp：`DataModule` 中 `@NewsClient`。

### 4.2 业务后端（用户 + 收藏）

- 基址：`NetworkModule` → `https://kkzynytfzajt.sealoshzh.site/`  
- 登录/注册：`@AuthClient`（无 Token）。  
- 收藏：`@FavoriteClient` + `AuthInterceptor`（`Authorization: Bearer <token>`）。

### 4.3 导航

- `NewsApp`：`Scaffold` + 底部栏（首页、搜索、收藏、我的）；`NavHost` 含 `auth`、`home`、`search`、`bookmark`、`profile`、`newsDetail?newsId=&isBookmarked=`。  
- **起始目的地** 当前多为 `home`；若产品要求未登录先进登录页，在 `NavHost` 调整 `startDestination` 并通测与 `Auth` 的 `dispatch`/`UiState` 联动。  
- 登出：通常 `dispatch(AuthIntent.Logout)` 后清空返回栈到 `auth`（以 `NewsApp` 实现为准）。

---

## 5. 核心功能说明（与 MVI 的对应关系）

- **首页**：`HomeViewModel` 分批加载、分页；UI 通过 `dispatch(HomeIntent.*)` 触发刷新与加载更多。  
- **搜索**：`SearchViewModel` 处理 `SubmitSearch` 等。  
- **详情**：加载详情与收藏切换均走 `NewsDetailIntent`。  
- **认证**：`AuthViewModel` 统一 `dispatch`；登录成功后 `UiState` 更新，导航层可 `LaunchedEffect` 响应。  
- **收藏**：未登录时 `LoginHint`；已登录列表刷新用 `BookmarkIntent.Refresh` / `ScreenBecameVisible`。  
- **个人中心 / 主题**：展示用户信息与主题切换；主题变更走 `ThemeIntent`，持久化在 DataStore。

性能相关实现与 `StartupTracer`、分批请求等见 **`Tool/PERFORMANCE_OPTIMIZATION.md`**。

---

## 6. 数据与网络细节

- **新闻 DTO**：`NewsDto`、`NewsResponse`；领域模型 `News`，映射见 `data/mapper/NewsMapper.kt`（`toNews`，含 `nextPage`）。
- **Repository**：多返回 `Flow<Resource<…>>`（Loading / Success / Error）。  
- **会话**：`SessionManager`（MMKV）；`AuthInterceptor` 注入 Token。  
- **安全**：Release 建议关闭 OkHttp **BODY** 级日志，禁止日志打印 Token。  
- **Room**：Gradle 已依赖但业务可尚未使用；离线缓存可作为扩展项（见第 9 节）。

后端 REST 细节见 **`Tool/API文档.md`**。

---

## 7. 权限、性能与可观测性

- **权限**：`INTERNET`、`ACCESS_NETWORK_STATE`（`AndroidManifest.xml`）。  
- **StartupTracer**：冷启动与各阶段耗时；`NewsApplication.enablePerformanceTracking` 控制详细日志。

---

## 8. 构建与运行

1. 配置 `app/src/main/assets/api-keys.properties`（可参考 `api-keys.properties.template`）。  
2. 确认业务后端可用；换域修改 `NetworkModule.BASE_URL`。  
3. 同步 Gradle，运行 `:app`。

环境需与 **AGP 8.7.3、Kotlin 2.0** 等版本兼容。

---

## 9. 测试建议（MVI）

- **ViewModel 单测**：给定 `dispatch` 序列，断言 `uiState` 的演进（可用 `TestScope` + 假 `Repository`）。  
- **Intent 契约**：新增功能优先补 `Intent` 子类与文档表，避免在 Composable 中直接调 Repository。

---

## 10. 后续可扩展功能（有深度的演进方向）

### 10.1 搜索：防抖、建议词与本地历史

- 增加 `QueryChanged(text)`、`SuggestionSelected(...)`；ViewModel 内对输入 `debounce` 再请求或查本地。  
- **深度**：Room 存搜索历史与点击权重，做个性化排序；单测用 `StandardTestDispatcher` 控制时间。

### 10.2 详情：阅读模式与内容策略

- `NewsDetailIntent` 扩展 `ToggleReaderMode`；对 HTML/正文做净化（白名单、去脚本）。  
- **深度**：抽象 `ContentPolicy`（地区/合规），同一 ViewModel 通过策略接口切换行为，无需复制页面。

### 10.3 收藏：弱网乐观更新与冲突

- `BookmarkIntent` 与本地 `pendingOps` 队列（Room）结合，失败重试或回滚。  
- **深度**：乐观 UI 更新 `UiState`，错误通过 `BookmarkEffect.ShowRetry` 提示；与服务端 `updatedAt` 约定 LWW 或合并规则。

### 10.4 首页：个性化 Feed

- 增加 `DismissArticle`、`BoostSource` 等 Intent；Repository 组合远程列表与本地重排/过滤。  
- **深度**：埋点经独立 `AnalyticsEffect` 或副作用层，不污染持久 `UiState`；远程开关控制策略，Intent 保持稳定。

### 10.5 账号：Token 刷新与统一 401 处理

- `AuthIntent.RefreshToken`；拦截器与 `SessionManager` 协作；失效时发 `AuthEffect.RequireLogin`。  
- **深度**：文档化状态机：未登录 / 已登录 / 刷新中 / 失效，避免分散的 `if (token)` 判断。

### 10.6 可观测性与调试

- Debug 下统一包装 `dispatch` 打日志（Intent 名 + state 摘要）。  
- **深度**：与 `StartupTracer` 指标分离——性能归性能，业务状态归 MVI，便于定位是「慢」还是「错」。

---

## 11. 目录速查

| 路径 | 说明 |
|------|------|
| `ui/mvi/MviHost.kt` | MVI 公共契约 |
| `ui/feature/*` | 各功能屏（home / search / news / bookmark / auth / profile） |
| `NewsApp.kt` | 底部导航 + NavHost |
| `MainActivity.kt` | Splash、主题、根 Compose |
| `di/DataModule.kt` | News Retrofit、ApiKey、`@NewsClient` |
| `di/NetworkModule.kt` | Auth / Favorite Retrofit |
| `di/RepositoryModule.kt` | `NewsRepository`、`FavoriteRepository` 绑定 |
| `data/local/SessionManager.kt` | MMKV 会话 |
| `assets/api-keys.properties` | newsdata.io Key |
| `Tool/API文档.md` | 后端接口 |
| `Tool/PERFORMANCE_OPTIMIZATION.md` | 性能笔记 |

---

## 12. 维护注意点

1. **Hilt 绑定**：`RepositoryModule` 中已为新闻与收藏分别 `@Binds`，同一接口勿重复绑定。  
2. **起始路由与登录策略**：变更 `startDestination` 时需通测 `Auth` 与主导航栈。  
3. **日志与密钥**：生产环境收紧日志，禁止输出 Token。  
4. **Room**：不用可删依赖；若做离线新闻，再引入 Entity/DAO 与迁移。  

---

*接口地址、依赖版本或 MVI Intent 表变更时，请同步更新本文档第 3、4、6、10 节相关表格与说明。*
