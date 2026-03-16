package com.fpoly.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_tom_tat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiTomTat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ 1-1 với bảng Chương (Vì Database ông set UNIQUE chuong_id)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chuong_id", nullable = false, unique = true)
    private Chuong chuong;

    @Column(name = "noi_dung_tom_tat", columnDefinition = "NVARCHAR(MAX)")
    private String noiDungTomTat;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao = LocalDateTime.now();
}