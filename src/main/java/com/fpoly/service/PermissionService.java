package com.fpoly.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.model.Chuong;
import com.fpoly.model.NguoiDung;
import com.fpoly.model.Truyen;
import com.fpoly.model.enums.VaiTro;
import com.fpoly.repository.ChuongRepository;
import com.fpoly.repository.MoKhoaChuongRepository;
import com.fpoly.repository.TruyenRepository;
import com.fpoly.security.SecurityUtil;

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

  
    private NguoiDung currentUser() {
        return securityUtil.getCurrentUserFromDB();
    }

    private boolean isAdmin(NguoiDung user) {
        return user != null && user.getVaiTro() == VaiTro.ADMIN;
    }

   
    public boolean canEditTruyen(Long truyenId) {
        NguoiDung user = currentUser();
        if (user == null) return false;
        if (isAdmin(user)) return true;

        Truyen truyen = truyenRepo.findById(truyenId).orElse(null);
        return truyen != null &&
               truyen.getNguoiDang() != null &&
               truyen.getNguoiDang().getId().equals(user.getId());
    }

    public boolean canDeleteTruyen(Long truyenId) {
        return canEditTruyen(truyenId);
    }

    public boolean canAddChuong(Long truyenId) {
        return canEditTruyen(truyenId);
    }

  
    public boolean canEditChuong(Long chuongId) {
        NguoiDung user = currentUser();
        if (user == null) return false;
        if (isAdmin(user)) return true;

        Chuong chuong = chuongRepo.findById(chuongId).orElse(null);
        return chuong != null &&
               chuong.getNguoiDang() != null &&
               chuong.getNguoiDang().getId().equals(user.getId());
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
    
    public boolean canReadChuong(Chuong chuong, NguoiDung user) {

        if (!chuong.isKhoa()) {
            return true;
        }

        if (user == null) {
            return false;
        }

        if (chuong.getNguoiDang() != null
            && chuong.getNguoiDang().getId().equals(user.getId())) {
            return true;
        }
        
        return moKhoaChuongRepo
                .existsByNguoiDung_IdAndChuong_Id(
                        user.getId(), chuong.getId());
    }
}
