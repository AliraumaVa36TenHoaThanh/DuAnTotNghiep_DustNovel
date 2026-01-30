package com.fpoly.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.fpoly.model.NguoiDung;
import com.fpoly.repository.NguoiDungRepository;

@Component("securityUtil")
public class SecurityUtil {
	@Autowired
	NguoiDungRepository nguoiDungRepo;
    public NguoiDung getCurrentUser() {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getNguoiDung();
        }
        return null;
    }
//    public NguoiDung getCurrentUserFromDB() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//
//        if (auth == null || !auth.isAuthenticated()
//            || auth instanceof AnonymousAuthenticationToken) {
//            return null;
//        }
//
//        CustomUserDetails cud = (CustomUserDetails) auth.getPrincipal();
//        return nguoiDungRepo.findById(cud.getId()).orElse(null);
//    }
    
    public NguoiDung getCurrentUserNew() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null|| !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        CustomUserDetails cud = (CustomUserDetails) auth.getPrincipal();

        return nguoiDungRepo.findById(cud.getId()).orElse(null);
    }
    
    public NguoiDung getCurrentUserFromDB() {
        Authentication auth = SecurityContextHolder
                .getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
            || auth.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        String username = auth.getName();
        return nguoiDungRepo.findByTenDangNhap(username).orElse(null);
    }
}

