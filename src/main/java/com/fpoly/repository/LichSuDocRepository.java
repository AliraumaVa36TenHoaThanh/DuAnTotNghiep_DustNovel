package com.fpoly.repository;

import com.fpoly.model.Chuong;
import com.fpoly.model.LichSuDoc;
import com.fpoly.model.NguoiDung;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LichSuDocRepository extends JpaRepository<LichSuDoc, Long> {
	@Query("""
		    select l from LichSuDoc l
		    where l.nguoiDung.id = :userId
		    order by l.lanDocCuoi desc
		""")
		List<LichSuDoc> findLatestHistoryByUser(@Param("userId") Long userId);

	Optional<LichSuDoc> findByNguoiDungAndChuong(NguoiDung nguoiDung, Chuong chuong);
    Optional<LichSuDoc> findByNguoiDungIdAndTruyenId(Long userId, Long truyenId);
    Optional<LichSuDoc> findByNguoiDungIdAndChuongId(Long nguoiDungId, Long chuongId);

    List<LichSuDoc> findByNguoiDungIdOrderByLanDocCuoiDesc(Long nguoiDungId);
    
    @Query("""
    	    select l
    	    from LichSuDoc l
    	    where l.nguoiDung.id = :userId
    	      and l.lanDocCuoi = (
    	          select max(l2.lanDocCuoi)
    	          from LichSuDoc l2
    	          where l2.nguoiDung.id = :userId
    	            and l2.truyen.id = l.truyen.id
    	      )
    	    order by l.lanDocCuoi desc
    	""")
    	List<LichSuDoc> findLastReadPerTruyen(@Param("userId") Long userId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM LichSuDoc l WHERE l.chuong.id = :chuongId")
    void deleteByChuongId(@Param("chuongId") Long chuongId);
    
    @Transactional
    void deleteByTruyenId(Long truyenId);
    @Modifying
    @Transactional
    @Query("DELETE FROM LichSuDoc l WHERE l.chuong.tap.id = :tapId")
    void deleteByTapId(@Param("tapId") Long tapId);
}