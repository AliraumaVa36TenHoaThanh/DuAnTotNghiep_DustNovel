package com.fpoly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fpoly.model.Chuong;

public interface ChuongRepository extends JpaRepository<Chuong, Long> {

	List<Chuong> findByTapIdOrderBySoChuongAsc(Long tapId);

    Optional<Chuong> findByTapIdAndSoChuong(Long tapId, Integer soChuong);

    @Query("SELECT MAX(c.soChuong) FROM Chuong c WHERE c.tap.id = :tapId")
    Integer findMaxSoChuongByTapId(@Param("tapId") Long tapId);
}
