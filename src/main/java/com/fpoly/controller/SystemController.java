package com.fpoly.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/DustNovel")
public class SystemController {

    @GetMapping("/system")
    public String system(Model model) {
        model.addAttribute("title", "Hệ thống");
        model.addAttribute("content", "system/index");
        return "layout";
    }
}
