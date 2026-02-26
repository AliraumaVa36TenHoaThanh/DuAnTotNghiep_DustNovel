package com.fpoly.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "loi_moi_nhom_dich",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nhom_dich_id", "nguoi_duoc_moi_id"})
    }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class LoiMoiNhomDich {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "nhom_dich_id", nullable = false)
    private NhomDich nhomDich;

    @ManyToOne
    @JoinColumn(name = "nguoi_moi_id", nullable = false)
    private NguoiDung nguoiMoi;

    @ManyToOne
    @JoinColumn(name = "nguoi_duoc_moi_id", nullable = false)
    private NguoiDung nguoiDuocMoi;
    @Column(name = "trang_thai", nullable = false)
    private String trangThai;
    @Column(name = "ngay_moi", nullable = false)
    private LocalDateTime ngayMoi;

    @PrePersist
    protected void onCreate() { this.ngayMoi = LocalDateTime.now(); }
}