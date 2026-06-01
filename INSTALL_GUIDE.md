# 🚀 EdgeGesture v1.2.0 安装指南

## 快速开始

### 步骤 1: 编译项目

打开命令行，进入项目目录：

```bash
cd E:\MyEdgeGesture

# 清理旧的构建文件
gradlew clean

# 构建 Release 版本
gradlew assembleRelease
```

**预计时间**: 首次编译 3-5 分钟，后续 1-2 分钟

---

### 步骤 2: 找到 APK

编译成功后，APK 位于：
```
E:\MyEdgeGesture\app\build\outputs\apk\release\app-release.apk
```

**文件信息**:
- 版本: 1.2.0 (versionCode 14)
- 大小: 约 3-4 MB
- 支持: Android 8.0+ (API 26+)

---

### 步骤 3: 安装到手机

#### 方法 A: 通过 ADB (推荐)
```bash
# 确保手机已连接并开启 USB 调试
adb devices

# 安装 APK (-r 表示覆盖安装)
adb install -r app\build\outputs\apk\release\app-release.apk
```

#### 方法 B: 手动安装
1. 将 `app-release.apk` 复制到手机
2. 在手机上打开文件管理器
3. 点击 APK 文件安装

---

## 📱 配置和使用

### 模式 1: 无障碍模式 (免 Root)

1. **打开应用**
   - 启动 EdgeGesture

2. **启用无障碍服务**
   - 点击"打开无障碍设置"
   - 找到 EdgeGesture
   - 开启服务

3. **配置手势**
   - 返回应用
   - 调整触发区域和灵敏度
   - 测试边缘上划手势

---

### 模式 2: LSPosed 增强模式 (需要 Root)

#### 前置条件
- ✅ 手机已 Root
- ✅ 已安装 LSPosed 或 Xposed
- ✅ LSPosed 正常运行

#### 配置步骤

1. **安装应用**
   - 安装 EdgeGesture APK

2. **在 LSPosed 中启用**
   - 打开 LSPosed Manager
   - 找到 EdgeGesture 模块
   - 勾选启用
   - **重要**: 作用域选择 "Android 系统框架" 或 "System Framework"

3. **重启设备**
   ```bash
   # 通过 ADB 重启
   adb reboot
   ```

4. **验证模块状态**
   - 重启后打开 EdgeGesture
   - 查看"模块状态"
   - 应显示绿色圆点和"LSPosed 输入过滤器已启动"

5. **配置手势**
   - 调整设置
   - 测试边缘手势

---

## 🔍 验证安装

### 检查版本
打开应用，查看顶部标题栏，应显示：
- 版本: 1.2.0
- 支持: Android 8.0+

### 检查模块状态

#### 无障碍模式
- 状态: "无障碍服务已启用"
- 颜色: 绿色圆点

#### LSPosed 模式
- 状态: "LSPosed 输入过滤器已启动"
- 颜色: 绿色圆点
- 增强: ✓

### 测试手势
1. 从屏幕右侧边缘向内斜上划
2. 应出现绿色箭头或光标
3. 移动手指控制指针
4. 松手点击目标位置

---

## 🐛 故障排查

### 问题 1: 编译失败

**错误**: `Execution failed for task ':app:compileReleaseKotlin'`

**解决**:
```bash
# 刷新依赖
gradlew --refresh-dependencies

# 清理后重新构建
gradlew clean assembleRelease
```

---

### 问题 2: 模块未加载

**症状**: 状态显示"未检测到加载"

**解决**:
1. 确认 LSPosed 已正确安装
2. 检查作用域是否包含"系统框架"
3. 重启设备
4. 查看 LSPosed 日志，搜索 "EdgeGesture"

**查看日志**:
```bash
# 通过 ADB 查看日志
adb logcat | grep EdgeGesture
```

---

### 问题 3: 手势不响应

**无障碍模式**:
- 确认无障碍服务已启用
- 检查是否被其他应用占用
- 重启应用

**LSPosed 模式**:
- 确认模块状态为"已启动"
- 检查触发区域设置
- 查看 LSPosed 日志

---

### 问题 4: 与系统手势冲突

**症状**: 触发系统返回而不是应用手势

**解决**:
1. 调整"触发边缘宽度"（减小）
2. 调整"触发区域"（避开底部）
3. 增加"上划触发距离"

---

### 问题 5: 应用闪退

**查看崩溃日志**:
```bash
adb logcat | grep -i "crash\|exception"
```

**常见原因**:
- Android 版本不兼容（需要 8.0+）
- 权限未授予
- LSPosed 版本过旧

---

## 📊 性能优化建议

### 1. 降低卡顿
- 降低"控制圆透明度"（0-100）
- 减少"平滑度"（提高响应速度）

### 2. 提高精度
- 增加"直线箭头灵敏度"
- 调整"指针最大速度"

### 3. 减少误触
- 增加"上划触发距离"
- 调整"方向允许偏角"（减小）

---

## 🎯 推荐配置

### 新手配置
```
触发边缘宽度: 18dp
上划触发距离: 80dp
直线箭头灵敏度: 100%
控制圆透明度: 150
```

### 高级配置
```
触发边缘宽度: 15dp
上划触发距离: 100dp
直线箭头灵敏度: 120%
控制圆透明度: 80
平滑度: 30%
```

---

## 📝 配置导入导出

### 导出配置
1. 点击顶部"下载"图标
2. 选择保存位置
3. 文件名: `EdgeGesture-config.json`

### 导入配置
1. 点击顶部"上传"图标
2. 选择配置文件
3. 确认导入

---

## 🆕 v1.2.0 新特性

### 设备兼容性
- ✅ 支持 Android 8.0+ (之前仅支持 Android 16)
- ✅ 支持更多设备型号

### 性能改进
- ✅ 构建速度提升 30-50%
- ✅ 代码质量检查 (Detekt + ktlint)

### 架构升级
- ✅ MVVM 架构
- ✅ 单元测试框架
- ✅ 版本兼容性工具

### 开发体验
- ✅ 详细的开发文档
- ✅ 快速开始指南
- ✅ 代码质量工具

---

## 📚 相关文档

- **README.md** - 项目介绍
- **IMPROVEMENTS.md** - 改进详情
- **QUICKSTART.md** - 开发指南
- **BUILD_INSTRUCTIONS.md** - 编译说明
- **REFACTORING_PROGRESS.md** - 重构进度

---

## 💬 反馈和支持

### 问题反馈
- GitHub Issues: https://github.com/Fieldtrans/EdgeGesture/issues

### 查看日志
```bash
# LSPosed 日志
在 LSPosed Manager 中查看日志，搜索 "EdgeGesture"

# 系统日志
adb logcat | grep EdgeGesture
```

---

## ✅ 安装检查清单

- [ ] 项目编译成功
- [ ] APK 文件生成
- [ ] APK 安装到手机
- [ ] 应用可以启动
- [ ] 模块状态正常
- [ ] 手势可以触发
- [ ] 配置可以保存
- [ ] 无明显卡顿

---

**祝你使用愉快！** 🎉

如有问题，请查看故障排查部分或提交 Issue。
