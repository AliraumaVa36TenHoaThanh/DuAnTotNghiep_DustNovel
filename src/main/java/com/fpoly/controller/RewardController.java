package com.fpoly.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/DustNovel")
public class RewardController {

    @GetMapping("/ma-thuong")
    public String maThuong() {
        return "user/ma_thuong";
    }
}

