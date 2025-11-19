@echo off
chcp 65001 >nul
echo.
echo ╔══════════════════════════════════════════════════════╗
echo ║     ЗАПУСК ТЕСТОВ СИМУЛЯЦИИ ИСКУССТВЕННОЙ ЖИЗНИ     ║
echo ╚══════════════════════════════════════════════════════╝
echo.
echo Компиляция исходного кода...
javac -d out src\*.java
if %errorlevel% neq 0 (
    echo ❌ Ошибка компиляции исходного кода!
    pause
    exit /b 1
)

echo Компиляция тестов...
javac -cp out -d out test\*.java
if %errorlevel% neq 0 (
    echo ❌ Ошибка компиляции тестов!
    pause
    exit /b 1
)

echo.
echo ✅ Компиляция завершена успешно!
echo.
echo Запуск тестов...
echo.
java -ea -cp out TestRunner

echo.
pause

