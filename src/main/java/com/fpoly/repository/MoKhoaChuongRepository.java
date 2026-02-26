package com.fpoly.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fpoly.model.MoKhoaChuong;

import jakarta.transaction.Transactional;
public interface MoKhoaChuongRepository extends JpaRepository<MoKhoaChuong, Long>{
	
	boolean existsByNguoiDung_IdAndChuong_Id(Long nguoiDungId, Long chuongId);
	
	@Modifying
    @Transactional
    @Query("""
        DELETE FROM MoKhoaChuong m
        WHERE m.chuong.id IN (
            SELECT c.id FROM Chuong c
            WHERE c.tap.id IN (
                SELECT t.id FROM Tap t
                WHERE t.truyen.id = :truyenId
            )
        )
    """)
    void deleteByTruyenId(@Param("truyenId") Long truyenId);
}
