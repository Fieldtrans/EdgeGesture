# EdgeGesture 开发快速开始指南

## 项目结构

```
EdgeGesture/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/myedgegesture/
│   │   │   │   ├── compat/              # 版本兼容性工具
│   │   │   │   │   └── VersionCompat.kt
│   │   │   │   ├── data/                # 数据层
│   │   │   │   │   ├── model/
│   │   │   │   │   │   └── SettingsState.kt
│   │   │   │   │   └── repository/
│   │   │   │   │       └── ConfigRepository.kt
│   │   │   │   ├── ui/                  # UI 层
│   │   │   │   │   └── viewmodel/
│   │   │   │   │       └── SettingsViewModel.kt
│   │   │   │   ├── MainActivity.kt      # 主界面（待重构）
│   │   │   │   ├── MainHook.kt          # Xposed Hook 入口
│   │   │   │   ├── EdgeGestureDetector.kt
│   │   │   │   ├── GestureActionDispatcher.kt
│   │   │   │   └── ...
│   │   │   └── res/                     # 资源文件
│   │   └── test/                        # 单元测试
│   │       └── java/com/example/myedgegesture/
│   │           └── ui/viewmodel/
│   │               └── SettingsViewModelTest.kt
│   ├── config/
│   │   └── detekt.yml                   # Detekt 配置
│   └── build.gradle.kts                 # 应用构建配置
├── gradle/
│   └── libs.versions.toml               # 依赖版本管理
├── gradle.properties                    # Gradle 配置
├── IMPROVEMENTS.md                      # 改进总结
└── README.md                            # 项目说明
```

## 环境要求

- **JDK**: 11 或更高
- **Android Studio**: Hedgehog (2023.1.1) 或更高
- **Gradle**: 8.0+
- **Android SDK**: API 26-35

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/Fieldtrans/EdgeGesture.git
cd EdgeGesture
```

### 2. 打开项目

使用 Android Studio 打开项目，等待 Gradle 同步完成。

### 3. 构建项目

```bash
# Debug 版本
./gradlew assembleDebug

# Release 版本
./gradlew assembleRelease
```

### 4. 运行测试

```bash
# 运行所有单元测试
./gradlew test

# 运行代码质量检查
./gradlew detekt ktlintCheck
```

## 开发工作流

### 添加新功能

1. **创建分支**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **编写代码**
   - 遵循 MVVM 架构
   - 在 ViewModel 中处理业务逻辑
   - 在 Repository 中处理数据访问

3. **编写测试**
   ```kotlin
   @Test
   fun `your test description`() = runTest {
       // Given
       // When
       // Then
   }
   ```

4. **运行检查**
   ```bash
   ./gradlew check
   ```

5. **提交代码**
   ```bash
   git add .
   git commit -m "feat: add your feature"
   git push origin feature/your-feature-name
   ```

### 代码风格

项目使用 ktlint 进行代码格式化：

```bash
# 检查代码风格
./gradlew ktlintCheck

# 自动修复格式问题
./gradlew ktlintFormat
```

### 代码质量检查

使用 Detekt 进行静态代码分析：

```bash
# 运行 Detekt
./gradlew detekt

# 查看报告
open app/build/reports/detekt/detekt.html
```

## 架构指南

### MVVM 架构

```
View (Composable) → ViewModel → Repository → Data Source
```

#### View (UI 层)
- 使用 Jetpack Compose
- 只负责显示和用户交互
- 通过 ViewModel 获取数据

```kotlin
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val settings by viewModel.settingsState.collectAsState()
    
    // UI 代码
}
```

#### ViewModel (业务逻辑层)
- 管理 UI 状态
- 处理业务逻辑
- 使用 Flow 进行响应式编程

```kotlin
class SettingsViewModel(
    private val repository: ConfigRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(SettingsState.default())
    val state: StateFlow<SettingsState> = _state.asStateFlow()
    
    fun updateSettings(settings: SettingsState) {
        viewModelScope.launch {
            repository.saveSettings(settings)
            _state.value = settings
        }
    }
}
```

#### Repository (数据访问层)
- 封装数据源访问
- 提供统一的数据接口
- 处理数据转换

```kotlin
class ConfigRepository(private val context: Context) {
    suspend fun loadSettings(): SettingsState {
        // 从 SharedPreferences 加载
    }
    
    suspend fun saveSettings(settings: SettingsState) {
        // 保存到 SharedPreferences
    }
}
```

## 测试指南

### 单元测试

使用 JUnit 4 + MockK：

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class YourViewModelTest {
    
    private lateinit var viewModel: YourViewModel
    private lateinit var repository: YourRepository
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        viewModel = YourViewModel(repository)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `test description`() = runTest {
        // Given
        val expected = "expected value"
        
        // When
        viewModel.doSomething()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertEquals(expected, viewModel.state.value)
    }
}
```

### 运行测试

```bash
# 所有测试
./gradlew test

# 特定测试类
./gradlew test --tests YourViewModelTest

# 带覆盖率报告
./gradlew testDebugUnitTest jacocoTestReport
```

## 常见任务

### 添加新的配置项

1. **在 GestureConfig 中添加常量**
   ```kotlin
   const val KEY_YOUR_SETTING = "your_setting"
   const val DEFAULT_YOUR_SETTING = 100
   ```

2. **在 SettingsState 中添加字段**
   ```kotlin
   data class SettingsState(
       // ...
       val yourSetting: Int
   )
   ```

3. **在 ConfigRepository 中添加读写**
   ```kotlin
   yourSetting = prefs.getInt(KEY_YOUR_SETTING, DEFAULT_YOUR_SETTING)
   ```

4. **在 UI 中添加控件**
   ```kotlin
   SettingSlider(
       title = "Your Setting",
       value = settings.yourSetting,
       onValueChange = { 
           onSettingsChange(settings.copy(yourSetting = it))
       }
   )
   ```

### 调试 Xposed Hook

1. **查看日志**
   ```bash
   adb logcat | grep EdgeGesture
   ```

2. **在 LSPosed 中查看**
   - 打开 LSPosed Manager
   - 进入日志页面
   - 搜索 "EdgeGesture"

3. **添加调试日志**
   ```kotlin
   XposedBridge.log("EdgeGesture: your debug message")
   ```

## 版本兼容性

使用 `VersionCompat` 工具类处理不同 Android 版本：

```kotlin
if (VersionCompat.isTiramisuOrHigher()) {
    // Android 13+ 特性
    context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
} else {
    // 兼容旧版本
    context.registerReceiver(receiver, filter)
}
```

## 性能优化建议

1. **使用 remember 缓存计算结果**
   ```kotlin
   val expensiveValue = remember(key) {
       computeExpensiveValue()
   }
   ```

2. **避免不必要的重组**
   ```kotlin
   @Composable
   fun MyComponent(
       data: Data,
       modifier: Modifier = Modifier
   ) {
       // 使用 derivedStateOf 避免重组
       val derived by remember {
           derivedStateOf { data.transform() }
       }
   }
   ```

3. **使用 LaunchedEffect 处理副作用**
   ```kotlin
   LaunchedEffect(key) {
       // 只在 key 变化时执行
   }
   ```

## 发布流程

1. **更新版本号**
   - 在 `app/build.gradle.kts` 中更新 `versionCode` 和 `versionName`

2. **运行完整检查**
   ```bash
   ./gradlew clean check
   ```

3. **构建 Release 版本**
   ```bash
   ./gradlew assembleRelease
   ```

4. **签名 APK**
   - 配置 `signing.properties`
   - 或使用 Android Studio 的签名工具

5. **创建 Release**
   - 在 GitHub 上创建新的 Release
   - 上传签名后的 APK
   - 编写 Release Notes

## 常见问题

### Q: 构建失败，提示找不到依赖
A: 运行 `./gradlew --refresh-dependencies` 刷新依赖

### Q: Detekt 报告太多问题
A: 可以在 `app/config/detekt.yml` 中调整规则

### Q: 测试失败
A: 确保使用 `testDispatcher.scheduler.advanceUntilIdle()` 等待协程完成

### Q: Hook 不生效
A: 
1. 确认 LSPosed 已启用模块
2. 检查作用域包含 Android 系统
3. 重启设备
4. 查看 LSPosed 日志

## 资源链接

- [Jetpack Compose 文档](https://developer.android.com/jetpack/compose)
- [Kotlin Coroutines 指南](https://kotlinlang.org/docs/coroutines-guide.html)
- [LSPosed 文档](https://github.com/LSPosed/LSPosed)
- [Detekt 规则](https://detekt.dev/docs/rules/complexity)
- [MockK 文档](https://mockk.io/)

## 贡献指南

欢迎贡献！请遵循以下步骤：

1. Fork 项目
2. 创建功能分支
3. 编写代码和测试
4. 运行代码质量检查
5. 提交 Pull Request

详细信息请参考 [CONTRIBUTING.md](CONTRIBUTING.md)（待创建）。

## 许可证

本项目采用 GPL-3.0 许可证。详见 [LICENSE](LICENSE) 文件。
