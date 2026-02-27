package com.fpoly.controller;

import java.util.Collections;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fpoly.model.Truyen;
import com.fpoly.model.enums.LoaiTruyen;
import com.fpoly.repository.TruyenRepository;
import com.fpoly.security.CustomUserDetails;
import com.fpoly.service.TruyenService;

@Controller
@RequestMapping("/DustNovel")
public class TrangChuController {

    @Autowired
    private TruyenService truyenService;
    @Autowired
    private TruyenRepository truyenRepo;
//    @GetMapping("/home")
//    public String home(Model model, Authentication authentication) {
//    	
//        model.addAttribute("title", "Trang chủ");
//        model.addAttribute("content", "home/index.html");
//        List<Truyen> truyenSangTac = truyenRepo.findByLoaiTruyenOrderByChuongMoiNhat(LoaiTruyen.SÁNG_TÁC);
//        model.addAttribute("truyenSangTac", truyenSangTac);
//        List<Truyen> truyenDich = truyenRepo.findByLoaiTruyenOrderByChuongMoiNhat(LoaiTruyen.DỊCH);
//        model.addAttribute("truyenDich", truyenService.getTruyenDich());
//        return "layout/main";
//    }
    
    @GetMapping("/home")
    public String home(Model model) {

        model.addAttribute("title", "Trang chủ");
        model.addAttribute("content", "home/index.html");

        model.addAttribute(
            "truyenSangTac",
            truyenRepo.findByLoaiTruyenOrderByChuongMoiNhat(LoaiTruyen.SÁNG_TÁC)
        );

        model.addAttribute(
            "truyenDich",
            truyenRepo.findByLoaiTruyenOrderByChuongMoiNhat(LoaiTruyen.DỊCH)
        );

        return "layout/main";
    }
    
//    @GetMapping("/truyensangtac")
//    public String sangtac(Model model, Authentication authentication) {
//    	
//        model.addAttribute("title", "Trang chủ");
//        model.addAttribute("content", "home/home-sang-tac.html");
//        model.addAttribute("truyenSangTac", truyenService.getTruyenSangTac());
//        return "layout/main";
//    }
//    
//    @GetMapping("/truyendich")
//    public String dich(Model model, Authentication authentication) {
//
//        model.addAttribute("title", "Trang chủ");
//        model.addAttribute("content", "home/home-dich.html");
//        model.addAttribute("truyenDich", truyenService.getTruyenDich());
//        return "layout/main";
//    }
//    @GetMapping("/truyen/sang-tac")
//    public String trangTruyenSangTac(Model model) {
//        List<Truyen> danhSach = truyenRepo.findByLoaiTruyenOrderByChuongMoiNhatHome(LoaiTruyen.SÁNG_TÁC);
//        
//        model.addAttribute("dsTruyen", danhSach);
//        model.addAttribute("pageTitle", "🔥 Tất Cả Truyện Sáng Tác"); 
//        model.addAttribute("title", "DustNovel | Truyện Sáng Tác");
//        model.addAttribute("content", "truyen/danh-sach"); 
//        
//        return "layout/main";
//    }
//    @GetMapping("/truyen/dich")
//    public String trangTruyenDich(Model model) {
//        List<Truyen> danhSach = truyenRepo.findByLoaiTruyenOrderByChuongMoiNhatHome(LoaiTruyen.DỊCH);
//        
//        model.addAttribute("dsTruyen", danhSach);
//        model.addAttribute("pageTitle", "🌍 Tất Cả Truyện Dịch"); 
//        model.addAttribute("title", "DustNovel | Truyện Dịch");
//        model.addAttribute("content", "truyen/danh-sach"); 
//        
//        return "layout/main";
//    }
    @GetMapping("/truyen/sang-tac")
    public String trangTruyenSangTac(@RequestParam(defaultValue = "1") int page, Model model) {
        Pageable pageable = PageRequest.of(page - 1, 8, Sort.unsorted());
        
        Page<Truyen> danhSach = truyenRepo.findByLoaiTruyenOrderByChuongMoiNhatPage(LoaiTruyen.SÁNG_TÁC, pageable);
        
        model.addAttribute("dsTruyen", danhSach);
        model.addAttribute("pageTitle", "🔥 Tất Cả Truyện Sáng Tác"); 
        model.addAttribute("title", "DustNovel | Truyện Sáng Tác");
        model.addAttribute("content", "truyen/danh-sach"); 
        
        return "layout/main";
    }

    @GetMapping("/truyen/dich")
    public String trangTruyenDich(@RequestParam(defaultValue = "1") int page, Model model) {
        Pageable pageable = PageRequest.of(page - 1, 8);
        
        Page<Truyen> danhSach = truyenRepo.findByLoaiTruyenOrderByChuongMoiNhatPage(LoaiTruyen.DỊCH, pageable);
        
        model.addAttribute("dsTruyen", danhSach);
        model.addAttribute("pageTitle", "🌍 Tất Cả Truyện Dịch"); 
        model.addAttribute("title", "DustNovel | Truyện Dịch");
        model.addAttribute("content", "truyen/danh-sach"); 
        
        return "layout/main";
    }
}
