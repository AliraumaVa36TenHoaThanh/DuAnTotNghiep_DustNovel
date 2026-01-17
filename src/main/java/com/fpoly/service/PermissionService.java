package com.fpoly.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fpoly.model.Chuong;
import com.fpoly.model.NguoiDung;
import com.fpoly.model.Truyen;
import com.fpoly.repository.ChuongRepository;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.repository.TruyenRepository;
@Service("permissionService")
public class PermissionService {

	 @Autowired
	    private TruyenRepository truyenRepo;

	    @Autowired
	    private ChuongRepository chuongRepo;

	    @Autowired
	    private NguoiDungRepository nguoiDungRepo; 
	    
	    
    private NguoiDung getCurrentUser() {
        Authentication auth =
            SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        String username = auth.getName();
        return nguoiDungRepo.findByTenDangNhap(username).orElse(null);
    }

    
      // TRUYỆN
    
    public boolean canEditTruyen(Long truyenId) {
        NguoiDung user = getCurrentUser();
        if (user == null) return false;

        // ADMIN luôn được
        if (user.getVaiTro().name().equals("ADMIN")) {
            return true;
        }

        Truyen truyen = truyenRepo.findById(truyenId).orElse(null);
        if (truyen == null) return false;

        return truyen.getNguoiDang().getId().equals(user.getId());
    }


       //CHƯƠNG
    public boolean canEditChuong(Long chuongId) {
        NguoiDung user = getCurrentUser();
        if (user == null) return false;

        if (user.getVaiTro().name().equals("ADMIN")) {
            return true;
        }
        
        Chuong chuong = chuongRepo.findById(chuongId).orElse(null);
        if (chuong == null) return false;

        return chuong.getNguoiDang().getId().equals(user.getId());
    }
      // THÊM CHƯƠNG
    public boolean canAddChuong(Long truyenId) {
        return canEditTruyen(truyenId);
    }
}
