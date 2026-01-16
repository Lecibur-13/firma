@echo off
echo Cambiando al directorio raiz del proyecto...
cd /d "%~dp0.."
echo Directorio actual: %CD%
echo.
echo Ejecutando Maven clean install...
mvn clean install -Denv=install -DskipTests
if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Build completado exitosamente!
    echo ========================================
) else (
    echo.
    echo ========================================
    echo Error en el build!
    echo ========================================
    exit /b %ERRORLEVEL%
)
pause
