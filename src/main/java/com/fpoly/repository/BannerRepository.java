package com.fpoly.repository;

import com.fpoly.model.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
            LocalDate ngayHienTai1,
            LocalDate ngayHienTai2
    );

}
