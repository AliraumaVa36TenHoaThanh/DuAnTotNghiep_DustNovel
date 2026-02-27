package com.fpoly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fpoly.model.NapTienTuDong;

@Repository
public interface NapTienTuDongRepository extends JpaRepository<NapTienTuDong, Long> {  
    NapTienTuDong findByNoiDungCk(String noiDungCk);
}