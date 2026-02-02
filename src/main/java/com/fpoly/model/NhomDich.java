package com.fpoly.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "nhom_dich")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NhomDich {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten_nhom", nullable = false, length = 255)
    private String tenNhom;

    @Column(name = "mo_ta", columnDefinition = "NVARCHAR(MAX)")
    private String moTa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "truong_nhom_id", nullable = false)
    private NguoiDung truongNhom;

    @Column(name = "trang_thai", length = 30)
    private String trangThai; // HOAT_DONG / KHOA

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        if (this.trangThai == null) {
            this.trangThai = "HOAT_DONG";
        }
        this.ngayTao = LocalDateTime.now();
    }
}
