package com.fpoly.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ma_thuong")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class MaThuong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "so_phieu_thuong", nullable = false)
    private Long soPhieuThuong;

    @Column(name = "so_luong_nhap")
    private Integer soLuongNhap; // Tổng số lượt có thể sử dụng mã này

    @Column(name = "da_nhap")
    private Integer daNhap = 0; // Số lượt thực tế đã được sử dụng

    @Column(name = "ngay_het_han")
    private LocalDateTime ngayHetHan;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        this.ngayTao = LocalDateTime.now();
        if (this.daNhap == null) this.daNhap = 0;
    }
}