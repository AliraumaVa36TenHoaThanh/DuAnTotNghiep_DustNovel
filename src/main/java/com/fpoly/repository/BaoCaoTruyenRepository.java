package com.fpoly.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fpoly.model.BaoCaoTruyen;

@Repository
public interface BaoCaoTruyenRepository extends JpaRepository<BaoCaoTruyen, Long> {
	
	@Query("SELECT b FROM BaoCaoTruyen b WHERE " +
	           "(:keyword IS NULL OR :keyword = '' OR LOWER(b.truyenBiBaoCao.tenTruyen) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
	           "(:lyDo IS NULL OR :lyDo = '' OR b.lyDo = :lyDo) " +
	           "ORDER BY b.trangThai ASC, b.ngayBaoCao DESC") // Ưu tiên hiện CHO_XU_LY lên đầu
	    List<BaoCaoTruyen> timKiemBaoCao(@Param("keyword") String keyword, @Param("lyDo") String lyDo);
	
}