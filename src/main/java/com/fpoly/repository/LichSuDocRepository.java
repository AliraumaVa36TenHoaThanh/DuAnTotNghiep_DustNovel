package com.fpoly.repository;

import com.fpoly.model.LichSuDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LichSuDocRepository extends JpaRepository<LichSuDoc, Long> {
    Optional<LichSuDoc> findByNguoiDungIdAndTruyenId(Long userId, Long truyenId);
}