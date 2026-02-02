package com.fpoly.repository;

import com.fpoly.model.BinhLuan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BinhLuanRepository extends JpaRepository<BinhLuan, Long> {
    List<BinhLuan> findByTruyenIdOrderByNgayBinhLuanDesc(Long truyenId);
}