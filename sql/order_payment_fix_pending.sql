-- 修复：历史「待确认」订单被误标为已支付，导致选「稍后支付」仍显示已支付
-- 仅将仍为 PENDING 且未真实走过支付流程的订单改回待支付

UPDATE `order`
SET pay_status = 'UNPAID',
    pay_time = NULL,
    pay_method = NULL
WHERE status = 'PENDING'
  AND (pay_method IS NULL OR pay_method = 'LEGACY');
