package com.fpoly.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

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
    @JoinColumn(name = "truyen_id", nullable = true)
    private Truyen truyen;

    @ManyToOne
    @JoinColumn(name = "chuong_id", nullable = true)
    private Chuong chuong;
    
    @Column(name = "noi_dung", columnDefinition = "NVARCHAR(MAX)", nullable = false)
    private String noiDung;

    @Column(name = "ngay_binh_luan")
    private LocalDateTime ngayBinhLuan;
    
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private BinhLuan parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("ngayBinhLuan ASC")
    private List<BinhLuan> replies;

    @PrePersist
    protected void onCreate() {
        this.ngayBinhLuan = LocalDateTime.now();
    }
} 