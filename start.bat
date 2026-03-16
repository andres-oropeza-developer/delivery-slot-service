@echo off
setlocal enabledelayedexpansion

echo.
echo ============================================================
echo   Delivery Slot Service - Walmart Chile
echo ============================================================
echo.

:: ── Check Java ─────────────────────────────────────────────
echo [1/4] Verificando Java 21...

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
        goto :java_ok
    )
)

echo      Instalando Java 21...
winget install Microsoft.OpenJDK.21 --silent --accept-package-agreements --accept-source-agreements
for /d %%d in ("C:\Program Files\Microsoft\jdk-21*") do (
    if exist "%%d\bin\java.exe" (
        set "PATH=%%d\bin;%PATH%"
        goto :java_ok
    )
)
echo      No se pudo configurar Java. Instala desde: https://adoptium.net
pause & exit /b 1

:java_ok
echo      Java OK

:: ── Check Node.js ──────────────────────────────────────────
echo.
echo [2/4] Verificando Node.js...
node --version >nul 2>&1
if %errorlevel% equ 0 (
    echo      Node.js OK
    goto :node_ok
)
echo      Instalando Node.js...
winget install OpenJS.NodeJS.LTS --silent --accept-package-agreements --accept-source-agreements
node --version >nul 2>&1
if %errorlevel% neq 0 (
    start cmd /k "cd /d %~dp0 && call start.bat"
    exit /b 0
)

:node_ok

:: ── Setup Maven Wrapper ────────────────────────────────────
echo.
echo [3/4] Preparando Maven...

cd /d "%~dp0"

:: Create .mvn\wrapper folder and download jar if missing
if not exist ".mvn\wrapper" mkdir ".mvn\wrapper"

if not exist ".mvn\wrapper\maven-wrapper.properties" (
    echo distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.zip> .mvn\wrapper\maven-wrapper.properties
    echo wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar>> .mvn\wrapper\maven-wrapper.properties
)

if not exist ".mvn\wrapper\maven-wrapper.jar" (
    echo      Descargando Maven Wrapper...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar' -OutFile '.mvn\wrapper\maven-wrapper.jar'"
)

if not exist "mvnw.cmd" (
    echo      ERROR: mvnw.cmd no encontrado.
    pause & exit /b 1
)

:: Install frontend dependencies
if exist "frontend\package.json" (
    echo      Instalando dependencias del frontend...
    cd frontend
    call npm install --silent 2>nul
    cd ..
)

:: Download backend dependencies
call mvnw.cmd dependency:resolve -q 2>nul

:: ── Start services ─────────────────────────────────────────
echo.
echo [4/4] Iniciando servicios...
echo.
echo ============================================================
echo   React UI:      http://localhost:5173
echo   Thymeleaf UI:  http://localhost:8080
echo   API Docs:      http://localhost:8080/swagger-ui.html
echo   H2 Console:    http://localhost:8080/h2-console
echo ============================================================
echo   Presiona Ctrl+C para detener el backend.
echo   El frontend se cierra con su ventana.
echo ============================================================
echo.

if exist "frontend\package.json" (
    start "React Frontend" cmd /k "cd /d %~dp0\frontend && npm run dev"
)

start /b cmd /c "timeout /t 12 /nobreak >nul && start http://localhost:5173 && start http://localhost:8080"

call mvnw.cmd spring-boot:run

echo.
echo Backend detenido.
pause