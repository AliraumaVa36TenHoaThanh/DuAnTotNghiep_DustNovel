package com.fpoly.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import com.fpoly.model.enums.LoaiTruyen;
import com.fpoly.model.enums.TrangThaiTruyen;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "truyen")
@Getter @Setter
public class Truyen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Tên truyện không được để trống")
    @Column(name = "ten_truyen", nullable = false)
    private String tenTruyen;
    
    @NotBlank(message = "Mô tả không được để trống")
    @Size(min = 3, message = "Mô tả phải từ 3 ký tự trở lên")
    @Column(name = "mo_ta", columnDefinition = "NVARCHAR(MAX)")
    private String moTa;

    @Column(name = "ten_tac_gia")
    private String tenTacGia;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_truyen")
    private LoaiTruyen loaiTruyen;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThaiTruyen trangThai;

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
    private List<TheLoai> theLoais = new ArrayList<>();
    
    @OneToMany(mappedBy = "truyen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tap> danhSachTap;
    @OneToMany(mappedBy = "truyen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikeTruyen> danhSachLike = new ArrayList<>();
    @OneToMany(mappedBy = "truyen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BinhLuan> danhSachBinhLuan = new ArrayList<>();
  
    @Column(name = "tong_like", columnDefinition = "bigint default 0")
    private Long tongLike = 0L;

    @Column(name = "luot_xem", nullable = false)
    private Long luotXem = 0L;

    @PrePersist
    protected void onCreate() {
        this.ngayTao = LocalDateTime.now();
        if (this.tongLike == null) this.tongLike = 0L;
        if (this.luotXem == null) this.luotXem = 0L;
    }

    public Long getTongLike() {
        return tongLike == null ? 0 : tongLike;
    }

    public Long getLuotXem() {
        return luotXem == null ? 0 : luotXem;
    }
}