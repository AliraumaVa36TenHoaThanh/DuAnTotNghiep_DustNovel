package com.fpoly.model.enums;

public enum StatusMaThuong {

    ON("HOẠT ĐỘNG"),
    OFF("ĐÃ TẮT");

    private final String displayName;

    StatusMaThuong(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isActive() {
        return this == ON;
    }

    public StatusMaThuong toggle() {
        return this == ON ? OFF : ON;
    }
}