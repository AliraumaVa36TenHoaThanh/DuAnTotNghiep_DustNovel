package com.fpoly.repository;

import java.time.LocalDateTime;
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
    
    @Query("""
    	    SELECT MAX(c.ngayTao)
    	    FROM Chuong c
    	    WHERE c.truyen.id = :truyenId
    	""")
    	LocalDateTime layNgayCapNhatTruyen(@Param("truyenId") Long truyenId);
    
    @Query(value = """
    	    SELECT SUM(LEN(CAST(c.noi_dung AS NVARCHAR(MAX))))
    	    FROM chuong c
    	    WHERE c.truyen_id = :truyenId
    	""", nativeQuery = true)
    	Long tinhTongSoTu(@Param("truyenId") Long truyenId);
    
    Optional<Chuong> findFirstByTapIdOrderBySoChuongAsc(Long tapId);
    Optional<Chuong> findFirstByTapIdOrderBySoChuongDesc(Long tapId);
    
    Optional<Chuong> findFirstByTapTruyenIdOrderByTapSoTapAscSoChuongAsc(Long truyenId);
    Optional<Chuong> findFirstByTapTruyenIdOrderByTapSoTapDescSoChuongDesc(Long truyenId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM LichSuDoc l WHERE l.chuong.tap.id = :tapId")
    void deleteByTapId(@Param("tapId") Long tapId);
}
