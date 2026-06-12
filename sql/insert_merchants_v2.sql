-- 为21个服务区各添加3-5个商户

-- 服务区6: G2京沪高速无锡惠山服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status) VALUES
(6, '惠山味道私房菜', 'RESTAURANT', '地道无锡本帮菜', '无锡惠山服务区A区', '0510-88880001', '08:00-21:00', 80, 1),
(6, '太湖度假酒店', 'HOTEL', '四星级商务酒店', '无锡惠山服务区B区', '0510-88880002', '24小时', 60, 1),
(6, '惠山便利店', 'GENERAL', '24小时便利店', '无锡惠山服务区C区', '0510-88880003', '24小时', 100, 1),
(6, '惠山充电站', 'GENERAL', '新能源快充桩', '无锡惠山服务区D区', '0510-88880004', '24小时', 12, 1);

-- 服务区7: G2京沪高速苏州相城服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status) VALUES
(7, '相城农家菜馆', 'RESTAURANT', '苏州农家特色菜', '苏州相城服务区A区', '0512-88880011', '08:00-21:00', 70, 1),
(7, '相城商务酒店', 'HOTEL', '商务舒适型', '苏州相城服务区B区', '0512-88880012', '24小时', 50, 1),
(7, '相城生活超市', 'GENERAL', '综合超市', '苏州相城服务区C区', '0512-88880013', '07:00-23:00', 80, 1);

-- 服务区8: G15沈海高速苏州常熟服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status) VALUES
(8, '常熟蒸菜馆', 'RESTAURANT', '正宗常熟蒸菜', '常熟服务区A区', '0512-88880021', '08:00-20:00', 60, 1),
(8, '常熟花园酒店', 'HOTEL', '精品花园酒店', '常熟服务区B区', '0512-88880022', '24小时', 55, 1),
(8, '常熟便利店', 'GENERAL', '24小时便利', '常熟服务区C区', '0512-88880023', '24小时', 70, 1),
(8, '常熟中石化加油站', 'GENERAL', '加油加气', '常熟服务区D区', '0512-88880024', '24小时', 10, 1);

-- 服务区9: G15沈海高速苏州太仓服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status) VALUES
(9, '太仓肉松骨头馆', 'RESTAURANT', '太仓特色美食', '太仓服务区A区', '0512-88880031', '08:00-21:00', 55, 1),
(9, '太仓度假酒店', 'HOTEL', '休闲度假型', '太仓服务区B区', '0512-88880032', '24小时', 45, 1),
(9, '太仓便利店', 'GENERAL', '连锁便利店', '太仓服务区C区', '0512-88880033', '24小时', 65, 1);

-- 服务区10: S58沪常高速上海青浦服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status) VALUES
(10, '淀山湖农家乐', 'RESTAURANT', '本帮农家风味', '青浦服务区A区', '021-88880041', '08:00-21:00', 65, 1),
(10, '淀山湖酒店', 'HOTEL', '湖景度假酒店', '青浦服务区B区', '021-88880042', '24小时', 60, 1),
(10, '青浦便利店', 'GENERAL', '智能便利店', '青浦服务区C区', '021-88880043', '24小时', 80, 1),
(10, '青浦特斯拉超充', 'GENERAL', '特斯拉专用充电', '青浦服务区D区', '021-88880044', '24小时', 8, 1);

-- 服务区11: G60沪杭高速上海金山服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status) VALUES
(11, '金山农家菜', 'RESTAURANT', '金山本地特色', '金山服务区A区', '021-88880051', '08:00-20:00', 50, 1),
(11, '金山商务酒店', 'HOTEL', '三星级商务', '金山服务区B区', '021-88880052', '24小时', 45, 1),
(11, '金山充电站', 'GENERAL', '国家电网充电', '金山服务区C区', '021-88880053', '24小时', 10, 1);

-- 服务区12: G15沈海高速宁波鄞州服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status) VALUES
(12, '鄞州海鲜楼', 'RESTAURANT', '宁波鲜活海鲜', '宁波鄞州服务区A区', '0574-88880061', '09:00-21:00', 70, 1),
(12, '鄞州大酒店', 'HOTEL', '四星标准', '宁波鄞州服务区B区', '0574-88880062', '24小时', 55, 1),
(12, '鄞州便利店', 'GENERAL', '24小时便利', '宁波鄞州服务区C区', '0574-88880063', '24小时', 75, 1),
(12, '鄞州中化石油', 'GENERAL', '加油服务', '宁波鄞州服务区D区', '0574-88880064', '24小时', 12, 1);

-- 服务区13: G15沈海高速温州乐清服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status) VALUES
(13, '乐清海鲜面馆', 'RESTAURANT', '温州特色面食', '温州乐清服务区A区', '0577-88880071', '07:00-21:00', 55, 1),
(13, '乐清酒店', 'HOTEL', '商务舒适', '温州乐清服务区B区', '0577-88880072', '24小时', 50, 1),
(13, '乐清便利店', 'GENERAL', '连锁便利', '温州乐清服务区C区', '0577-88880073', '24小时', 60, 1);

-- 服务区14: G15沈海高速福州闽侯服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status) VALUES
(14, '闽侯佛跳墙', 'RESTAURANT', '福州传统名菜', '福州闽侯服务区A区', '0591-88880081', '09:00-21:00', 60, 1),
(14, '闽侯温泉酒店', 'HOTEL', '温泉度假型', '福州闽侯服务区B区', '0591-88880082', '24小时', 50, 1),
(14, '闽侯便利店', 'GENERAL', '综合便利', '福州闽侯服务区C区', '0591-88880083', '24小时', 65, 1),
(14, '闽侯充电桩', 'GENERAL', '新能源充电', '福州闽侯服务区D区', '0591-88880084', '24小时', 8, 1);

-- 服务区15: G15沈海高速厦门集美服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status) VALUES
(15, '集美海鲜馆', 'RESTAURANT', '厦门鲜活海鲜', '厦门集美服务区A区', '0592-88880091', '08:00-22:00', 75, 1),
(15, '集美酒店', 'HOTEL', '海滨商务', '厦门集美服务区B区', '0592-88880092', '24小时', 60, 1),
(15, '集美便利店', 'GENERAL', '连锁便利', '厦门集美服务区C区', '0592-88880093', '24小时', 70, 1);

-- 服务区16: G15沈海高速广州白云服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status) VALUES
(16, '白云粤菜馆', 'RESTAURANT', '正宗广式粤菜', '广州白云服务区A区', '020-88880101', '07:00-22:00', 80, 1),
(16, '白云酒店', 'HOTEL', '四星商务', '广州白云服务区B区', '020-88880102', '24小时', 65, 1),
(16, '白云便利店', 'GENERAL', '24小时便利', '广州白云服务区C区', '020-88880103', '24小时', 85, 1),
(16, '白云充电站', 'GENERAL', '蔚来小鹏充电', '广州白云服务区D区', '020-88880104', '24小时', 10, 1);

-- 服务区17: G15沈海高速深圳宝安服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status) VALUES
(17, '宝安茶餐厅', 'RESTAURANT', '港式茶餐厅', '深圳宝安服务区A区', '0755-88880111', '08:00-23:00', 70, 1),
(17, '宝安国际酒店', 'HOTEL', '五星标准', '深圳宝安服务区B区', '0755-88880112', '24小时', 80, 1),
(17, '宝安便利店', 'GENERAL', '连锁便利', '深圳宝安服务区C区', '0755-88880113', '24小时', 90, 1);

-- 服务区18: G15沈海高速珠海香洲服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status) VALUES
(18, '香洲海鲜坊', 'RESTAURANT', '珠海鲜活海鲜', '珠海香洲服务区A区', '0756-88880121', '09:00-21:00', 60, 1),
(18, '香洲度假酒店', 'HOTEL', '海滨度假', '珠海香洲服务区B区', '0756-88880122', '24小时', 55, 1),
(18, '香洲便利店', 'GENERAL', '24小时便利', '珠海香洲服务区C区', '0756-88880123', '24小时', 65, 1),
(18, '香洲壳牌加油站', 'GENERAL', '国际品牌加油', '珠海香洲服务区D区', '0756-88880124', '24小时', 10, 1);

-- 服务区19: G15沈海高速海口秀英服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status) VALUES
(19, '秀英老爸茶', 'RESTAURANT', '海南特色老爸茶', '海口秀英服务区A区', '0898-88880131', '07:00-21:00', 55, 1),
(19, '秀英酒店', 'HOTEL', '热带度假', '海口秀英服务区B区', '0898-88880132', '24小时', 50, 1),
(19, '秀英便利店', 'GENERAL', '连锁便利', '海口秀英服务区C区', '0898-88880133', '24小时', 60, 1);

-- 服务区20: G25长深高速湖州南浔服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status) VALUES
(20, '南浔特色菜馆', 'RESTAURANT', '湖州南浔古镇特色', '湖州南浔服务区A区', '0572-88880141', '08:00-21:00', 60, 1),
(20, '南浔酒店', 'HOTEL', '水乡度假', '湖州南浔服务区B区', '0572-88880142', '24小时', 50, 1),
(20, '南浔充电站', 'GENERAL', '国家电网充电', '湖州南浔服务区C区', '0572-88880143', '24小时', 10, 1),
(20, '南浔便利店', 'GENERAL', '便利服务', '湖州南浔服务区D区', '0572-88880144', '24小时', 70, 1);

-- 服务区21: G25长深高速杭州余杭服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status) VALUES
(21, '余杭农家菜', 'RESTAURANT', '杭州余杭农家风味', '杭州余杭服务区A区', '0571-88880151', '08:00-21:00', 65, 1),
(21, '余杭酒店', 'HOTEL', '商务舒适', '杭州余杭服务区B区', '0571-88880152', '24小时', 55, 1),
(21, '余杭便利店', 'GENERAL', '24小时便利', '杭州余杭服务区C区', '0571-88880153', '24小时', 75, 1);

-- 服务区22: G60沪杭高速嘉兴秀洲服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status) VALUES
(22, '秀洲粽子店', 'RESTAURANT', '嘉兴五芳斋粽子', '嘉兴秀洲服务区A区', '0573-88880161', '07:00-21:00', 50, 1),
(22, '秀洲酒店', 'HOTEL', '商务型', '嘉兴秀洲服务区B区', '0573-88880162', '24小时', 45, 1),
(22, '秀洲充电站', 'GENERAL', '新能源充电', '嘉兴秀洲服务区C区', '0573-88880163', '24小时', 8, 1);

-- 服务区23: G60沪杭高速嘉兴海宁服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status) VALUES
(23, '海宁钱塘鱼馆', 'RESTAURANT', '钱塘江鲜特色', '嘉兴海宁服务区A区', '0573-88880171', '08:00-21:00', 55, 1),
(23, '海宁酒店', 'HOTEL', '标准商务', '嘉兴海宁服务区B区', '0573-88880172', '24小时', 50, 1),
(23, '海宁便利店', 'GENERAL', '连锁便利', '嘉兴海宁服务区C区', '0573-88880173', '24小时', 60, 1),
(23, '海宁中石油加油站', 'GENERAL', '加油服务', '嘉兴海宁服务区D区', '0573-88880174', '24小时', 10, 1);

-- 服务区24: G2京沪高速无锡江阴服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status) VALUES
(24, '江阴河豚鱼馆', 'RESTAURANT', '长江三鲜特色', '无锡江阴服务区A区', '0510-88880181', '09:00-20:00', 50, 1),
(24, '江阴国际酒店', 'HOTEL', '五星级标准', '无锡江阴服务区B区', '0510-88880182', '24小时', 70, 1),
(24, '江阴便利店', 'GENERAL', '24小时便利', '无锡江阴服务区C区', '0510-88880183', '24小时', 65, 1);

-- 服务区25: G2京沪高速南京栖霞服务区
INSERT INTO merchant (service_area_id, name, type, description, address, contact_phone, business_hours, capacity, status) VALUES
(25, '栖霞盐水鸭', 'RESTAURANT', '南京特色盐水鸭', '南京栖霞服务区A区', '025-88880191', '08:00-21:00', 60, 1),
(25, '栖霞酒店', 'HOTEL', '商务舒适', '南京栖霞服务区B区', '025-88880192', '24小时', 55, 1),
(25, '栖霞便利店', 'GENERAL', '连锁便利', '南京栖霞服务区C区', '025-88880193', '24小时', 70, 1),
(25, '栖霞充电站', 'GENERAL', '新能源快充', '南京栖霞服务区D区', '025-88880194', '24小时', 10, 1);
