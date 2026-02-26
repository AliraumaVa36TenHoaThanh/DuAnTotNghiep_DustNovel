package com.fpoly.model;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "mo_khoa_chuong",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nguoi_dung_id", "chuong_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoKhoaChuong {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
    @ManyToOne
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "chuong_id", nullable = false)
    private Chuong chuong;


    @Column(name = "gia_token", nullable = false)
    private Long giaToken;


    @Column(name = "ngay_mo")
    private LocalDateTime ngayMo;
    
    @PrePersist
    protected void onCreate() {
        this.ngayMo = LocalDateTime.now();
    }
}
