package com.fpoly.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "ai_dich",
       uniqueConstraints = @UniqueConstraint(columnNames = {"chuong_id","ngon_ngu"}))
public class AiDich {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ với chương
    @ManyToOne
    @JoinColumn(name = "chuong_id", nullable = false)
    private Chuong chuong;

    // vi / en / ja / ko / zh
    @Column(name = "ngon_ngu")
    private String ngonNgu;

    @Column(name = "noi_dung_dich", columnDefinition = "NVARCHAR(MAX)")
    private String noiDungDich;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao = LocalDateTime.now();

    public AiDich() {}

    public AiDich(Chuong chuong, String ngonNgu, String noiDungDich) {
        this.chuong = chuong;
        this.ngonNgu = ngonNgu;
        this.noiDungDich = noiDungDich;
    }

    public Long getId() {
        return id;
    }

    public Chuong getChuong() {
        return chuong;
    }

    public void setChuong(Chuong chuong) {
        this.chuong = chuong;
    }

    public String getNgonNgu() {
        return ngonNgu;
    }

    public void setNgonNgu(String ngonNgu) {
        this.ngonNgu = ngonNgu;
    }

    public String getNoiDungDich() {
        return noiDungDich;
    }

    public void setNoiDungDich(String noiDungDich) {
        this.noiDungDich = noiDungDich;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }
}