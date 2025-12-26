package com.fpoly.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "nguoi_dung")
@Getter @Setter
public class NguoiDung {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten_dang_nhap", nullable = false, unique = true)
    private String tenDangNhap;

    @Column(name = "mat_khau", nullable = false)
    private String matKhau;

    @Column(name = "vai_tro", nullable = false)
    private String vaiTro; // USER / ADMIN

    @Column(name = "trang_thai")
    private String trangThai; // HOAT_DONG / KHOA
}
