package com.fpoly.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "nap_tien")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class NapTien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @Column(name = "so_tien_thuc", nullable = false)
    private BigDecimal soTienThuc;

    @Column(name = "so_token_nhan", nullable = false)
    private Long soTokenNhan;
    @Column(name = "phuong_thuc", nullable = false)
    private String phuongThuc;
    @Column(name = "trang_thai", nullable = false)
    private String trangThai; 

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() { this.ngayTao = LocalDateTime.now(); }
}