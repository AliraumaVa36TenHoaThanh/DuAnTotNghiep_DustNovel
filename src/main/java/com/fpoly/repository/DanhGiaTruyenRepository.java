package com.fpoly.repository;

import com.fpoly.model.DanhGiaTruyen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DanhGiaTruyenRepository extends JpaRepository<DanhGiaTruyen, Long> {
    
    // Lấy danh sách đánh giá của 1 truyện (Sắp xếp mới nhất lên đầu)
    List<DanhGiaTruyen> findByTruyenIdOrderByNgayDanhGiaDesc(Long truyenId);

    // Tính điểm trung bình sao của 1 truyện
    @Query("SELECT AVG(d.soSao) FROM DanhGiaTruyen d WHERE d.truyen.id = :truyenId")
    Double calculateAverageStar(Long truyenId);

    // Tìm xem user này đã đánh giá truyện này chưa (Để phục vụ tính năng cập nhật đánh giá)
    DanhGiaTruyen findByNguoiDungIdAndTruyenId(Long nguoiDungId, Long truyenId);
}