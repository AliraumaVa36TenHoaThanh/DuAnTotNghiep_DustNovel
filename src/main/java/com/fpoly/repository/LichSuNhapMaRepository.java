package com.fpoly.repository;

import com.fpoly.model.LichSuNhapMa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LichSuNhapMaRepository extends JpaRepository<LichSuNhapMa, Long> {
    boolean existsByNguoiDungIdAndMaThuongId(Long userId, Long maId);
}