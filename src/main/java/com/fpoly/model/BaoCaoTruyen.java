package com.fpoly.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bao_cao_truyen")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class BaoCaoTruyen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_bao_cao_id", nullable = false)
    private NguoiDung nguoiBaoCao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "truyen_bi_bao_cao_id", nullable = false)
    private Truyen truyenBiBaoCao;

    @Column(name = "ly_do", nullable = false)
    private String lyDo;

    @Column(name = "mo_ta_chi_tiet", columnDefinition = "NVARCHAR(MAX)")
    private String moTaChiTiet;

    @Column(name = "trang_thai")
    private String trangThai; 

    @Column(name = "ngay_bao_cao")
    private LocalDateTime ngayBaoCao;

    @PrePersist
    protected void onCreate() {
        this.ngayBaoCao = LocalDateTime.now();
        if (this.trangThai == null) {
            this.trangThai = "CHO_XU_LY";
        }
    }
}