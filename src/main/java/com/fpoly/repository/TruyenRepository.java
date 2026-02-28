package com.fpoly.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpoly.model.Truyen;
import com.fpoly.model.enums.LoaiTruyen;

@Repository
public interface TruyenRepository extends JpaRepository<Truyen, Long> {
  
    @Query("""
        SELECT DISTINCT t
        FROM Truyen t
        JOIN t.theLoais tl
        WHERE tl.id = :theLoaiId
    """)
    List<Truyen> findByTheLoai(@Param("theLoaiId") Long theLoaiId);

    List<Truyen> findByTheLoais_Id(Long theLoaiId);
  
    List<Truyen> findByLoaiTruyen(LoaiTruyen loaiTruyen);

    
    @Query("""
        SELECT t FROM Truyen t
        WHERE t.loaiTruyen = :loaiTruyen
    """)
    List<Truyen> findByLoaiTruyenJPQL(
        @Param("loaiTruyen") LoaiTruyen loaiTruyen
    );

    List<Truyen> findByTenTruyenContainingIgnoreCase(String keyword);


    @Query("""
    	    SELECT t FROM Truyen t
    	    WHERE
    	    (:tenTruyen IS NULL OR LOWER(t.tenTruyen) LIKE LOWER(CONCAT('%', :tenTruyen, '%')))
    	    AND (:tenTacGia IS NULL OR LOWER(t.tenTacGia) LIKE LOWER(CONCAT('%', :tenTacGia, '%')))
    	    AND (:loaiTruyen IS NULL OR t.loaiTruyen = :loaiTruyen)
    	    AND (:showTag18 IS NULL OR t.tag18 = :showTag18)
    	""")
    	List<Truyen> timKiemKhongTheLoai(
    	    @Param("tenTruyen") String tenTruyen,
    	    @Param("tenTacGia") String tenTacGia,
    	    @Param("loaiTruyen") LoaiTruyen loaiTruyen,
    	    @Param("showTag18") Boolean showTag18
    	);

    @Query("""
    	    SELECT DISTINCT t FROM Truyen t
    	    WHERE
    	    (:tenTruyen IS NULL OR LOWER(t.tenTruyen) LIKE LOWER(CONCAT('%', :tenTruyen, '%')))
    	    AND (:tenTacGia IS NULL OR LOWER(t.tenTacGia) LIKE LOWER(CONCAT('%', :tenTacGia, '%')))
    	    AND (:loaiTruyen IS NULL OR t.loaiTruyen = :loaiTruyen)

    	    AND (
    	        :includeSize = 0 OR t.id IN (
    	            SELECT t2.id FROM Truyen t2
    	            JOIN t2.theLoais tl2
    	            WHERE tl2.id IN :includeIds
    	            GROUP BY t2.id
    	        )
    	    )

    	    AND (
    	        :excludeSize = 0 OR t.id NOT IN (
    	            SELECT t3.id FROM Truyen t3
    	            JOIN t3.theLoais tl3
    	            WHERE tl3.id IN :excludeIds
    	        )
    	    )

    	    AND (:showTag18 IS NULL OR t.tag18 = :showTag18)
    	""")
    	List<Truyen> timKiemCoTheLoai(
    	    @Param("tenTruyen") String tenTruyen,
    	    @Param("tenTacGia") String tenTacGia,
    	    @Param("loaiTruyen") LoaiTruyen loaiTruyen,
    	    @Param("includeIds") List<Long> includeIds,
    	    @Param("excludeIds") List<Long> excludeIds,
    	    @Param("showTag18") Boolean showTag18,
    	    @Param("includeSize") long includeSize,
    	    @Param("excludeSize") long excludeSize
    	);
    
    @Query("""
    	    SELECT t FROM Truyen t
    	    WHERE t.nguoiDang.id = :userId
    	    AND t.loaiTruyen = :loai
    	""")
    	List<Truyen> findByUserAndLoai(
    	    @Param("userId") Long userId,
    	    @Param("loai") LoaiTruyen loai
    	);
    
    @Query("""
            SELECT t FROM Truyen t
            JOIN t.theLoais tl
            WHERE tl.id = :theLoaiId
        """)
        List<Truyen> findTruyenByTheLoaiId(Long theLoaiId);
    
    @Modifying
    @Query("UPDATE Truyen t SET t.luotXem = t.luotXem + 1 WHERE t.id = :id")
    void tangLuotXem(@Param("id") Long id);
    
    @Query("SELECT t FROM Truyen t LEFT JOIN Chuong c ON t.id = c.truyen.id WHERE t.loaiTruyen = :loaiTruyen GROUP BY t.id, t.tenTruyen, t.moTa, t.tenTacGia, t.loaiTruyen, t.trangThai, t.tag18, t.nguoiDang.id, t.anhBia, t.ngayTao, t.tongLike, t.luotXem ORDER BY MAX(c.ngayTao) DESC NULLS LAST, t.ngayTao DESC")
    List<Truyen> findByLoaiTruyenOrderByChuongMoiNhat(@Param("loaiTruyen") LoaiTruyen loaiTruyen);
    
    @Query("SELECT t FROM Truyen t " +
            "LEFT JOIN t.danhSachTap tap " +
            "LEFT JOIN tap.danhSachChuong c " +
            "WHERE t.loaiTruyen = :loaiTruyen " +
            "GROUP BY t " +
            "ORDER BY COALESCE(MAX(c.ngayTao), t.ngayTao) DESC")
     List<Truyen> findByLoaiTruyenOrderByChuongMoiNhatHome(@Param("loaiTruyen") LoaiTruyen loaiTruyen);
    
    @Query("SELECT t FROM Truyen t " +
            "LEFT JOIN t.danhSachTap tap " +
            "LEFT JOIN tap.danhSachChuong c " +
            "WHERE t.loaiTruyen = :loaiTruyen " +
            "GROUP BY t " +
            "ORDER BY COALESCE(MAX(c.ngayTao), t.ngayTao) DESC")
    Page<Truyen> findByPage(@Param("loaiTruyen") LoaiTruyen loaiTruyen, Pageable pageable);
    
    @Query(
    	    value = """
    	        SELECT t
    	        FROM Truyen t
    	        WHERE t.loaiTruyen = :loaiTruyen
    	        ORDER BY (
    	            SELECT MAX(c.ngayTao)
    	            FROM Chuong c
    	            JOIN c.tap tap
    	            WHERE tap.truyen.id = t.id
    	        ) DESC
    	        """,
    	    countQuery = """
    	        SELECT COUNT(t)
    	        FROM Truyen t
    	        WHERE t.loaiTruyen = :loaiTruyen
    	        """
    	)
    	Page<Truyen> findByLoaiTruyenOrderByChuongMoiNhatPage(
    	    @Param("loaiTruyen") LoaiTruyen loaiTruyen,
    	    Pageable pageable
    	);
	boolean existsByTenTruyenIgnoreCase(String tenTruyen);
	boolean existsByTenTruyenIgnoreCaseAndIdNot(String tenTruyen, Long id);
}
