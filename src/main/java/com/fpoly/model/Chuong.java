package com.fpoly.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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
    @JoinColumn(name = "tap_id", nullable = false)
    private Tap tap;
    
    @ManyToOne
    @JoinColumn(name = "truyen_id", nullable = false)
    private Truyen truyen;

    
    @ManyToOne
    @JoinColumn(name = "nguoi_dang_id", nullable = false)
    private NguoiDung nguoiDang;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;
    @PrePersist
    protected void onCreate() {
        this.ngayTao = LocalDateTime.now();
    }
    
    @Column(name = "khoa", columnDefinition = "BIT")
    private boolean khoa;

    @Column(name = "gia_token")
    private Long giaToken = 0L;
    
    @Transient
    public boolean isNewChuong() {
        return ngayTao != null &&
               ngayTao.isAfter(LocalDateTime.now().minusHours(24));
    }
    @OneToMany(mappedBy = "chuong", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MoKhoaChuong> danhSachMoKhoa;
    
}