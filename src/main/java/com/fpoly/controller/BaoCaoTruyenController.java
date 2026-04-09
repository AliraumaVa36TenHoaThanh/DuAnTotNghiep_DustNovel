package com.fpoly.controller;

import com.fpoly.model.NguoiDung;
import com.fpoly.model.Truyen;
import com.fpoly.repository.TruyenRepository;
import com.fpoly.security.SecurityUtil;
import com.fpoly.service.BaoCaoTruyenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/DustNovel/bao-cao")
public class BaoCaoTruyenController {

    @Autowired BaoCaoTruyenService baoCaoService;
    @Autowired TruyenRepository truyenRepo;
    @Autowired SecurityUtil securityUtil;

    @PostMapping("/gui")
    public String guiBaoCao(@RequestParam("truyenId") Long truyenId,
                            @RequestParam("lyDo") String lyDo,
                            @RequestParam(value = "moTaChiTiet", required = false) String moTaChiTiet,
                            HttpServletRequest request) {
        
        NguoiDung user = securityUtil.getCurrentUserFromDB();
        if (user == null) {
            return "redirect:/DustNovel/login"; // Yêu cầu đăng nhập
        }

        Truyen truyen = truyenRepo.findById(truyenId).orElse(null);
        if (truyen != null) {
            baoCaoService.guiBaoCao(user, truyen, lyDo, moTaChiTiet);
        }

        // Quay lại đúng trang truyện đó
        return "redirect:" + request.getHeader("Referer");
    }
}