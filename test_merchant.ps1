$body = @{
    username = "ceshi1"
    password = "123456"
} | ConvertTo-Json

Write-Host "1. 测试ceshi1用户登录..." -ForegroundColor Yellow
$loginResponse = Invoke-RestMethod -Uri "http://localhost:9093/api/auth/login" -Method POST -Body $body -ContentType "application/json"
Write-Host "登录响应: $($loginResponse | ConvertTo-Json -Depth 3)" -ForegroundColor Cyan

if ($loginResponse.code -eq 0) {
    $token = $loginResponse.data.token
    Write-Host "Token获取成功: $($token.Substring(0, [Math]::Min(20, $token.Length)))..." -ForegroundColor Green

    $headers = @{
        "Authorization" = "Bearer $token"
    }

    Write-Host "`n2. 测试获取当前用户信息..." -ForegroundColor Yellow
    $meResponse = Invoke-RestMethod -Uri "http://localhost:9093/api/auth/me" -Method GET -Headers $headers
    Write-Host "用户信息: $($meResponse | ConvertTo-Json -Depth 3)" -ForegroundColor Cyan

    $merchantId = $meResponse.data.merchantId
    Write-Host "MerchantId: $merchantId" -ForegroundColor Green

    if ($merchantId) {
        Write-Host "`n3. 测试获取商家订单列表..." -ForegroundColor Yellow
        $ordersResponse = Invoke-RestMethod -Uri "http://localhost:9093/api/orders/page?pageNum=1&pageSize=10&merchantId=$merchantId" -Method GET -Headers $headers
        Write-Host "订单响应: $($ordersResponse | ConvertTo-Json -Depth 3)" -ForegroundColor Cyan
    } else {
        Write-Host "`n警告: merchantId为空!" -ForegroundColor Red
    }
} else {
    Write-Host "登录失败: $($loginResponse.message)" -ForegroundColor Red
}
