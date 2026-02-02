package com.fpoly.repository;

import com.fpoly.model.LikeTruyen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeTruyenRepository extends JpaRepository<LikeTruyen, Long> {
    boolean existsByNguoiDungIdAndTruyenId(Long userId, Long truyenId);
}