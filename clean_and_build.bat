@echo off
echo ========================================================
echo   Ejecutando Clean and Build (Apache NetBeans Ant)
echo ========================================================
call "C:\Program Files\Apache NetBeans\extide\ant\bin\ant.bat" -f "%~dp0build.xml" clean jar
if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================================
    echo   [OK] BUILD SUCCESSFUL - Compilacion exitosa
    echo   Archivo generado: dist\SistemaDeAeropuerto.jar
    echo ========================================================
) else (
    echo.
    echo ========================================================
    echo   [ERROR] Hubo un problema durante la compilacion
    echo ========================================================
)
pause
