package com.fpoly.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fpoly.model.NguoiDung;
@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long>{
	Optional<NguoiDung> findByTenDangNhap(String tenDangNhap);
}
