package com.fpoly.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.fpoly.model.Chuong;
import com.fpoly.model.NguoiDung;
import com.fpoly.model.Tap;
import com.fpoly.model.Truyen;
import com.fpoly.model.enums.TrangThaiTruyen;
import com.fpoly.model.enums.VaiTro;
import com.fpoly.repository.ChuongRepository;
import com.fpoly.repository.MoKhoaChuongRepository;
import com.fpoly.repository.NhomDichRepository;
import com.fpoly.repository.TapRepository;
import com.fpoly.repository.ThanhVienNhomDichRepository;
import com.fpoly.repository.TruyenRepository;
import com.fpoly.security.SecurityUtil;
import com.fpoly.model.NhomDich;
@Service("permissionService")
public class PermissionService {

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private TruyenRepository truyenRepo;

    @Autowired
    private ChuongRepository chuongRepo;

    @Autowired
    private MoKhoaChuongRepository moKhoaChuongRepo;
    
    @Autowired
    private TapRepository tapRepo;
    @Autowired
    private ThanhVienNhomDichRepository thanhVienRepo;
    @Autowired
    private NhomDichRepository nhomDichRepo;
    @Autowired
    private TruyenService truyenService;
    
    private NguoiDung currentUser() {
        return securityUtil.getCurrentUserFromDB();
    }

    private boolean isAdmin(NguoiDung user) {
        return user != null && user.getVaiTro() == VaiTro.ADMIN;
    }

    private boolean isNguoiDangHoacThanhVienNhom(Truyen truyen, NguoiDung user) {
        if (truyen == null || user == null) return false;

        // 1. Nếu là người trực tiếp đăng truyện
        if (truyen.getNguoiDang() != null && truyen.getNguoiDang().getId().equals(user.getId())) {
            return true;
        }

        // 2. Nếu truyện thuộc về 1 nhóm dịch
        if (truyen.getNhomDich() != null) {
            // Kiểm tra xem user có phải là thành viên ĐÃ DUYỆT của nhóm đó không
            return thanhVienRepo.existsByNhomDichAndNguoiDungAndTrangThai(
                    truyen.getNhomDich(), user, "DA_DUYET");
        }
        
        if (truyen.getNguoiDang() != null) {
            // Lấy nhóm của người đăng truyện
            List<NhomDich> nhomCuaNguoiDang = nhomDichRepo.findNhomByUserId(truyen.getNguoiDang().getId());
            if (nhomCuaNguoiDang != null && !nhomCuaNguoiDang.isEmpty()) {
                NhomDich nhomChinh = nhomCuaNguoiDang.get(0); // Lấy nhóm đầu tiên
                
                // Kiểm tra xem User hiện tại có phải trưởng nhóm hay thành viên duyệt của nhóm này không
                if (nhomChinh.getTruongNhom().getId().equals(user.getId())) return true;
                return thanhVienRepo.existsByNhomDichAndNguoiDungAndTrangThai(nhomChinh, user, "DA_DUYET");
            }
        }

        return false;
    }
    
    
//    public boolean canEditTruyen(Long truyenId) {
//        NguoiDung user = currentUser();
//        if (user == null) return false;
//        if (isAdmin(user)) return true;
//
//        Truyen truyen = truyenRepo.findById(truyenId).orElse(null);
//        return truyen != null &&
//               truyen.getNguoiDang() != null &&
//               truyen.getNguoiDang().getId().equals(user.getId());
//    }
    public boolean canEditTruyen(Long truyenId) {
        NguoiDung user = currentUser();
        if (user == null) return false;
        if (isAdmin(user)) return true;

        Truyen truyen = truyenRepo.findById(truyenId).orElse(null);
        return isNguoiDangHoacThanhVienNhom(truyen, user); // Gọi hàm kiểm tra chung
    }
    
    public boolean canDeleteTruyen(Long truyenId) {
        return canEditTruyen(truyenId);
    }

    public boolean canAddChuong(Long truyenId) {
        return canEditTruyen(truyenId);
    }

  
//    public boolean canEditChuong(Long chuongId) {
//        NguoiDung user = currentUser();
//        if (user == null) return false;
//        if (isAdmin(user)) return true;
//
//        Chuong chuong = chuongRepo.findById(chuongId).orElse(null);
//        return chuong != null &&
//               chuong.getNguoiDang() != null &&
//               chuong.getNguoiDang().getId().equals(user.getId());
//    }
    
    public boolean canEditChuong(Long chuongId) {
        NguoiDung user = currentUser();
        if (user == null) return false;
        if (isAdmin(user)) return true;

        Chuong chuong = chuongRepo.findById(chuongId).orElse(null);
        if (chuong == null || chuong.getTruyen() == null) return false;
        
        // Cấp quyền dựa trên truyện chứa chương đó
        return isNguoiDangHoacThanhVienNhom(chuong.getTruyen(), user);
    }

    
    public boolean canToggleChuong(Long chuongId) {
        return canEditChuong(chuongId);
    }

 
//    public boolean canReadChuong(Chuong chuong, NguoiDung user) {
//        if (chuong == null) return false;
//        if (!chuong.isKhoa()) return true;
//        if (user == null) return false;
//        if (isAdmin(user)) return true;
//        if (chuong.getNguoiDang() != null && chuong.getNguoiDang().getId().equals(user.getId())) {
//            return true;
//        }
//  
//        if (chuong.getTruyen() != null &&
//            chuong.getTruyen().getNguoiDang() != null &&
//            chuong.getTruyen().getNguoiDang().getId().equals(user.getId())) {
//            return true;
//        }
//
//        return moKhoaChuongRepo.existsByNguoiDung_IdAndChuong_Id( user.getId(), chuong.getId());
//    }
    
//    public boolean canReadChuong(Chuong chuong, NguoiDung user) {
//
//        if (!chuong.isKhoa()) {
//            return true;
//        }
//
//        if (user == null) {
//            return false;
//        }
//
//        if (chuong.getNguoiDang() != null
//            && chuong.getNguoiDang().getId().equals(user.getId())) {
//            return true;
//        }
//        
//        return moKhoaChuongRepo
//                .existsByNguoiDung_IdAndChuong_Id(
//                        user.getId(), chuong.getId());
//    }
    
    public boolean canReadChuong(Chuong chuong, NguoiDung user) {
        if (!chuong.isKhoa()) {
            return true;
        }
        if (user == null) {
            return false;
        }
        
        // Nếu là tác giả hoặc người trong cùng nhóm dịch thì được đọc Free chương khóa
        if (isNguoiDangHoacThanhVienNhom(chuong.getTruyen(), user)) {
            return true;
        }
        
        // Nếu không phải nhóm dịch thì check xem đã mua chưa
        return moKhoaChuongRepo.existsByNguoiDung_IdAndChuong_Id(user.getId(), chuong.getId());
    }
    
//    public boolean canManageTapByTruyen(Long truyenId) {
//        NguoiDung user = currentUser();
//        if (user == null) return false;
//        if (isAdmin(user)) return true;
//
//        Truyen truyen = truyenRepo.findById(truyenId).orElse(null);
//        return truyen != null
//            && truyen.getNguoiDang() != null
//            && truyen.getNguoiDang().getId().equals(user.getId());
//    }
    public boolean canManageTapByTruyen(Long truyenId) {
        return canEditTruyen(truyenId);
    }

//    public boolean canManageTap(Long tapId) {
//        NguoiDung user = currentUser();
//        if (user == null) return false;
//        if (isAdmin(user)) return true;
//
//        Tap tap = tapRepo.findById(tapId).orElse(null);
//        return tap != null
//            && tap.getTruyen() != null
//            && tap.getTruyen().getNguoiDang() != null
//            && tap.getTruyen().getNguoiDang().getId().equals(user.getId());
//    }
//    public boolean canAddChuongByTap(Long tapId) {
//        NguoiDung user = currentUser();
//        if (user == null) return false;
//        if (isAdmin(user)) return true;
//
//        Tap tap = tapRepo.findById(tapId).orElse(null);
//        if (tap == null) return false;
//
//        return tap.getTruyen().getNguoiDang().getId()
//                .equals(user.getId());
//    }
    
    public boolean canManageTap(Long tapId) {
        NguoiDung user = currentUser();
        if (user == null) return false;
        if (isAdmin(user)) return true;

        Tap tap = tapRepo.findById(tapId).orElse(null);
        if (tap == null || tap.getTruyen() == null) return false;
        
        return isNguoiDangHoacThanhVienNhom(tap.getTruyen(), user);
    }
    
    public boolean canAddChuongByTap(Long tapId) {
        return canManageTap(tapId);
    }
    
//    @PostMapping("/truyen/{id}/doi-trang-thai")
//    @PreAuthorize("@permissionService.canEditTruyen(#id)")
//    public String doiTrangThai(@PathVariable Long id) {
//        Truyen truyen = truyenService.findById(id);
//
//        if (truyen.getTrangThai() == TrangThaiTruyen.ĐANG_RA) {
//            truyen.setTrangThai(TrangThaiTruyen.HOÀN_THÀNH);
//        } else {
//            truyen.setTrangThai(TrangThaiTruyen.ĐANG_RA);
//        }
//
//        truyenService.save2(truyen);
//        return "redirect:/DustNovel/truyen/" + id;
//    }
   
    @PostMapping("/truyen/{id}/doi-trang-thai")
    @PreAuthorize("@permissionService.canEditTruyen(#id)")
    public String doiTrangThai(@PathVariable Long id) {
        Truyen truyen = truyenService.findById(id);

        if (truyen.getTrangThai() == TrangThaiTruyen.ĐANG_RA) {
            truyen.setTrangThai(TrangThaiTruyen.HOÀN_THÀNH);
        } else {
            truyen.setTrangThai(TrangThaiTruyen.ĐANG_RA);
        }

        truyenService.save2(truyen);
        return "redirect:/DustNovel/truyen/" + id;
    }
    
}
