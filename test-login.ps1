$body = '{"username": "admin", "password": "123456"}';
$response = Invoke-WebRequest -Uri 'http://localhost:9091/auth/login' -Method POST -Headers @{'Content-Type'='application/json'} -Body $body -UseBasicParsing;
$response.Content;