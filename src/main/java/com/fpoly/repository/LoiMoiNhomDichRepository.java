package com.fpoly.repository;

import com.fpoly.model.LoiMoiNhomDich;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoiMoiNhomDichRepository extends JpaRepository<LoiMoiNhomDich, Long> {
    List<LoiMoiNhomDich> findByNguoiDuocMoiIdAndTrangThai(Long userId, String trangThai);
    boolean existsByNhomDichIdAndNguoiDuocMoiId(Long nhomId, Long userId);
}