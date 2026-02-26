package com.fpoly.controller;

import com.fpoly.model.NguoiDung;
import com.fpoly.security.SecurityUtil;
import com.fpoly.service.LichSuDocService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/DustNovel")
public class LichSuDocController {

    @Autowired
    LichSuDocService lichSuDocService;

    @Autowired
    SecurityUtil securityUtil;

    @GetMapping("/lich-su-doc")
    public String lichSuDoc(Model model) {

        NguoiDung user = securityUtil.getCurrentUserFromDB();
        if (user == null) {
            return "redirect:/DustNovel/login";
        }

        model.addAttribute(
            "danhSachLichSu",
            lichSuDocService.layLichSu(user.getId())
        );

        model.addAttribute("content", "truyen/lichsudoc");
        model.addAttribute("title", "Lịch sử đọc");

        return "layout/main";
    }
    
}