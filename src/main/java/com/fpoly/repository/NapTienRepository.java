package com.fpoly.repository;

import com.fpoly.model.NapTien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NapTienRepository extends JpaRepository<NapTien, Long> {
    List<NapTien> findByNguoiDungIdOrderByNgayTaoDesc(Long nguoiDungId);
    
    List<NapTien> findByTrangThai(String trangThai);
    @Query("""
    	    SELECT CAST(n.ngayTao AS date), SUM(n.soTienThuc)
    	    FROM NapTien n
    	    WHERE n.trangThai = 'DA_DUYET'
    	    GROUP BY CAST(n.ngayTao AS date)
    	    ORDER BY CAST(n.ngayTao AS date)
    	""")
    	List<Object[]> doanhThuTheoNgay();
    @Query("""
    	    SELECT n.nguoiDung.tenDangNhap, SUM(n.soTienThuc)
    	    FROM NapTien n
    	    WHERE n.trangThai = 'DA_DUYET'
    	    GROUP BY n.nguoiDung.tenDangNhap
    	    ORDER BY SUM(n.soTienThuc) DESC
    	""")
    	List<Object[]> topNapNhieu();
}