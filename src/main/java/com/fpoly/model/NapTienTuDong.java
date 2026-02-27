package com.fpoly.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "nap_tien_tu_dong")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class NapTienTuDong {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @Column(name = "ten_goi_nap")
    private String tenGoiNap;

    @Column(name = "so_tien_thuc", nullable = false)
    private Long soTienThuc;

    @Column(name = "so_token_nhan", nullable = false)
    private Long soTokenNhan;

    @Column(name = "noi_dung_ck", nullable = false, unique = true)
    private String noiDungCk;

    @Column(name = "ma_giao_dich_ngan_hang")
    private String maGiaoDichNganHang;

    @Column(name = "phuong_thuc")
    private String phuongThuc = "VIETQR";

    @Column(name = "trang_thai")
    private String trangThai = "PENDING"; 

    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    protected void onCreate() { 
        this.ngayTao = LocalDateTime.now(); 
    }

    @PreUpdate
    protected void onUpdate() {
        this.ngayCapNhat = LocalDateTime.now();
    }
}