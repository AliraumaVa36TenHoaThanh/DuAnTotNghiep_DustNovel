package com.fpoly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.fpoly.model.NguoiDung;
@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {

    Optional<NguoiDung> findByTenDangNhap(String tenDangNhap);
    
    Optional<NguoiDung> findByEmail(String email);
    boolean existsByTenDangNhap(String tenDangNhap);

    boolean existsByEmail(String email);
    
    NguoiDung findByTenDangNhapOrEmail(String tenDangNhap, String email);
    
    @Query(value = """
            SELECT TOP 5 *
            FROM nguoi_dung
            ORDER BY NEWID()
        """, nativeQuery = true)
        List<NguoiDung> findRandom5Users();
}
