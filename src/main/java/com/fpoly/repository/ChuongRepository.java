package com.fpoly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fpoly.model.Chuong;

public interface ChuongRepository extends JpaRepository<Chuong, Long> {

    List<Chuong> findByTruyenIdOrderBySoChuongAsc(Long truyenId);

    Optional<Chuong> findByTruyenIdAndSoChuong(Long truyenId, Integer soChuong);

    @Query("SELECT MAX(c.soChuong) FROM Chuong c WHERE c.truyen.id = :truyenId")
    Integer findMaxSoChuongByTruyenId(@Param("truyenId") Long truyenId);
}
