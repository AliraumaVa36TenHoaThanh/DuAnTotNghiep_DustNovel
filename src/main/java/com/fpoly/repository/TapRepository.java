package com.fpoly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fpoly.model.Tap;

import jakarta.transaction.Transactional;
public interface TapRepository extends JpaRepository<Tap, Long> {

	 List<Tap> findByTruyenIdOrderBySoTapAsc(Long truyenId);

	    @Query("SELECT MAX(t.soTap) FROM Tap t WHERE t.truyen.id = :truyenId")
	    Integer findMaxSoTapByTruyenId(@Param("truyenId") Long truyenId);
	    
	    @Modifying
	    @Transactional
	    @Query("DELETE FROM Tap t WHERE t.truyen.id = :truyenId")
	    void deleteByTruyenId(@Param("truyenId") Long truyenId);
	    
	    Optional<Tap> findFirstByTruyenIdAndSoTapGreaterThanOrderBySoTapAsc(Long truyenId, Integer soTap);
	    Optional<Tap> findFirstByTruyenIdAndSoTapLessThanOrderBySoTapDesc(Long truyenId, Integer soTap);

}
