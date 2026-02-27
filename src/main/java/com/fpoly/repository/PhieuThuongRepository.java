package com.fpoly.repository;

import com.fpoly.model.PhieuThuong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PhieuThuongRepository extends JpaRepository<PhieuThuong, Long> { 
	 @Query("""
		        SELECT p.soLuong
		        FROM PhieuThuong p
		        WHERE p.nguoiDung.id = :nguoiDungId
		    """)
		    Long findSoLuongByNguoiDungId(Long nguoiDungId);
	
}
