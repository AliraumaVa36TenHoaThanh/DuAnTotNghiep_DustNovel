package com.fpoly.repository;

import com.fpoly.model.LichSuNhapMa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LichSuNhapMaRepository extends JpaRepository<LichSuNhapMa, Long> {
    boolean existsByNguoiDung_IdAndMaThuong_Id(Long userId, Long maId);
}
