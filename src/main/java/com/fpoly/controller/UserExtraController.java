package com.fpoly.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/DustNovel/user")
public class UserExtraController {

    @GetMapping("/history")
    public String history(Model model) {
        model.addAttribute("title", "Lịch sử đọc");
        model.addAttribute("content", "user/history");
        return "layout/main";
    }

    @GetMapping("/bookmark")
    public String bookmark(Model model) {
        model.addAttribute("title", "Đánh dấu");
        model.addAttribute("content", "user/bookmark");
        return "layout/main";
    }

    @GetMapping("/message")
    public String message(Model model) {
        model.addAttribute("title", "Tin nhắn");
        model.addAttribute("content", "user/message");
        return "layout/main";
    }
}

