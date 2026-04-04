package com.fpoly.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "banner")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vi_tri")
    private String viTri; 

    @Column(name = "anh_banner")
    private String anhBanner;

    @Column(name = "token_moi_ngay", nullable = false)
    private Long tokenMoiNgay;

    @Column(name = "trang_thai")
    private String trangThai;
    
    @Column(name = "ngay_bat_dau")
    private LocalDateTime ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDateTime ngayKetThuc;
    
    @Column(name = "gio_tao")
    private LocalTime gioTao;
    
    @Column(name = "thoi_gian_con_lai")
    private Long thoiGianConLai;
    
    @Column(name = "token_da_dung")
    private Long tokenDaDung = 0L;
    
    @Column(name = "last_update_time")
    private LocalDateTime lastUpdateTime;
    
    @Column(name = "so_ngay_chay")
    private Long soNgayChay;
       
    @ManyToOne
    @JoinColumn(name = "truyen_id")
    private Truyen truyen;
}
