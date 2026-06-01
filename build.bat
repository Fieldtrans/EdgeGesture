@echo off
chcp 65001 >nul
echo ========================================
echo EdgeGesture v1.2.0 编译脚本
echo ========================================
echo.

echo 当前目录: %CD%
echo 切换到项目目录...
cd /d E:\MyEdgeGesture
echo 项目目录: %CD%
echo.

echo [1/3] 清理旧的构建文件...
gradlew.bat clean
if errorlevel 1 (
    echo.
    echo 错误: 清理失败
    echo 请检查 JDK 是否已安装
    pause
    exit /b 1
)
echo 清理完成!
echo.

echo [2/3] 开始编译 Release 版本...
echo 这可能需要 3-5 分钟，请耐心等待...
echo.
gradlew.bat assembleRelease
if errorlevel 1 (
    echo.
    echo 错误: 编译失败
    echo 请查看上面的错误信息
    pause
    exit /b 1
)
echo.
echo 编译完成!
echo.

echo [3/3] 检查 APK 文件...
if exist "app\build\outputs\apk\release\app-release.apk" (
    echo ========================================
    echo 编译成功! APK 已生成
    echo ========================================
    echo.
    echo APK 位置:
    echo %CD%\app\build\outputs\apk\release\app-release.apk
    echo.
    echo 文件信息:
    dir "app\build\outputs\apk\release\app-release.apk" | findstr "app-release.apk"
    echo.
    echo ========================================
    echo 下一步: 安装到手机
    echo ========================================
    echo.
    echo 方法 1: 通过 ADB 安装
    echo   adb install -r app\build\outputs\apk\release\app-release.apk
    echo.
    echo 方法 2: 手动安装
    echo   将 APK 复制到手机，点击安装
    echo.
    echo 方法 3: 打开 APK 所在文件夹
    echo   按任意键打开文件夹...
    pause >nul
    explorer "app\build\outputs\apk\release"
) else (
    echo.
    echo 错误: APK 文件未生成
    echo 请检查编译日志中的错误信息
    echo.
)

echo.
echo 按任意键退出...
pause >nul
