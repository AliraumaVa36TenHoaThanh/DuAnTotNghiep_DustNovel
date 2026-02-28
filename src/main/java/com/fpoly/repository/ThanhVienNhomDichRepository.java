package com.fpoly.repository;

import com.fpoly.model.ThanhVienNhomDich;
import com.fpoly.model.NguoiDung;
import com.fpoly.model.NhomDich;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ThanhVienNhomDichRepository extends JpaRepository<ThanhVienNhomDich, Long> {
    Optional<ThanhVienNhomDich> findByNhomDichIdAndNguoiDungId(Long nhomId, Long userId);
    List<ThanhVienNhomDich> findByNhomDich(NhomDich nhomDich);
    List<ThanhVienNhomDich> 
    findByNhomDichAndTrangThai(NhomDich nhomDich, String trangThai);


    Optional<ThanhVienNhomDich> 
    findByNhomDichAndNguoiDung(NhomDich nhom, NguoiDung nguoiDung);
    
    boolean existsByNguoiDungAndTrangThai(NguoiDung nguoiDung, String trangThai);

}