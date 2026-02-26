package com.fpoly.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.fpoly.model.TheLoai;
import com.fpoly.repository.TheLoaiRepository;
import com.fpoly.service.NapTienService;
import com.fpoly.service.TheLoaiService;
import com.fpoly.model.NguoiDung;
import com.fpoly.model.Truyen;
import com.fpoly.service.LikeTruyenService;
import jakarta.servlet.http.HttpSession;

import java.util.List;
@ControllerAdvice
public class GlobalController {

    @Autowired
    private TheLoaiService theLoaiService;
    
    @Autowired
    private NapTienService napTienService;

    @ModelAttribute("theLoais")
    public List<TheLoai> loadTheLoai1() {
        return theLoaiService.getAllTheLoai();
    }
    
    @Autowired
    private TheLoaiRepository theLoaiRepository;

    @ModelAttribute("theLoais")
    public List<TheLoai> loadTheLoai() {
        return theLoaiRepository.findAll();
    }

    @ModelAttribute("userToken")
    public Long userToken(Authentication auth) {
        if (auth == null) return 0L;

        var user = napTienService.getByTenDangNhap(auth.getName());
      
        return user != null ? user.getToken() : 0L;
    }
    @Autowired
    LikeTruyenService likeService;

    @ModelAttribute("truyenDaLike")
    public List<Truyen> getTruyenDaLike(Authentication auth) {
        if (auth == null) return List.of();

        NguoiDung user = napTienService.getByTenDangNhap(auth.getName());
        if (user == null) return List.of();

        return likeService.getTruyenDaLike(user);
    }
}
