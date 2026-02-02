package com.fpoly.repository;

import com.fpoly.model.NapTien;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NapTienRepository extends JpaRepository<NapTien, Long> {
    List<NapTien> findByNguoiDungIdOrderByNgayTaoDesc(Long nguoiDungId);
}