package com.fpoly.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "giao_dich_token")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class GiaoDichToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @Column(name = "loai_giao_dich", length = 50, nullable = false)
    private String loaiGiaoDich;

    // + hoặc -
    @Column(name = "so_token", nullable = false)
    private Long soToken;

    @Column(name = "so_du_sau", nullable = false)
    private Long soDuSau;

    @Column(name = "mo_ta", length = 255)
    private String moTa;
    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() { this.ngayTao = LocalDateTime.now(); }
}