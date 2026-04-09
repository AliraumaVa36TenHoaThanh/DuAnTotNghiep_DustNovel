package com.fpoly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fpoly.model.NguoiDung;
import com.fpoly.model.Truyen;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.service.TruyenService; // 👉 Import Service vào

@Controller
@RequestMapping("/DustNovel")
public class HoSoController {

    @Autowired
    private NguoiDungRepository nguoiDungRepo;

    // 👉 DÙNG SERVICE THAY VÌ REPOSITORY
    @Autowired
    private TruyenService truyenService; 

    @GetMapping("/ho-so/{id}")
    public String trangCaNhanPublic(@PathVariable Long id, 
                                    @RequestParam(defaultValue = "0") int page, 
                                    Model model) {
        
        // 1. Tìm user
        NguoiDung tacGia = nguoiDungRepo.findById(id).orElse(null);
        if (tacGia == null) {
            return "redirect:/DustNovel/home"; 
        }

        // 2. Lấy thống kê QUA TẦNG SERVICE
        long tongTruyen = truyenService.demTongSoTruyenCuaTacGia(id);
        long tongLike = truyenService.tinhTongLikeCuaTacGia(id);

        // 3. Phân trang truyện QUA TẦNG SERVICE
        Pageable pageable = PageRequest.of(page, 8);
        Page<Truyen> pageTruyen = truyenService.layTruyenCuaTacGia(id, pageable);

        // 4. Đẩy dữ liệu ra View
        model.addAttribute("tacGia", tacGia);
        model.addAttribute("tongTruyen", tongTruyen);
        model.addAttribute("tongLike", tongLike);
        
        model.addAttribute("pageTruyen", pageTruyen);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageTruyen.getTotalPages());

        model.addAttribute("content", "user/ho-so-public"); 
        model.addAttribute("title", "Hồ sơ: " + tacGia.getTenDangNhap());

        return "layout/main";
    }
}