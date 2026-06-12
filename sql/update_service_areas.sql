-- 服务区数据更新
-- 统一命名格式：[高速编号][方向][位置]服务区，并确保名称唯一

-- 更新服务区名称（确保唯一性）
UPDATE service_area SET name = 'G2京沪高速无锡惠山服务区' WHERE id = 1;
UPDATE service_area SET name = 'G42沪宁高速苏州相城服务区' WHERE id = 2;
UPDATE service_area SET name = 'G60沪杭高速嘉兴秀洲服务区' WHERE id = 3;
UPDATE service_area SET name = 'G15沈海高速常熟服务区' WHERE id = 4;
UPDATE service_area SET name = 'G25长深高速湖州南浔服务区' WHERE id = 5;
UPDATE service_area SET name = 'S58沪常高速上海青浦服务区' WHERE id = 6;
UPDATE service_area SET name = 'G2京沪高速无锡江阴服务区' WHERE id = 7;
UPDATE service_area SET name = 'G60沪杭高速嘉兴海宁服务区' WHERE id = 8;
UPDATE service_area SET name = 'S26沪常高速浙江嘉善服务区' WHERE id = 9;
UPDATE service_area SET name = 'G15沈海高速苏州太仓服务区' WHERE id = 10;

-- 更新地址信息使其更准确
UPDATE service_area SET address = '江苏省无锡市惠山区G2京沪高速' WHERE id = 1;
UPDATE service_area SET address = '江苏省苏州市相城区G42沪宁高速' WHERE id = 2;
UPDATE service_area SET address = '浙江省嘉兴市秀洲区G60沪杭高速' WHERE id = 3;
UPDATE service_area SET address = '江苏省苏州市常熟市G15沈海高速' WHERE id = 4;
UPDATE service_area SET address = '浙江省湖州市南浔区G25长深高速' WHERE id = 5;
UPDATE service_area SET address = '上海市青浦区S58沪常高速' WHERE id = 6;
UPDATE service_area SET address = '江苏省无锡市江阴市G2京沪高速' WHERE id = 7;
UPDATE service_area SET address = '浙江省嘉兴市海宁市G60沪杭高速' WHERE id = 8;
UPDATE service_area SET address = '浙江省嘉兴市嘉善县S26沪常高速' WHERE id = 9;
UPDATE service_area SET address = '江苏省苏州市太仓市G15沈海高速' WHERE id = 10;
