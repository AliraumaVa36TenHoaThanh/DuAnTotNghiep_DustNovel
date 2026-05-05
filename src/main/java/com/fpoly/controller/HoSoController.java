package com.fpoly.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fpoly.model.NguoiDung;
import com.fpoly.model.Truyen;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.repository.TruyenRepository;
import com.fpoly.service.TruyenService; // 👉 Import Service vào

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/DustNovel")
public class HoSoController {

    @Autowired
    private NguoiDungRepository nguoiDungRepo;
    @Autowired
    private TruyenService truyenService; 
    @Autowired
    private TruyenRepository truyenRepo;
    @GetMapping("/ho-so/{id}")
    public String trangCaNhanPublic(@PathVariable Long id, 
                                    @RequestParam(defaultValue = "0") int page, 
                                    Model model) {
        
        NguoiDung tacGia = nguoiDungRepo.findById(id).orElse(null);
        if (tacGia == null) {
            return "redirect:/DustNovel/home"; 
        }

        long tongTruyen = truyenService.demTongSoTruyenCuaTacGia(id);
        long tongLike = truyenService.tinhTongLikeCuaTacGia(id);
        Pageable pageable = PageRequest.of(page, 8);
        Page<Truyen> pageTruyen = truyenService.layTruyenCuaTacGia(id, pageable);
        List<Truyen> tatCaTruyen = truyenRepo.findByNguoiDangId(tacGia.getId());
        model.addAttribute("tacGia", tacGia);
        model.addAttribute("tongTruyen", tongTruyen);
        model.addAttribute("tongLike", tongLike);
        model.addAttribute("tatCaTruyen", tatCaTruyen);
        model.addAttribute("pageTruyen", pageTruyen);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageTruyen.getTotalPages());

        model.addAttribute("content", "user/ho-so-public"); 
        model.addAttribute("title", "Hồ sơ: " + tacGia.getTenDangNhap());

        return "layout/main";
    }
    
    @PostMapping("/truyen/xoa-hang-loat")
    public String xoaTruyenHangLoat(@RequestParam(value = "truyenIds", required = false) List<Long> truyenIds, HttpServletRequest request) {

        String referer = request.getHeader("Referer");
        if (truyenIds != null && !truyenIds.isEmpty()) {
            truyenRepo.deleteAllById(truyenIds);
        }
        return "redirect:" + (referer != null ? referer : "/DustNovel/home");
    }
}