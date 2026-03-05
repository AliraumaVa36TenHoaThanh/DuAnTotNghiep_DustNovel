package com.fpoly.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookmark")
@Getter
@Setter
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id")
    private NguoiDung nguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chuong_id")
    private Chuong chuong;

    @Column(name = "vi_tri_ky_tu")
    private Integer viTriKyTu;

    @Column(name = "phan_tram_doc")
    private Float phanTramDoc;

    @Column(name = "doan_text")
    private String doanText;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;
    
    @PrePersist
    public void prePersist() {
        this.ngayCapNhat = LocalDateTime.now();
    }
}