package com.fpoly.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "binh_luan")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class BinhLuan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "truyen_id", nullable = false)
    private Truyen truyen;

    @Column(name = "noi_dung", columnDefinition = "NVARCHAR(MAX)", nullable = false)
    private String noiDung;

    @Column(name = "ngay_binh_luan")
    private LocalDateTime ngayBinhLuan;

    @PrePersist
    protected void onCreate() {
        this.ngayBinhLuan = LocalDateTime.now();
    }
}