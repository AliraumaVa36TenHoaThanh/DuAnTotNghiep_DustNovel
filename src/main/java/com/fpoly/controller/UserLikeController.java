package com.fpoly.controller;

import com.fpoly.model.NguoiDung;
import com.fpoly.security.SecurityUtil;
import com.fpoly.service.LikeTruyenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserLikeController {

    @Autowired
    LikeTruyenService likeService;

    @Autowired
    SecurityUtil securityUtil;

    @GetMapping("/user/truyen-da-thich")
    public String truyenDaThich(Model model) {

        NguoiDung user = securityUtil.getCurrentUserFromDB();
        if (user == null) return "redirect:/login";
        model.addAttribute("list", likeService.getTruyenDaLike(user));

        model.addAttribute("content", "view/client/truyen/truyen_da_like");
        return "layout/cilent_base";
    }
}