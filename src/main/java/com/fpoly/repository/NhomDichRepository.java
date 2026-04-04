package com.fpoly.repository;

import com.fpoly.model.NguoiDung;
import com.fpoly.model.NhomDich;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NhomDichRepository extends JpaRepository<NhomDich, Long> { 
    List<NhomDich> findByTrangThai(String trangThai);
    
    boolean existsByTruongNhom(NguoiDung truongNhom);
    
    List<NhomDich> findByTruongNhom(NguoiDung truongNhom);
    
    @Query("SELECT n FROM NhomDich n LEFT JOIN n.danhSachThanhVien tv " +
            "WHERE n.truongNhom.id = :userId OR tv.nguoiDung.id = :userId")
     List<NhomDich> findNhomByUserId(@org.springframework.data.repository.query.Param("userId") Long userId);
}