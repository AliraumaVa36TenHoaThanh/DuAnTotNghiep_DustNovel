package com.fpoly.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpoly.model.Truyen;
import com.fpoly.model.enums.LoaiTruyen;

@Repository
public interface TruyenRepository extends JpaRepository<Truyen, Long> {

    /* ===================== THỂ LOẠI ===================== */

  
    @Query("""
        SELECT DISTINCT t
        FROM Truyen t
        JOIN t.theLoais tl
        WHERE tl.id = :theLoaiId
    """)
    List<Truyen> findByTheLoai(@Param("theLoaiId") Long theLoaiId);

    List<Truyen> findByTheLoais_Id(Long theLoaiId);


    /* ===================== LOẠI TRUYỆN (ENUM) ===================== */

  
    List<Truyen> findByLoaiTruyen(LoaiTruyen loaiTruyen);

    
    @Query("""
        SELECT t FROM Truyen t
        WHERE t.loaiTruyen = :loaiTruyen
    """)
    List<Truyen> findByLoaiTruyenJPQL(
        @Param("loaiTruyen") LoaiTruyen loaiTruyen
    );


    /* ===================== TÌM KIẾM ===================== */

    List<Truyen> findByTenTruyenContainingIgnoreCase(String keyword);


    /* ===================== TÌM KIẾM KHÔNG THỂ LOẠI ===================== */

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




    /* ===================== TÌM KIẾM CÓ THỂ LOẠI ===================== */

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
}
