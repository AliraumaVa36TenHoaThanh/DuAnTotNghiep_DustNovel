package com.fpoly.model.enums;

public enum TrangThaiTruyen {
    DANG_TIEN_HANH("Đang tiến hành"),
    KET_THUC("Kết thúc"),
    ĐANG_RA("Đang ra"), 
    HOÀN_THÀNH("Hoàn thành"); 

    private final String label;

    TrangThaiTruyen(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}