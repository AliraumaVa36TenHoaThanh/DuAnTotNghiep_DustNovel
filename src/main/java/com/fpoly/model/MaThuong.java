
package com.fpoly.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.fpoly.model.enums.StatusMaThuong;
@Entity
@Table(name = "ma_thuong")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaThuong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "so_phieu_thuong", nullable = false)
    private Long soPhieuThuong;

    @Column(name = "so_luong_nhap")
    private Integer soLuongNhap; 

    @Column(name = "da_nhap")
    private Integer daNhap = 0;

    @Column(name = "ngay_het_han")
    private LocalDateTime ngayHetHan;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status_ma_thuong")
    private StatusMaThuong statusMaThuong;
    @PrePersist
        public void prePersist() {
            if (daNhap == null) daNhap = 0;
            if (ngayTao == null) ngayTao = LocalDateTime.now();
            if (soLuongNhap == null) soLuongNhap = 999999;
            if (statusMaThuong == null) statusMaThuong = StatusMaThuong.ON;
            
        }
    }