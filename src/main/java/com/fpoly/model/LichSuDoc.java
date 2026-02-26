package com.fpoly.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lich_su_doc", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"nguoi_dung_id", "chuong_id"})
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class LichSuDoc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "truyen_id", nullable = false)
    private Truyen truyen;

    @ManyToOne
    @JoinColumn(name = "chuong_id", nullable = false)
    private Chuong chuong;
    @Column(name = "lan_doc_cuoi")
    private LocalDateTime lanDocCuoi;

    @PreUpdate @PrePersist
    protected void onUpdate() { this.lanDocCuoi = LocalDateTime.now(); }
}