package com.fpoly.repository;
import org.springframework.data.jpa.repository.JpaRepository;


import com.fpoly.model.MoKhoaChuong;
public interface MoKhoaChuongRepository extends JpaRepository<MoKhoaChuong, Long>{
	
	boolean existsByNguoiDung_IdAndChuong_Id(Long nguoiDungId, Long chuongId);
}
