package com.fpoly.repository;

import com.fpoly.model.LikeTruyen;
import com.fpoly.model.NguoiDung;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeTruyenRepository extends JpaRepository<LikeTruyen, Long> {
    List<LikeTruyen> findByNguoiDung(NguoiDung nguoiDung);
	 boolean existsByNguoiDung_IdAndTruyen_Id(Long userId, Long truyenId);

	    Optional<LikeTruyen> findByNguoiDung_IdAndTruyen_Id(Long userId, Long truyenId);

	    long countByTruyen_Id(Long truyenId);
	    }
