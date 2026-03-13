package com.fpoly.model;

import java.time.LocalDate;

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

	/*
	 * @Column(name = "truyen_id", nullable = false) private Long truyenId;
	 */

    @Column(name = "vi_tri")
    private String viTri; 

    @Column(name = "anh_banner")
    private String anhBanner;

    @Column(name = "token_moi_ngay", nullable = false)
    private Long tokenMoiNgay;

    @Column(name = "trang_thai")
    private String trangThai;
    
    @Column(name = "ngay_bat_dau")
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDate ngayKetThuc;
    
    @ManyToOne
    @JoinColumn(name = "truyen_id")
    private Truyen truyen;
}
