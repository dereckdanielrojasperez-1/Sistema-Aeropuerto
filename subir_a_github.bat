@echo off
echo ======================================================================
echo   SISTEMA DE AEROPUERTO (CORPAC S.A.) - SUBIR A GITHUB
echo ======================================================================
echo.
echo Rama: main
echo Destino: https://github.com/dereckdanielrojasperez-1/Sistema-Aeropuerto.git
echo.
"C:\Program Files\Microsoft SQL Server Management Studio 22\Release\Common7\IDE\CommonExtensions\Microsoft\TeamFoundation\Team Explorer\Git\cmd\git.exe" push -u origin main
echo.
if %ERRORLEVEL% equ 0 (
    echo [EXITO] Proyecto subido exitosamente a GitHub.
) else (
    echo [INFO] Si el repositorio aun no existe en tu GitHub:
    echo        1. Entra a https://github.com/new
    echo        2. Nombre: Sistema-Aeropuerto
    echo        3. Crealo vacio (sin README ni .gitignore)
    echo        4. Vuelve a ejecutar este archivo.
)
echo.
pause
