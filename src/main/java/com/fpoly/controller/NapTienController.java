package com.fpoly.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/DustNovel/nap-tien")
public class NapTienController {

    @GetMapping
    public String napTien(Model model) {

        model.addAttribute("title", "DustNovel | Nạp tiền");
        model.addAttribute("content", "truyen/nap-tien");

        return "layout/main";
    }
}
