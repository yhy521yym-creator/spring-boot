$body = ConvertTo-Json @{username="ceshi1";password="123456"}
$login = Invoke-RestMethod -Uri "http://localhost:9093/api/auth/login" -Method POST -Body $body -ContentType "application/json"
Write-Host "Login:"; $login | ConvertTo-Json -Depth 3
if ($login.code -eq 0) {
    $token = $login.data.token
    $h = @{"Authorization"="Bearer $token"}
    $me = Invoke-RestMethod -Uri "http://localhost:9093/api/auth/me" -Method GET -Headers $h
    Write-Host "`nMe:"; $me | ConvertTo-Json -Depth 3
}
