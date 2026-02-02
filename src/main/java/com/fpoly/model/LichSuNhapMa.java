package com.fpoly.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "lich_su_nhap_ma",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nguoi_dung_id", "ma_thuong_id"})
    }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class LichSuNhapMa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "ma_thuong_id", nullable = false)
    private MaThuong maThuong;

    @Column(name = "ngay_nhap")
    private LocalDateTime ngayNhap;

    @PrePersist
    protected void onCreate() {
        this.ngayNhap = LocalDateTime.now();
    }
}