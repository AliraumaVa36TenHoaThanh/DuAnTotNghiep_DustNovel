package com.fpoly.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "chuong")
@Getter @Setter
public class Chuong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tieu_de")
    private String tieuDe;

    @Column(name = "noi_dung", columnDefinition = "TEXT")
    private String noiDung;

    @Column(name = "so_chuong")
    private Integer soChuong;

    @ManyToOne
    @JoinColumn(name = "truyen_id", nullable = false)
    private Truyen truyen;

    @ManyToOne
    @JoinColumn(name = "nguoi_dang_id", nullable = false)
    private NguoiDung nguoiDang;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;
    
    private Boolean khoa = false;

    @Column(name = "gia_token")
    private Long giaToken = 0L;
}