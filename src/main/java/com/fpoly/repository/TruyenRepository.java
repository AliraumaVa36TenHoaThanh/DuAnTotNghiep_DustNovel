package com.fpoly.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fpoly.model.Truyen;
import com.fpoly.model.enums.LoaiTruyen;

@Repository
public interface TruyenRepository extends JpaRepository<Truyen, Long> {
	List<Truyen> findByLoaiTruyen(LoaiTruyen loaiTruyen);

	@Query("""
			    SELECT DISTINCT t
			    FROM Truyen t
			    JOIN t.theLoais tl
			    WHERE tl.id = :theLoaiId
			""")
	List<Truyen> findByTheLoai(@Param("theLoaiId") Long theLoaiId);

	@Query("""
			    SELECT DISTINCT t
			    FROM Truyen t
			    JOIN t.theLoais tl
			    WHERE tl.id = :theLoaiId
			""")
	List<Truyen> findByTheLoaiId(@Param("theLoaiId") Long theLoaiId);

	List<Truyen> findByTheLoais_Id(Long theLoaiId);
	
    List<Truyen> findByTenTruyenContainingIgnoreCase(String keyword);
}
