package com.fpoly.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fpoly.security.CustomUserDetails;
import com.fpoly.service.TruyenService;

@Controller
@RequestMapping("/DustNovel")
public class TrangChuController {

    @Autowired
    private TruyenService truyenService;

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {

        model.addAttribute("title", "Trang chủ");
        model.addAttribute("content", "home/index.html");
        model.addAttribute("truyenSangTac", truyenService.getTruyenSangTac());
        model.addAttribute("truyenDich", truyenService.getTruyenDich());
        return "layout/main";
    }
}
