package com.fpoly.service;

import com.fpoly.model.DanhGiaTruyen;
import com.fpoly.model.NguoiDung;
import com.fpoly.model.Truyen;
import com.fpoly.repository.DanhGiaTruyenRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DanhGiaTruyenService {

    @Autowired
    private DanhGiaTruyenRepository danhGiaRepo;

    public List<DanhGiaTruyen> layDanhGiaTheoTruyen(Long truyenId) {
        List<DanhGiaTruyen> list = danhGiaRepo.findByTruyenIdOrderByNgayDanhGiaDesc(truyenId);
        return list != null ? list : new ArrayList<>(); // Trả về mảng rỗng thay vì null
    }

    public Double layDiemTrungBinh(Long truyenId) {
        Double avg = danhGiaRepo.calculateAverageStar(truyenId);
        // Làm tròn 1 chữ số thập phân (VD: 4.5, 4.8). Nếu chưa có ai đánh giá thì trả về 0.0
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }

    public void luuDanhGia(NguoiDung user, Truyen truyen, Integer soSao, String noiDung) {
        // Kiểm tra xem đã đánh giá chưa
        DanhGiaTruyen existing = danhGiaRepo.findByNguoiDungIdAndTruyenId(user.getId(), truyen.getId());
        
        if (existing != null) {
            // Đã đánh giá -> Cập nhật lại nội dung và số sao mới
            existing.setSoSao(soSao);
            existing.setNoiDung(noiDung);
            existing.setNgayDanhGia(LocalDateTime.now());
            danhGiaRepo.save(existing);
        } else {
            // Chưa đánh giá -> Tạo mới
            DanhGiaTruyen newReview = new DanhGiaTruyen();
            newReview.setNguoiDung(user);
            newReview.setTruyen(truyen);
            newReview.setSoSao(soSao);
            newReview.setNoiDung(noiDung);
            newReview.setNgayDanhGia(LocalDateTime.now());
            danhGiaRepo.save(newReview);
        }
    }
    
    @Transactional
    public void xoaDanhGia(Long idDanhGia, Long nguoiDungId) {
        DanhGiaTruyen danhGia = danhGiaRepo.findById(idDanhGia)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá này!"));
        if (!danhGia.getNguoiDung().getId().equals(nguoiDungId)) {
            throw new RuntimeException("Lỗi bảo mật: Bạn không có quyền xóa đánh giá của người khác!");
        }
        danhGiaRepo.delete(danhGia);
    }
}