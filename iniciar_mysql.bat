@echo off
title Servidor MySQL 9.7 - CORPAC
echo =========================================================
echo    INICIANDO SERVIDOR MYSQL 9.7 (CORPAC S.A.)
echo =========================================================
netstat -ano | findstr :3306 >nul
if %errorLevel% equ 0 (
    echo [OK] El servidor MySQL ya esta en ejecucion en el puerto 3306.
    goto fin
)

echo Iniciando motor mysqld en segundo plano...
start /b "" "C:\Program Files\MySQL\MySQL Server 9.7\bin\mysqld.exe" --datadir="C:\Users\Computecno\Documents\Algoritmos\mysql_data" --port=3306
timeout /t 3 /nobreak >nul
netstat -ano | findstr :3306 >nul
if %errorLevel% equ 0 (
    echo.
    echo =========================================================
    echo   [OK] MySQL Server 9.7 activo y escuchando en el puerto 3306!
    echo   Base de datos: aeropuerto_db cargada y lista.
    echo =========================================================
) else (
    echo [ALERTA] Verifique si otro proceso esta ocupando el puerto 3306.
)

:fin
timeout /t 3
exit
