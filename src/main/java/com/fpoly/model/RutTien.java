package com.fpoly.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rut_tien")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RutTien {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @Column(name = "so_token", nullable = false)
    private Long soToken;

    @Column(name = "thue", nullable = false)
    private Long thue; // 10%

    @Column(name = "token_thuc_nhan", nullable = false)
    private Long tokenThucNhan;
    
    @Column(name = "ngan_hang", length = 100, nullable = false)
    private String nganHang;

    @Column(name = "so_tai_khoan", length = 50, nullable = false)
    private String soTaiKhoan;

    @Column(name = "ten_chu_tai_khoan", length = 150, nullable = false)
    private String tenChuTaiKhoan;

    @Column(name = "trang_thai", length = 30)
    private String trangThai; // CHO_DUYET / DA_TRA / TU_CHOI

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        if (this.trangThai == null) {
            this.trangThai = "CHO_DUYET";
        }
        this.ngayTao = LocalDateTime.now();
    }
}
