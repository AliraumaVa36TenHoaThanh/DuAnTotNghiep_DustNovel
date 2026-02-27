	package com.fpoly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fpoly.model.TheLoai;
import com.fpoly.model.enums.StatusTheLoai;

import java.util.List;
@Repository
public interface TheLoaiRepository extends JpaRepository<TheLoai, Long>{
	boolean existsByTenTheLoai(String tenTheLoai);
	boolean existsByTenTheLoaiAndIdNot(String tenTheLoai, Long id);
	
	List<TheLoai> findByStatusTheLoai(StatusTheLoai statusTheLoai);

}
