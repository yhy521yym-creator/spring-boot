-- 商户表数据
-- 为10个服务区各添加3-5个商户

-- 服务区1: G2京沪高速无锡服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status)
VALUES
(1, '味千拉面馆', 'RESTAURANT', '正宗日式拉面', '无锡服务区A区1楼', '0510-88880001', '08:00-22:00', 80, 1),
(1, '星巴克咖啡', 'RESTAURANT', '美式咖啡及甜点', '无锡服务区A区2楼', '0510-88880002', '07:00-23:00', 50, 1),
(1, '无锡太湖酒店', 'HOTEL', '商务舒适型酒店', '无锡服务区B区3楼', '0510-88880003', '24小时', 40, 1),
(1, '途虎养车', 'GENERAL', '汽车保养维修', '无锡服务区C区', '0510-88880004', '08:00-20:00', 20, 1);

-- 服务区2: G42沪宁高速苏州服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status)
VALUES
(2, '苏州得月楼', 'RESTAURANT', '苏帮菜特色餐厅', '苏州服务区A区1楼', '0512-88880011', '09:00-21:00', 100, 1),
(2, '麦当劳', 'RESTAURANT', '快餐连锁', '苏州服务区A区2楼', '0512-88880012', '07:00-23:00', 60, 1),
(2, '苏州园林酒店', 'HOTEL', '精品文化酒店', '苏州服务区B区', '0512-88880013', '24小时', 50, 1),
(2, '特斯拉超充', 'GENERAL', '新能源汽车充电', '苏州服务区C区', '0512-88880014', '24小时', 10, 1),
(2, '中石化加油站', 'GENERAL', '加油加气服务', '苏州服务区D区', '0512-88880015', '24小时', 8, 1);

-- 服务区3: G60沪杭高速嘉兴服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status)
VALUES
(3, '嘉兴五芳斋', 'RESTAURANT', '粽子特色小吃', '嘉兴服务区A区', '0573-88880021', '07:00-21:00', 60, 1),
(3, '嘉兴南湖酒店', 'HOTEL', '红色文化主题酒店', '嘉兴服务区B区', '0573-88880022', '24小时', 45, 1),
(3, '蔚来充电站', 'GENERAL', '新能源汽车充电', '嘉兴服务区C区', '0573-88880023', '24小时', 8, 1);

-- 服务区4: G15沈海高速常熟服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status)
VALUES
(4, '常熟蒸菜馆', 'RESTAURANT', '地道常熟蒸菜', '常熟服务区A区', '0512-88880031', '08:00-20:00', 70, 1),
(4, '常熟国际酒店', 'HOTEL', '五星级商务酒店', '常熟服务区B区', '0512-88880032', '24小时', 80, 1),
(4, '京东便利店', 'GENERAL', '24小时便利店', '常熟服务区C区', '0512-88880033', '24小时', 100, 1),
(4, '途虎养车', 'GENERAL', '汽车快修保养', '常熟服务区D区', '0512-88880034', '08:00-20:00', 15, 1);

-- 服务区5: G25长深高速湖州服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status)
VALUES
(5, '湖州丁莲芳', 'RESTAURANT', '千张包子特色店', '湖州服务区A区', '0572-88880041', '08:00-21:00', 55, 1),
(5, '湖州太湖酒店', 'HOTEL', '湖景商务酒店', '湖州服务区B区', '0572-88880042', '24小时', 40, 1),
(5, '国家电网充电', 'GENERAL', '电动车充电桩', '湖州服务区C区', '0572-88880043', '24小时', 12, 1);

-- 服务区6: S58沪常高速淀山湖服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status)
VALUES
(6, '淀山湖农家菜', 'RESTAURANT', '本帮农家风味', '淀山湖服务区A区', '021-88880051', '08:00-21:00', 65, 1),
(6, '淀山湖度假酒店', 'HOTEL', '湖景度假酒店', '淀山湖服务区B区', '021-88880052', '24小时', 60, 1),
(6, '便利蜂', 'GENERAL', '智能便利店', '淀山湖服务区C区', '021-88880053', '24小时', 80, 1),
(6, '小鹏充电', 'GENERAL', '新能源汽车充电', '淀山湖服务区D区', '021-88880054', '24小时', 8, 1),
(6, '中化石油', 'GENERAL', '加油加气', '淀山湖服务区E区', '021-88880055', '24小时', 10, 1);

-- 服务区7: G2京沪高速江阴服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status)
VALUES
(7, '江阴河豚鱼馆', 'RESTAURANT', '长江鲜特色', '江阴服务区A区', '0510-88880061', '09:00-20:00', 50, 1),
(7, '江阴国际饭店', 'HOTEL', '江景商务酒店', '江阴服务区B区', '0510-88880062', '24小时', 70, 1),
(7, '快充易充', 'GENERAL', '充电桩服务', '江阴服务区C区', '0510-88880063', '24小时', 10, 1);

-- 服务区8: G60沪杭高速长安服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status)
VALUES
(8, '长安面馆', 'RESTAURANT', '浙江特色面食', '长安服务区A区', '0573-88880071', '07:00-21:00', 45, 1),
(8, '钱塘酒店', 'HOTEL', '钱塘江景酒店', '长安服务区B区', '0573-88880072', '24小时', 55, 1),
(8, '全家便利店', 'GENERAL', '日本连锁便利店', '长安服务区C区', '0573-88880073', '24小时', 90, 1),
(8, '理想充电', 'GENERAL', '新能源车充电', '长安服务区D区', '0573-88880074', '24小时', 6, 1);

-- 服务区9: S26沪常高速嘉善服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status)
VALUES
(9, '嘉善老娘舅', 'RESTAURANT', '本地家常菜', '嘉善服务区A区', '0573-88880081', '08:00-20:00', 50, 1),
(9, '嘉善宾馆', 'HOTEL', '商务会议酒店', '嘉善服务区B区', '0573-88880082', '24小时', 45, 1),
(9, '来充充电', 'GENERAL', '电单车充电桩', '嘉善服务区C区', '0573-88880083', '24小时', 20, 1);

-- 服务区10: G15沈海高速太仓服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status)
VALUES
(10, '太仓肉松店', 'RESTAURANT', '太仓特色肉松', '太仓服务区A区', '0512-88880091', '08:00-21:00', 40, 1),
(10, '太仓花园酒店', 'HOTEL', '花园式度假酒店', '太仓服务区B区', '0512-88880092', '24小时', 65, 1),
(10, '罗森便利店', 'GENERAL', '24小时便利店', '太仓服务区C区', '0512-88880093', '24小时', 85, 1),
(10, '壳牌加油站', 'GENERAL', '国际品牌加油站', '太仓服务区D区', '0512-88880094', '24小时', 12, 1);

-- 重置merchant表的自增计数器
ALTER TABLE merchant ALTER COLUMN id RESTART WITH 1;
