@echo off
echo Testing Moker HTTP Server...
echo.

echo Test 1: Testing root path
curl -i http://localhost:8080/
echo.

echo Test 2: Testing API endpoint (if configured)
echo Please configure a route in the GUI first, then test it with:
echo curl -i http://localhost:8080/your/path
echo.

pause
