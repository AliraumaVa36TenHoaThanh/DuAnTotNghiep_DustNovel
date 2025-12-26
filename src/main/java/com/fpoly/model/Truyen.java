package com.fpoly.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fpoly.model.enums.LoaiTruyen;
import java.time.LocalDateTime;
import java.util.List;
@Entity
@Table(name = "truyen")
@Getter @Setter
public class Truyen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten_truyen", nullable = false)
    private String tenTruyen;

    @Column(name = "mo_ta", columnDefinition = "NVARCHAR(MAX)")
    private String moTa;

    @Column(name = "ten_tac_gia")
    private String tenTacGia;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "loai_truyen")
    private LoaiTruyen loaiTruyen; // SÁNG_TÁC / DỊCH

    @Column(name = "trang_thai")
    private String trangThai; // ĐANG_RA / HOÀN_THÀNH

    @Column(name = "tag_18")
    private Boolean tag18;

    @ManyToOne
    @JoinColumn(name = "nguoi_dang_id", nullable = false)
    private NguoiDung nguoiDang;

    @Column(name = "anh_bia")
    private String anhBia;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;
    
    @ManyToMany
    @JoinTable(
        name = "truyen_the_loai",
        joinColumns = @JoinColumn(name = "truyen_id"),
        inverseJoinColumns = @JoinColumn(name = "the_loai_id")
    )
    private List<TheLoai> theLoais;
    
    
}
