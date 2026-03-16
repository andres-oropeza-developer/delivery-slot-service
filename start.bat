@echo off
setlocal enabledelayedexpansion

echo.
echo ============================================================
echo   Delivery Slot Service - Walmart Chile
echo ============================================================
echo.

echo [1/3] Buscando Java 21...

java -version >nul 2>&1
if %errorlevel% equ 0 goto :java_ok

for /d %%d in ("C:\Program Files\Microsoft\jdk-21*") do (
    if exist "%%d\bin\java.exe" (
        set "PATH=%%d\bin;%PATH%"
        echo      Java encontrado en: %%d
        goto :java_ok
    )
)

for /d %%d in ("C:\Program Files\Eclipse Adoptium\jdk-21*") do (
    if exist "%%d\bin\java.exe" (
        set "PATH=%%d\bin;%PATH%"
        echo      Java encontrado en: %%d
        goto :java_ok
    )
)

for /d %%d in ("C:\Program Files\Java\jdk-21*") do (
    if exist "%%d\bin\java.exe" (
        set "PATH=%%d\bin;%PATH%"
        echo      Java encontrado en: %%d
        goto :java_ok
    )
)

echo      Java 21 no encontrado. Instalando...
winget install Microsoft.OpenJDK.21 --silent --accept-package-agreements --accept-source-agreements

for /d %%d in ("C:\Program Files\Microsoft\jdk-21*") do (
    if exist "%%d\bin\java.exe" (
        set "PATH=%%d\bin;%PATH%"
        echo      Java 21 instalado.
        goto :java_ok
    )
)

echo.
echo      No se pudo configurar Java.
echo      Instala Java 21 desde: https://adoptium.net
echo      y vuelve a ejecutar start.bat
pause
exit /b 1

:java_ok
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo      Java instalado pero PATH no actualizado.
    echo      Abriendo nueva terminal...
    for /d %%d in ("C:\Program Files\Microsoft\jdk-21*") do (
        if exist "%%d\bin\java.exe" (
            start cmd /k "set PATH=%%d\bin;%PATH% && cd /d %~dp0 && call start.bat"
            exit /b 0
        )
    )
)

echo      Java OK
echo.
echo [2/3] Descargando dependencias y compilando...
echo      La primera vez puede tardar 2-3 minutos.
echo.

cd /d "%~dp0"

if not exist "mvnw.cmd" (
    echo      ERROR: mvnw.cmd no encontrado en %CD%
    echo      Asegurate de que start.bat y mvnw.cmd esten en la misma carpeta.
    pause
    exit /b 1
)

call mvnw.cmd dependency:resolve -q 2>nul

echo.
echo [3/3] Iniciando la aplicacion...
echo.
echo ============================================================
echo   Frontend:   http://localhost:8080
echo   API Docs:   http://localhost:8080/swagger-ui.html
echo   H2 Console: http://localhost:8080/h2-console
echo ============================================================
echo.

start /b cmd /c "timeout /t 12 /nobreak >nul && start http://localhost:8080"

call mvnw.cmd spring-boot:run

echo.
echo Aplicacion detenida.
pause
