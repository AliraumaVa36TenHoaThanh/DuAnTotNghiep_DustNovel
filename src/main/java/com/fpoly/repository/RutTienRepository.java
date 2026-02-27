package com.fpoly.repository;

import com.fpoly.model.RutTien;
import com.fpoly.model.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RutTienRepository extends JpaRepository<RutTien, Long> {

    // Lấy tất cả theo user
    List<RutTien> findByNguoiDungOrderByNgayTaoDesc(NguoiDung nguoiDung);

    // Lọc theo trạng thái
    List<RutTien> findByTrangThaiOrderByNgayTaoDesc(String trangThai);

    // Lọc theo user và trạng thái
    List<RutTien> findByNguoiDungAndTrangThaiOrderByNgayTaoDesc(
            NguoiDung nguoiDung, String trangThai);

    // Lấy tất cả mới nhất
    List<RutTien> findAllByOrderByNgayTaoDesc();
    
    List<RutTien> findByNguoiDung_TenDangNhapContainingIgnoreCase(String username);

    List<RutTien> findByTrangThai(String trangThai);

    List<RutTien> findByNguoiDung_TenDangNhapContainingIgnoreCaseAndTrangThai(
            String username, String trangThai);
}