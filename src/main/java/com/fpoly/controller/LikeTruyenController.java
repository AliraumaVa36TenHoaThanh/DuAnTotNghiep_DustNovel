package com.fpoly.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.fpoly.model.NguoiDung;
import com.fpoly.security.SecurityUtil;
import com.fpoly.service.LikeTruyenService;

@Controller
@RequestMapping("/DustNovel/like")
public class LikeTruyenController {

    @Autowired
    LikeTruyenService service;

    @Autowired
    SecurityUtil securityUtil;

    @PostMapping("/{id}")
    @ResponseBody
    public Map<String, Object> like(@PathVariable Long id) {

        NguoiDung user = securityUtil.getCurrentUserFromDB();
        Map<String, Object> res = new HashMap<>();

        if (user == null) {
            res.put("error", "NOT_LOGIN");
            return res;
        }

        long tongLike = service.toggleLike(user.getId(), id);
        res.put("tongLike", tongLike);

        return res;
    }
    
    @GetMapping("/user/list-like")
    public String listLike(org.springframework.ui.Model model) {
        NguoiDung user = securityUtil.getCurrentUserFromDB();
        
        // Nếu chưa đăng nhập thì đá văng ra trang login
        if (user == null) {
            return "redirect:/DustNovel/login";
        }

        // Lấy danh sách truyện đã like ném ra ngoài View
        java.util.List<com.fpoly.model.Truyen> dsTruyenLike = service.getTruyenDaLike(user);
        model.addAttribute("dsTruyenLike", dsTruyenLike);
        
        model.addAttribute("title", "DustNovel | Truyện đã thích");
        // Giả sử ông tạo file HTML trong thư mục templates/user/list-like.html
        model.addAttribute("content", "user/list-like"); 

        return "layout/main";
    }
}
