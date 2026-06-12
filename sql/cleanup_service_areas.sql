-- 数据清理脚本 - 处理服务区重复和命名问题
-- 注意：需要先处理商户表的外键约束

-- 1. 先将商户的service_area_id更新为有效的ID (使用id=6的服务区)
UPDATE merchant SET service_area_id = 6 WHERE service_area_id IN (1, 3, 4, 5, 27, 28, 29);

-- 2. 删除测试服务区
DELETE FROM service_area WHERE id = 30;

-- 3. 删除不符合命名规范的北京、广州、深圳、成都服务区
DELETE FROM service_area WHERE id IN (1, 3, 4, 5);

-- 4. 删除重复的"G2京沪高速无锡服务区"(ID 27,28,29)
DELETE FROM service_area WHERE name = 'G2京沪高速无锡服务区' AND id IN (27, 28, 29);

-- 5. 更新剩余重复服务区的名称和地址
UPDATE service_area SET name = 'G2京沪高速无锡惠山服务区', address = '江苏省无锡市惠山区G2京沪高速' WHERE id = 26;
UPDATE service_area SET name = 'G2京沪高速无锡江阴服务区', address = '江苏省无锡市江阴市G2京沪高速' WHERE id = 24;
UPDATE service_area SET name = 'G60沪杭高速嘉兴秀洲服务区', address = '浙江省嘉兴市秀洲区G60沪杭高速' WHERE id = 22;
UPDATE service_area SET name = 'G60沪杭高速嘉兴海宁服务区', address = '浙江省嘉兴市海宁市G60沪杭高速' WHERE id = 23;
UPDATE service_area SET name = 'G2京沪高速苏州相城服务区', address = '江苏省苏州市相城区G2京沪高速' WHERE id = 7;
UPDATE service_area SET name = 'G15沈海高速苏州常熟服务区', address = '江苏省苏州市常熟市G15沈海高速' WHERE id = 8;
UPDATE service_area SET name = 'G15沈海高速苏州太仓服务区', address = '江苏省苏州市太仓市G15沈海高速' WHERE id = 9;
UPDATE service_area SET name = 'S58沪常高速上海青浦服务区', address = '上海市青浦区S58沪常高速' WHERE id = 10;
UPDATE service_area SET name = 'G60沪杭高速上海金山服务区', address = '上海市金山区G60沪杭高速' WHERE id = 11;
UPDATE service_area SET name = 'G15沈海高速宁波鄞州服务区', address = '浙江省宁波市鄞州区G15沈海高速' WHERE id = 12;
UPDATE service_area SET name = 'G15沈海高速温州乐清服务区', address = '浙江省温州市乐清市G15沈海高速' WHERE id = 13;
UPDATE service_area SET name = 'G15沈海高速福州闽侯服务区', address = '福建省福州市闽侯县G15沈海高速' WHERE id = 14;
UPDATE service_area SET name = 'G15沈海高速厦门集美服务区', address = '福建省厦门市集美区G15沈海高速' WHERE id = 15;
UPDATE service_area SET name = 'G15沈海高速广州白云服务区', address = '广东省广州市白云区G15沈海高速' WHERE id = 16;
UPDATE service_area SET name = 'G15沈海高速深圳宝安服务区', address = '广东省深圳市宝安区G15沈海高速' WHERE id = 17;
UPDATE service_area SET name = 'G15沈海高速珠海香洲服务区', address = '广东省珠海市香洲区G15沈海高速' WHERE id = 18;
UPDATE service_area SET name = 'G15沈海高速海口秀英服务区', address = '海南省海口市秀英区G15沈海高速' WHERE id = 19;
UPDATE service_area SET name = 'G25长深高速湖州南浔服务区', address = '浙江省湖州市南浔区G25长深高速' WHERE id = 20;
UPDATE service_area SET name = 'G25长深高速杭州余杭服务区', address = '浙江省杭州市余杭区G25长深高速' WHERE id = 21;
UPDATE service_area SET name = 'G2京沪高速南京栖霞服务区', address = '江苏省南京市栖霞区G2京沪高速' WHERE id = 25;

-- 6. 更新商户名称
UPDATE merchant SET name = '无锡惠山餐厅', type = 'RESTAURANT', description = '无锡服务区餐厅' WHERE id = 1;
UPDATE merchant SET name = '无锡惠山酒店', type = 'HOTEL', description = '无锡服务区酒店' WHERE id = 3;
UPDATE merchant SET name = '无锡惠山充电站', type = 'GENERAL', description = '新能源充电' WHERE id = 4;
UPDATE merchant SET name = '无锡惠山加油站', type = 'GENERAL', description = '加油服务' WHERE id = 5;
