package com.fpoly.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.fpoly.model.TheLoai;
import com.fpoly.repository.TheLoaiRepository;
import com.fpoly.service.NapTienService;
import com.fpoly.service.NguoiDungService;
import com.fpoly.service.PhieuThuongService;
import com.fpoly.service.TheLoaiService;

@ControllerAdvice
public class GlobalController {

    @Autowired
    private TheLoaiService theLoaiService;
    
    @Autowired
    private NapTienService napTienService;
    @Autowired
    private PhieuThuongService phieuThuongService;
    @Autowired
    private NguoiDungService nguoiDungService;
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
    @ModelAttribute("userPhieuThuong")
    public Long userPhieuThuong(Authentication auth) {
        if (auth == null) return 0L;

        var user = nguoiDungService.findByTenDangNhap(auth.getName());

        Long phieu = phieuThuongService.getSoLuong(user.getId());
        return phieu == null ? 0L : phieu;
    }
}
