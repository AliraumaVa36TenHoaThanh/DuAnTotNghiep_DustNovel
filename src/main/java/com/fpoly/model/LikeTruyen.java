package com.fpoly.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "like_truyen",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nguoi_dung_id", "truyen_id"})
    }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class LikeTruyen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "truyen_id", nullable = false)
    private Truyen truyen;

    @Column(name = "ngay_like")
    private LocalDateTime ngayLike;

    @PrePersist
    protected void onCreate() {
        this.ngayLike = LocalDateTime.now();
    }
}