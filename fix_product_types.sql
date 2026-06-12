-- 修复商品类型，将详细类型合并为三大分类
-- 餐饮类：主食、凉菜、热菜、汤品、饮品等
-- 通用类：饮料、食品、特产等

-- 更新商品类型为餐饮类
UPDATE product SET type = 'RESTAURANT' WHERE type IN ('主食', '凉菜', '热菜', '汤品', '饮品', '点心', '面食', '海鲜', '肉类');

-- 更新商品类型为通用类
UPDATE product SET type = 'GENERAL' WHERE type IN ('饮料', '食品', '特产', '礼品', '日用品', '零食');

-- 确保所有商品都有正确的类型
UPDATE product SET type = 'RESTAURANT' WHERE type NOT IN ('RESTAURANT', 'HOTEL', 'GENERAL');

SELECT type, COUNT(*) as count FROM product GROUP BY type;
