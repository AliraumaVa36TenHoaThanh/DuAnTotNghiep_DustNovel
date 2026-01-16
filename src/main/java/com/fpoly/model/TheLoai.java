package com.fpoly.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
@Entity
@Table(name = "the_loai")
@Getter @Setter
public class TheLoai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten_the_loai", nullable = false)
    private String tenTheLoai;
    
    @ManyToMany(mappedBy = "theLoais")
    private List<Truyen> truyenList;
    
    
}
