package com.fpoly.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import com.fpoly.model.enums.StatusTheLoai;
@Entity
@Table(name = "the_loai")
@Getter @Setter
public class TheLoai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten_the_loai", nullable = false)
    private String tenTheLoai;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status_the_loai")
    private StatusTheLoai statusTheLoai;


    
    @ManyToMany(mappedBy = "theLoais")
    private List<Truyen> truyenList;
    
    
}
