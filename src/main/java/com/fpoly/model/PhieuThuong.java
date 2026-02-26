package com.fpoly.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "phieu_thuong")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class PhieuThuong {
    @Id
    private Long nguoiDungId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "nguoi_dung_id")
    private NguoiDung nguoiDung;

    @Column(name = "so_luong")
    private Long soLuong = 0L;
}