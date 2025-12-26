package com.fpoly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fpoly.model.NguoiDung;
@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long>{

}
