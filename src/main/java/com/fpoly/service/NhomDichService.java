package com.fpoly.service;

import com.fpoly.model.LoiMoiNhomDich;
import com.fpoly.model.NguoiDung;
import com.fpoly.model.NhomDich;
import com.fpoly.model.ThanhVienNhomDich;
import com.fpoly.repository.LoiMoiNhomDichRepository;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.repository.NhomDichRepository;
import com.fpoly.repository.ThanhVienNhomDichRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NhomDichService {

    private final NhomDichRepository nhomDichRepository;

    private final ThanhVienNhomDichRepository thanhVienRepository;
    
    private final LoiMoiNhomDichRepository loiMoiRepo;
    
    @Autowired
    private NguoiDungRepository nguoiDungRepository;
    
    private NguoiDung getUserDangDangNhap() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return nguoiDungRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
    }

    public NhomDich findById(Long id) {
        return nhomDichRepository.findById(id).orElse(null);
    }

    public List<ThanhVienNhomDich> getThanhVien(NhomDich nhom) {
        return thanhVienRepository.findByNhomDich(nhom);
    }
    public void taoNhom(String tenNhom, String moTa, NguoiDung truongNhom) {
    	
    	// 🔥 Nếu đã là trưởng nhóm
        if (nhomDichRepository.existsByTruongNhom(truongNhom)) {
            throw new RuntimeException("Bạn đã tạo 1 nhóm rồi!");
        }

        NhomDich nhom = new NhomDich();
        nhom.setTenNhom(tenNhom);
        nhom.setMoTa(moTa);
        nhom.setTruongNhom(truongNhom);
        nhom.setTrangThai("HOAT_DONG");
        nhom.setNgayTao(LocalDateTime.now());

        nhomDichRepository.save(nhom);
    }
    
    public boolean daGuiYeuCau(NhomDich nhom, NguoiDung user) {
        return loiMoiRepo
                .findByNhomDichAndNguoiMoi(nhom, user)
                .isPresent();
    }

    @Transactional
    public void guiYeuCauThamGia(NhomDich nhom, NguoiDung user) {

        Optional<LoiMoiNhomDich> optional =
                loiMoiRepo.findByNhomDichAndNguoiDuocMoi(nhom, user);

        LoiMoiNhomDich loiMoi;

        if (optional.isPresent()) {

            // Đã từng tồn tại → dùng lại record cũ
            loiMoi = optional.get();
            loiMoi.setTrangThai("CHO_DUYET");

        } else {

            // Chưa từng tồn tại → tạo mới
            loiMoi = new LoiMoiNhomDich();
            loiMoi.setNhomDich(nhom);
            loiMoi.setNguoiMoi(user);
            loiMoi.setNguoiDuocMoi(user);
            loiMoi.setTrangThai("CHO_DUYET");
        }

        loiMoiRepo.save(loiMoi);
    }
    
    public List<LoiMoiNhomDich> getYeuCauChoDuyet(NhomDich nhom) {
        return loiMoiRepo.findByNhomDichAndTrangThai(nhom, "CHO_DUYET");
    }

//    public void duyetYeuCau(Long loiMoiId) {
//
//        LoiMoiNhomDich loiMoi = loiMoiRepo.findById(loiMoiId).orElse(null);
//        if (loiMoi == null) return;
//
//        // Tạo thành viên mới
//        ThanhVienNhomDich tv = new ThanhVienNhomDich();
//        tv.setNhomDich(loiMoi.getNhomDich());
//        tv.setNguoiDung(loiMoi.getNguoiMoi());
//        tv.setVaiTro("THANH_VIEN");
//        tv.setTrangThai("DA_DUYET");
//
//        thanhVienRepository.save(tv);
//
//        loiMoi.setTrangThai("DA_DUYET");
//        loiMoiRepo.save(loiMoi);
//    }
    
    @Transactional
    public void duyetYeuCau(Long loiMoiId) {

        LoiMoiNhomDich loiMoi = loiMoiRepo.findById(loiMoiId).orElse(null);
        if (loiMoi == null) return;

        NhomDich nhom = loiMoi.getNhomDich();
        NguoiDung user = loiMoi.getNguoiMoi();

        // 🔥 Kiểm tra đã tồn tại thành viên chưa
        Optional<ThanhVienNhomDich> tonTai =
                thanhVienRepository.findByNhomDichAndNguoiDung(nhom, user);

        if (tonTai.isPresent()) {

            // Nếu đã tồn tại (ví dụ từng bị kick) → cập nhật lại
            ThanhVienNhomDich tv = tonTai.get();
            tv.setTrangThai("DA_DUYET");
            tv.setVaiTro("THANH_VIEN");

            thanhVienRepository.save(tv);

        } else {

            // Nếu chưa từng tồn tại → tạo mới
            ThanhVienNhomDich tv = new ThanhVienNhomDich();
            tv.setNhomDich(nhom);
            tv.setNguoiDung(user);
            tv.setVaiTro("THANH_VIEN");
            tv.setTrangThai("DA_DUYET");

            thanhVienRepository.save(tv);
        }

        // Cập nhật trạng thái lời mời
        loiMoi.setTrangThai("DA_DUYET");
        loiMoiRepo.save(loiMoi);
    }
    
    
    @Transactional
    public void tuChoiYeuCau(Long loiMoiId) {
        LoiMoiNhomDich loiMoi = loiMoiRepo.findById(loiMoiId).orElse(null);
        if (loiMoi == null) return;

        loiMoi.setTrangThai("TU_CHOI");
        loiMoiRepo.save(loiMoi);
        
        loiMoiRepo.deleteById(loiMoiId);
    }
    
    @Transactional
    public void kickThanhVien(ThanhVienNhomDich tv) {

        tv.setTrangThai("DA_KICK");
        thanhVienRepository.save(tv);

        loiMoiRepo.deleteByNguoiDuocMoiAndNhomDich(
                tv.getNguoiDung(),
                tv.getNhomDich()
        );
    }
    @Transactional
    public void xoaNhom(Long nhomId) {

        NhomDich nhom = nhomDichRepository.findById(nhomId)
                .orElseThrow();

        NguoiDung user = getUserDangDangNhap();

        if (!nhom.getTruongNhom().getId().equals(user.getId())) {
            throw new RuntimeException("Không có quyền");
        }

        nhomDichRepository.delete(nhom);
    }
    
    @Transactional
    public void outNhom(Long nhomId) {	

        NguoiDung user = getUserDangDangNhap();
        NhomDich nhom = nhomDichRepository.findById(nhomId).orElseThrow();

        ThanhVienNhomDich tv = thanhVienRepository
                .findByNhomDichAndNguoiDung(nhom, user)
                .orElseThrow();
 
//        thanhVienRepository.delete(tv);
        
        tv.setTrangThai("DA_KICK");
        thanhVienRepository.save(tv);

        loiMoiRepo.deleteByNguoiDuocMoiAndNhomDich(user, nhom);

    }
}