package com.fpoly.repository;

import com.fpoly.model.LoiMoiNhomDich;
import com.fpoly.model.NguoiDung;
import com.fpoly.model.NhomDich;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LoiMoiNhomDichRepository extends JpaRepository<LoiMoiNhomDich, Long> {

    Optional<LoiMoiNhomDich> 
    findByNhomDichAndNguoiMoi(NhomDich nhomDich, NguoiDung nguoiMoi);

    List<LoiMoiNhomDich> 
    findByNguoiDuocMoiAndTrangThai(NguoiDung nguoiDuocMoi, String trangThai);
    
    Optional<LoiMoiNhomDich> 
    findByNhomDichAndNguoiDuocMoi(NhomDich nhom, NguoiDung user);
    List<LoiMoiNhomDich> findByNhomDichAndTrangThai(
            NhomDich nhomDich,
            String trangThai
    );

    Optional<LoiMoiNhomDich> findByNhomDichAndNguoiMoiAndTrangThai(
            NhomDich nhom, 
            NguoiDung user, 
            String trangThai
    );
    
    boolean existsByNhomDichAndNguoiMoi(NhomDich nhom, NguoiDung nguoiMoi);
}