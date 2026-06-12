package com.example.servicearea.entity;

public enum MerchantStatus {
    PENDING(0, "待审核"),
    ACTIVE(1, "营业中"),
    SUSPENDED(2, "暂停营业"),
    CLOSED(3, "已关闭");

    private final int code;
    private final String description;

    MerchantStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static MerchantStatus fromCode(int code) {
        for (MerchantStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown merchant status code: " + code);
    }

    public static boolean canTransition(MerchantStatus from, MerchantStatus to) {
        switch (from) {
            case PENDING:
                return to == ACTIVE || to == CLOSED;
            case ACTIVE:
                return to == SUSPENDED || to == CLOSED;
            case SUSPENDED:
                return to == ACTIVE || to == CLOSED;
            case CLOSED:
                return false;
            default:
                return false;
        }
    }
}