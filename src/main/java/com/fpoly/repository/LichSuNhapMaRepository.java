package com.fpoly.repository;
import com.fpoly.model.MaThuong;
import com.fpoly.model.LichSuNhapMa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fpoly.model.NguoiDung;
@Repository
public interface LichSuNhapMaRepository extends JpaRepository<LichSuNhapMa, Long> {
	boolean existsByNguoiDungAndMaThuong(NguoiDung nguoiDung, MaThuong maThuong);
}
