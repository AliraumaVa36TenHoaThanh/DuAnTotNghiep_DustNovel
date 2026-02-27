package com.fpoly.repository;

import com.fpoly.model.BinhLuan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BinhLuanRepository extends JpaRepository<BinhLuan, Long> {
    List<BinhLuan> findByTruyenIdOrderByNgayBinhLuanDesc(Long truyenId);
    
    List<BinhLuan> findByChuongIdAndParentIsNullOrderByNgayBinhLuanDesc(Long chuongId);
    @Query("""
    	    SELECT DISTINCT b
    	    FROM BinhLuan b
    	    LEFT JOIN FETCH b.replies r
    	    WHERE b.chuong.id = :chuongId
    	      AND b.parent IS NULL
    	    ORDER BY b.ngayBinhLuan DESC
    	""")
    	List<BinhLuan> findParentCommentsWithChildren(@Param("chuongId") Long chuongId);
} 