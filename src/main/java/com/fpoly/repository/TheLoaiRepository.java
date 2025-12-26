	package com.fpoly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fpoly.model.TheLoai;
@Repository
public interface TheLoaiRepository extends JpaRepository<TheLoai, Long>{

}
