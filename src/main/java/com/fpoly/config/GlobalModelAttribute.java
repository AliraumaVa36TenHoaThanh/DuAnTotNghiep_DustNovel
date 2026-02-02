package com.fpoly.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.fpoly.model.NguoiDung;
import com.fpoly.repository.NguoiDungRepository;

@ControllerAdvice
public class GlobalModelAttribute {

    @Autowired
    private NguoiDungRepository nguoiDungRepo;

    @ModelAttribute("user")
    public NguoiDung addUserToModel(Authentication auth) {
        if (auth == null) return null;

        return nguoiDungRepo
                .findByTenDangNhap(auth.getName())
                .orElse(null);
    }
}
