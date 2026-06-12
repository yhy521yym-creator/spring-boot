Write-Host "=== 测试 ceshi1 登录和 API ===" -ForegroundColor Green
Write-Host ""

# 1. 登录
$body = @{
    username = "ceshi1"
    password = "123456"
} | ConvertTo-Json

Write-Host "1. 登录 ceshi1" -ForegroundColor Yellow
$loginRes = Invoke-RestMethod -Uri "http://localhost:9093/api/auth/login" -Method POST -Body $body -ContentType "application/json"
Write-Host "登录响应:" -ForegroundColor Cyan
$loginRes | ConvertTo-Json -Depth 4
Write-Host ""

if ($loginRes.code -eq 0) {
    $token = $loginRes.data.token
    $headers = @{ "Authorization" = "Bearer $token" }

    Write-Host "2. 调用 /me 接口" -ForegroundColor Yellow
    $meRes = Invoke-RestMethod -Uri "http://localhost:9093/api/auth/me" -Method GET -Headers $headers
    Write-Host "/me 响应:" -ForegroundColor Cyan
    $meRes | ConvertTo-Json -Depth 4
    Write-Host ""

    Write-Host "3. 直接查询数据库" -ForegroundColor Yellow
    mysql -u root -p1234 service_reservation_system -e "SELECT id, username, role, merchant_id FROM sys_user WHERE username='ceshi1';"
} else {
    Write-Host "登录失败!" -ForegroundColor Red
}
