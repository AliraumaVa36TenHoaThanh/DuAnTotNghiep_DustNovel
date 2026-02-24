package com.fpoly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fpoly.model.Chuong;

import jakarta.transaction.Transactional;

public interface ChuongRepository extends JpaRepository<Chuong, Long> {

	List<Chuong> findByTapIdOrderBySoChuongAsc(Long tapId);

    Optional<Chuong> findByTapIdAndSoChuong(Long tapId, Integer soChuong);

    @Query("SELECT MAX(c.soChuong) FROM Chuong c WHERE c.tap.id = :tapId")
    Integer findMaxSoChuongByTapId(@Param("tapId") Long tapId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Chuong c WHERE c.tap.id IN "
         + "(SELECT t.id FROM Tap t WHERE t.truyen.id = :truyenId)")
    void deleteByTruyenId(@Param("truyenId") Long truyenId);
}
