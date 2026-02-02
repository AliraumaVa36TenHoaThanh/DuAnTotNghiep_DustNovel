package com.fpoly.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "thanh_vien_nhom_dich",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nhom_dich_id", "nguoi_dung_id"})
    }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ThanhVienNhomDich {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "nhom_dich_id", nullable = false)
    private NhomDich nhomDich;

    @ManyToOne
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @Column(name = "vai_tro", length = 30)
    private String vaiTro; // TRUONG_NHOM / THANH_VIEN / CONG_TAC_VIEN

    @Column(name = "trang_thai", length = 30)
    private String trangThai; // CHO_DUYET / DA_DUYET / TU_CHOI / ROI_NHOM

    @Column(name = "ngay_tham_gia")
    private LocalDateTime ngayThamGia;

    @PrePersist
    protected void onCreate() {
        this.ngayThamGia = LocalDateTime.now();
        if (this.vaiTro == null) this.vaiTro = "THANH_VIEN";
        if (this.trangThai == null) this.trangThai = "CHO_DUYET";
    }
}