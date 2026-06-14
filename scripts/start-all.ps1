param(
    [switch]$SkipDocker,
    [switch]$StartUi
)

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot

Set-Location $Root

if (-not $SkipDocker) {
    docker compose up -d
}

$ports = @(8082, 8083, 8084, 8085, 8086)
$busyPorts = foreach ($port in $ports) {
    Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
}

if ($busyPorts) {
    Write-Host "One or more service ports are already in use:" -ForegroundColor Yellow
    foreach ($connection in $busyPorts) {
        $process = Get-Process -Id $connection.OwningProcess -ErrorAction SilentlyContinue
        $name = if ($process) { $process.ProcessName } else { "unknown" }
        Write-Host "Port $($connection.LocalPort) is used by PID $($connection.OwningProcess) ($name)"
    }
    Write-Host "Close the old service windows or run: .\scripts\stop-all.ps1" -ForegroundColor Yellow
    exit 1
}

.\mvnw.cmd -DskipTests install
.\mvnw.cmd org.springframework.boot:spring-boot-maven-plugin:3.3.11:help -Ddetail=false

$services = @(
    "energy-communities-platform-rest-api",
    "usage-service",
    "current-percentage-service",
    "energy-producer-service",
    "energy-user-service"
)

foreach ($service in $services) {
    Start-Process powershell -WindowStyle Normal -ArgumentList @(
        "-NoExit",
        "-Command",
        "cd '$Root'; .\mvnw.cmd -pl $service spring-boot:run"
    )
    Start-Sleep -Seconds 2
}

if ($StartUi) {
    Start-Sleep -Seconds 15
    Start-Process powershell -WindowStyle Normal -ArgumentList @(
        "-NoExit",
        "-Command",
        "cd '$Root'; .\mvnw.cmd -pl ui-javafx javafx:run"
    )
}

Write-Host "Started infrastructure and Spring services."
if ($StartUi) {
    Write-Host "JavaFX UI startup requested."
}
Write-Host "REST API: http://localhost:8082"
Write-Host "Producer: http://localhost:8083/health"
Write-Host "User: http://localhost:8084/health"
Write-Host "Usage: http://localhost:8085/health"
Write-Host "Current Percentage: http://localhost:8086/health"
Write-Host "RabbitMQ Management: http://localhost:15672 (guest / guest)"
