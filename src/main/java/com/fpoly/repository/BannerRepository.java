package com.fpoly.repository;

import com.fpoly.model.Banner;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

    List<Banner> findByViTri(String viTri);

    List<Banner> findByTrangThai(String trangThai);
    
    List<Banner> findByViTriAndTrangThai(String viTri, String trangThai);
    
    List<Banner> findByTruyenIdIn(List<Long> truyenIds);
    
    List<Banner> findByViTriAndTrangThaiAndNgayBatDauLessThanEqualAndNgayKetThucGreaterThanEqualOrderByTokenMoiNgayDesc(
            String viTri,
            String trangThai,
            LocalDateTime ngayHienTai1,
            LocalDateTime ngayHienTai2
    );
    
    List<Banner> findByTrangThaiAndViTri(String trangThai, String viTri);
    
    List<Banner> findByTruyenIdInAndTruyen_TenTruyenContainingIgnoreCase(
            List<Long> ids, String keyword);
    
    boolean existsByTruyenId(Long truyenId);
    
    @Modifying
    @Transactional
    void deleteByTruyenId(Long truyenId);
    
    @Query("SELECT b FROM Banner b WHERE b.truyen.id = :truyenId")
    List<Banner> findByTruyenId(@Param("truyenId") Long truyenId);
    
    boolean existsByTruyen_IdAndViTriAndTrangThaiIn(
    	    Long truyenId,
    	    String viTri,
    	    List<String> trangThai
    	);

}
