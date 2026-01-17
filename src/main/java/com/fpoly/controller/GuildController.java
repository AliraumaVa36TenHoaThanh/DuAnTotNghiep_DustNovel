package com.fpoly.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/DustNovel/guild")
public class GuildController {

    @GetMapping
    public String guild(Model model) {

        model.addAttribute("title", "DustNovel | Guild");
        model.addAttribute("content", "truyen/guild.html");

        return "layout/main";
    }
}
