package com.example.servicearea.support;

/**
 * 防止订单与预约状态双向同步时递归调用
 */
public final class OrderReservationSyncContext {

    private static final ThreadLocal<Boolean> SYNCING = ThreadLocal.withInitial(() -> false);

    private OrderReservationSyncContext() {
    }

    public static boolean isSyncing() {
        return Boolean.TRUE.equals(SYNCING.get());
    }

    public static void run(Runnable action) {
        if (isSyncing()) {
            action.run();
            return;
        }
        SYNCING.set(true);
        try {
            action.run();
        } finally {
            SYNCING.remove();
        }
    }
}
