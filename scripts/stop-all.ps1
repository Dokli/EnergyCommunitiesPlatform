$ErrorActionPreference = "Stop"

$ports = @(8082, 8083, 8084, 8085, 8086)
$connections = foreach ($port in $ports) {
    Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
}

if (-not $connections) {
    Write-Host "No project service ports are currently listening."
    exit 0
}

$pids = $connections | Select-Object -ExpandProperty OwningProcess -Unique

foreach ($processId in $pids) {
    $process = Get-Process -Id $processId -ErrorAction SilentlyContinue
    if ($process) {
        Write-Host "Stopping PID $processId ($($process.ProcessName))"
        Stop-Process -Id $processId -Force
    }
}

Write-Host "Stopped project services on ports 8082-8086."
