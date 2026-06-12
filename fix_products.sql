-- 修复商品类型，保留大饼卷肉，其他商品按正确分类重新添加

-- 先保存大饼卷肉的信息
-- id=61, merchant_id=77, name='大饼卷肉', type='RESTAURANT', description='传统大饼卷肉，美味可口', price=10.00, stock=64, status=1

-- 删除其他AI添加的商品（id>61的商品）
DELETE FROM product WHERE id > 20;

-- 重新添加正确分类的商品

-- 服务区1: G2京沪高速无锡服务区
-- 餐饮类商品 (merchant 11, 13)
INSERT INTO product (id, merchant_id, name, type, description, price, stock, status) VALUES
(21, 11, '蛋炒饭', 'RESTAURANT', '粒粒分明，蛋香浓郁', 18.00, 100, 1),
(22, 11, '酸辣土豆丝', 'RESTAURANT', '酸辣爽口', 12.00, 50, 1),
(23, 11, '番茄炒蛋', 'RESTAURANT', '经典家常菜', 16.00, 50, 1),
(27, 13, '拿铁咖啡', 'RESTAURANT', '香浓拿铁', 28.00, 50, 1),
(28, 13, '提拉米苏', 'RESTAURANT', '意大利甜点', 32.00, 30, 1);

-- 通用类商品 (merchant 12, 14)
INSERT INTO product (id, merchant_id, name, type, description, price, stock, status) VALUES
(24, 12, '矿泉水550ml', 'GENERAL', '瓶装矿泉水', 2.00, 500, 1),
(25, 12, '可乐500ml', 'GENERAL', '碳酸饮料', 3.50, 300, 1),
(26, 12, '方便面', 'GENERAL', '速食泡面', 4.50, 200, 1),
(29, 14, '无锡酱排骨礼盒', 'GENERAL', '特色礼盒装', 128.00, 50, 1),
(30, 14, '太湖银鱼干', 'GENERAL', '太湖特产', 68.00, 80, 1);

-- 服务区2: G42沪宁高速苏州服务区
-- 餐饮类商品 (merchant 16, 18, 19)
INSERT INTO product (id, merchant_id, name, type, description, price, stock, status) VALUES
(31, 16, '松鼠鳜鱼', 'RESTAURANT', '苏州名菜', 128.00, 20, 1),
(32, 16, '响油鳝糊', 'RESTAURANT', '鲜嫩爽滑', 68.00, 30, 1),
(33, 16, '清炒虾仁', 'RESTAURANT', '鲜嫩可口', 58.00, 40, 1),
(36, 18, '苏式汤面', 'RESTAURANT', '正宗苏式面', 28.00, 50, 1),
(37, 18, '焖肉面', 'RESTAURANT', '经典焖肉', 32.00, 40, 1),
(38, 19, '碧螺春', 'RESTAURANT', '苏州名茶', 188.00, 20, 1),
(39, 19, '蟹粉小笼', 'RESTAURANT', '鲜美多汁', 38.00, 60, 1);

-- 通用类商品 (merchant 17, 20)
INSERT INTO product (id, merchant_id, name, type, description, price, stock, status) VALUES
(34, 17, '矿泉水550ml', 'GENERAL', '瓶装水', 2.00, 400, 1),
(35, 17, '零食大礼包', 'GENERAL', '各类零食', 45.00, 100, 1),
(40, 20, '苏绣围巾', 'GENERAL', '精美刺绣', 168.00, 30, 1);

-- 服务区3: G60沪杭高速嘉兴服务区
-- 餐饮类商品 (merchant 22, 24, 25)
INSERT INTO product (id, merchant_id, name, type, description, price, stock, status) VALUES
(41, 22, '西湖醋鱼', 'RESTAURANT', '酸甜适口', 58.00, 45, 1),
(42, 22, '东坡肉', 'RESTAURANT', '肥而不腻', 42.00, 50, 1),
(43, 22, '龙井虾仁', 'RESTAURANT', '茶香虾仁', 78.00, 35, 1),
(46, 24, '鲜肉粽', 'RESTAURANT', '嘉兴肉粽', 12.00, 100, 1),
(47, 24, '蛋黄粽', 'RESTAURANT', '蛋黄肉粽', 15.00, 80, 1),
(48, 25, '定胜糕', 'RESTAURANT', '江南传统糕点', 18.00, 60, 1),
(49, 25, '条头糕', 'RESTAURANT', '软糯香甜', 12.00, 80, 1);

-- 通用类商品 (merchant 23, 26)
INSERT INTO product (id, merchant_id, name, type, description, price, stock, status) VALUES
(44, 23, '饮料组合', 'GENERAL', '多种饮料', 25.00, 150, 1),
(45, 23, '方便面组合', 'GENERAL', '多种口味', 30.00, 100, 1),
(50, 26, '嘉兴蜜梨', 'GENERAL', '新鲜蜜梨', 15.00, 100, 1);

-- 服务区4: G15沈海高速常熟服务区
-- 餐饮类商品 (merchant 27, 29, 31)
INSERT INTO product (id, merchant_id, name, type, description, price, stock, status) VALUES
(51, 27, '叫化鸡', 'RESTAURANT', '传统工艺', 98.00, 25, 1),
(52, 27, '鸭血粉丝汤', 'RESTAURANT', '南京特色', 28.00, 60, 1),
(53, 27, '盐水鸭', 'RESTAURANT', '南京名菜', 48.00, 40, 1),
(56, 29, '奥灶面', 'RESTAURANT', '昆山特色', 35.00, 40, 1),
(57, 29, '爆鱼面', 'RESTAURANT', '鱼香浓郁', 32.00, 45, 1),
(60, 31, '美式咖啡', 'RESTAURANT', '经典美式', 22.00, 60, 1);

-- 通用类商品 (merchant 28, 30)
INSERT INTO product (id, merchant_id, name, type, description, price, stock, status) VALUES
(54, 28, '零食小吃', 'GENERAL', '各类零食', 20.00, 200, 1),
(55, 28, '矿泉水', 'GENERAL', '瓶装水', 2.00, 500, 1),
(58, 30, '桂花酒', 'GENERAL', '常熟特产', 88.00, 60, 1),
(59, 30, '鸭血', 'GENERAL', '新鲜鸭血', 15.00, 100, 1);

-- 服务区5: G25长深高速湖州服务区
-- 餐饮类商品 (merchant 32, 34, 35)
INSERT INTO product (id, merchant_id, name, type, description, price, stock, status) VALUES
(62, 32, '太湖蟹', 'RESTAURANT', '膏肥黄满', 168.00, 15, 1),
(63, 32, '太湖白鱼', 'RESTAURANT', '肉质鲜美', 88.00, 30, 1),
(64, 32, '银鱼炒蛋', 'RESTAURANT', '鲜香嫩滑', 48.00, 50, 1),
(66, 34, '鱼头汤', 'RESTAURANT', '鲜美鱼汤', 68.00, 30, 1),
(67, 34, '清蒸鲈鱼', 'RESTAURANT', '鲜嫩可口', 58.00, 40, 1),
(68, 35, '安吉白茶', 'RESTAURANT', '名茶', 168.00, 20, 1),
(69, 35, '茶点', 'RESTAURANT', '精美茶点', 38.00, 50, 1);

-- 通用类商品 (merchant 33, 36)
INSERT INTO product (id, merchant_id, name, type, description, price, stock, status) VALUES
(65, 33, '饮料', 'GENERAL', '各类饮料', 3.00, 400, 1),
(70, 36, '湖笔', 'GENERAL', '文房四宝', 88.00, 30, 1);

-- 服务区6: S58沪常高速淀山湖服务区
-- 餐饮类商品 (merchant 37, 39, 40)
INSERT INTO product (id, merchant_id, name, type, description, price, stock, status) VALUES
(71, 37, '红烧肉', 'RESTAURANT', '本帮菜', 48.00, 40, 1),
(72, 37, '油爆虾', 'RESTAURANT', '上海名菜', 68.00, 35, 1),
(73, 37, '响油鳝糊', 'RESTAURANT', '鲜嫩爽滑', 58.00, 30, 1),
(76, 39, '生煎包', 'RESTAURANT', '皮薄汁多', 28.00, 100, 1),
(77, 39, '小笼包', 'RESTAURANT', '南翔小笼', 32.00, 80, 1),
(78, 40, '马卡龙', 'RESTAURANT', '法式甜点', 38.00, 40, 1),
(79, 40, '芝士蛋糕', 'RESTAURANT', '香浓可口', 48.00, 30, 1);

-- 通用类商品 (merchant 38, 41, 42)
INSERT INTO product (id, merchant_id, name, type, description, price, stock, status) VALUES
(74, 38, '进口零食', 'GENERAL', '进口食品', 35.00, 150, 1),
(75, 38, '矿泉水', 'GENERAL', '瓶装水', 2.00, 500, 1),
(80, 41, '大白兔奶糖', 'GENERAL', '上海特产', 28.00, 100, 1);

-- 服务区7: G2京沪高速江阴服务区
-- 餐饮类商品 (merchant 43, 45, 47)
INSERT INTO product (id, merchant_id, name, type, description, price, stock, status) VALUES
(81, 43, '刀鱼馄饨', 'RESTAURANT', '鲜美无比', 48.00, 40, 1),
(82, 43, '河豚', 'RESTAURANT', '江阴特色', 198.00, 10, 1),
(83, 43, '清蒸刀鱼', 'RESTAURANT', '春季美味', 128.00, 20, 1),
(86, 45, '长鱼面', 'RESTAURANT', '特色面', 38.00, 40, 1),
(87, 45, '腰花面', 'RESTAURANT', '鲜美可口', 35.00, 45, 1),
(90, 47, '汉堡套餐', 'RESTAURANT', '快餐', 25.00, 80, 1);

-- 通用类商品 (merchant 44, 46)
INSERT INTO product (id, merchant_id, name, type, description, price, stock, status) VALUES
(84, 44, '方便面', 'GENERAL', '速食', 4.50, 200, 1),
(85, 44, '饮料', 'GENERAL', '各类饮料', 3.50, 300, 1),
(88, 46, '马蹄酥', 'GENERAL', '江阴特产', 38.00, 60, 1),
(89, 46, '黑杜酒', 'GENERAL', '江阴老酒', 128.00, 40, 1);

-- 服务区8: G60沪杭高速长安服务区
-- 餐饮类商品 (merchant 48, 50, 51, 53)
INSERT INTO product (id, merchant_id, name, type, description, price, stock, status) VALUES
(91, 48, '片儿川', 'RESTAURANT', '杭州特色面', 28.00, 60, 1),
(92, 48, '宋嫂鱼羹', 'RESTAURANT', '酸辣鲜香', 38.00, 50, 1),
(93, 48, '龙井虾仁', 'RESTAURANT', '茶香鲜嫩', 78.00, 35, 1),
(96, 50, '葱包烩', 'RESTAURANT', '杭州小吃', 12.00, 80, 1),
(97, 50, '定胜糕', 'RESTAURANT', '传统糕点', 18.00, 60, 1),
(98, 51, '西湖龙井', 'RESTAURANT', '名茶', 288.00, 15, 1),
(99, 51, '茶点', 'RESTAURANT', '精美茶点', 32.00, 50, 1);

-- 通用类商品 (merchant 49, 52)
INSERT INTO product (id, merchant_id, name, type, description, price, stock, status) VALUES
(94, 49, '零食', 'GENERAL', '各类零食', 20.00, 200, 1),
(95, 49, '矿泉水', 'GENERAL', '瓶装水', 2.00, 400, 1),
(100, 52, '杨梅', 'GENERAL', '新鲜杨梅', 35.00, 80, 1);

-- 服务区9: S26沪常高速嘉善服务区
-- 餐饮类商品 (merchant 54, 56, 57)
INSERT INTO product (id, merchant_id, name, type, description, price, stock, status) VALUES
(101, 54, '嘉善酱鸭', 'RESTAURANT', '特色酱鸭', 58.00, 40, 1),
(102, 54, '炒螺蛳', 'RESTAURANT', '鲜美可口', 38.00, 50, 1),
(103, 54, '清蒸鳜鱼', 'RESTAURANT', '鲜嫩爽滑', 98.00, 25, 1),
(106, 56, '鲜肉粽', 'RESTAURANT', '嘉兴肉粽', 12.00, 100, 1),
(107, 56, '豆沙粽', 'RESTAURANT', '甜粽', 10.00, 80, 1),
(108, 57, '芋圆', 'RESTAURANT', '甜品', 28.00, 50, 1),
(109, 57, '双皮奶', 'RESTAURANT', '奶香浓郁', 22.00, 60, 1);

-- 通用类商品 (merchant 55, 58)
INSERT INTO product (id, merchant_id, name, type, description, price, stock, status) VALUES
(104, 55, '饮料', 'GENERAL', '各类饮料', 3.00, 350, 1),
(105, 55, '零食', 'GENERAL', '各类零食', 18.00, 200, 1),
(110, 58, '西塘芡实糕', 'GENERAL', '古镇特产', 28.00, 80, 1);

-- 服务区10: G15沈海高速太仓服务区
-- 餐饮类商品 (merchant 59, 61, 62, 64)
INSERT INTO product (id, merchant_id, name, type, description, price, stock, status) VALUES
(111, 59, '太仓肉松', 'RESTAURANT', '特色肉松', 48.00, 50, 1),
(112, 59, '糟油鸡', 'RESTAURANT', '太仓特色', 58.00, 40, 1),
(113, 59, '红烧羊肉', 'RESTAURANT', '鲜美羊肉', 68.00, 35, 1),
(116, 61, '奥灶面', 'RESTAURANT', '昆山特色', 35.00, 40, 1),
(117, 61, '大排面', 'RESTAURANT', '经典大排', 32.00, 45, 1),
(118, 62, '清蒸虾', 'RESTAURANT', '新鲜大虾', 88.00, 30, 1),
(119, 62, '炒花蛤', 'RESTAURANT', '鲜美花蛤', 48.00, 40, 1),
(120, 64, '拿铁咖啡', 'RESTAURANT', '香浓拿铁', 28.00, 50, 1);

-- 通用类商品 (merchant 60, 63)
INSERT INTO product (id, merchant_id, name, type, description, price, stock, status) VALUES
(114, 60, '矿泉水', 'GENERAL', '瓶装水', 2.00, 450, 1),
(115, 60, '零食', 'GENERAL', '各类零食', 25.00, 180, 1),
(121, 63, '太仓糟油', 'GENERAL', '特产调味', 38.00, 60, 1);

-- 重置自增ID
ALTER TABLE product AUTO_INCREMENT = 122;
