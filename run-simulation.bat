@echo off
chcp 65001 >nul
echo.
echo ╔══════════════════════════════════════════════════════╗
echo ║       ЗАПУСК СИМУЛЯЦИИ ИСКУССТВЕННОЙ ЖИЗНИ          ║
echo ╚══════════════════════════════════════════════════════╝
echo.
echo Компиляция...
javac -d out src\*.java
if %errorlevel% neq 0 (
    echo ❌ Ошибка компиляции!
    pause
    exit /b 1
)

echo ✅ Компиляция завершена!
echo Запуск симуляции...
echo.
java -cp out Main

pause

