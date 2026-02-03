package com.fpoly.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "tap")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Tap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "truyen_id", nullable = false)
    private Truyen truyen;

    @Column(name = "ten_tap", nullable = false)
    private String tenTap; 

    @Column(name = "so_tap")
    private Integer soTap; 
    
    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;
    
    @PrePersist
    public void prePersist() {
        this.ngayTao = LocalDateTime.now();
    }
    
   
}
