package com.fpoly.repository;

import com.fpoly.model.ThanhVienNhomDich;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ThanhVienNhomDichRepository extends JpaRepository<ThanhVienNhomDich, Long> {
    Optional<ThanhVienNhomDich> findByNhomDichIdAndNguoiDungId(Long nhomId, Long userId);
}