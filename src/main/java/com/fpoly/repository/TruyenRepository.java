package com.fpoly.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fpoly.model.Truyen;
import com.fpoly.model.enums.LoaiTruyen;
@Repository
public interface TruyenRepository extends JpaRepository<Truyen, Long>{
	List<Truyen> findByLoaiTruyen(LoaiTruyen loaiTruyen);
}
