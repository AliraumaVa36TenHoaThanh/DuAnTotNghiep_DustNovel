package com.fpoly.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fpoly.model.MaThuong;
import com.fpoly.service.MaThuongService;

@Controller
@RequestMapping("/admin/ma-thuong")
public class MaThuongAdminController {

    @Autowired
    private MaThuongService service;

    // CLICK MENU -> HIỆN FORM + BẢNG
    @GetMapping
    public String page(Model model) {
        model.addAttribute("list", service.findAll());
        model.addAttribute("ma", new MaThuong()); // form object
        model.addAttribute("content", "view/admin/ma-thuong/index");
        return "layout/admin_base";
    }

    // SUBMIT FORM
    @PostMapping("/add")
    public String add(@ModelAttribute MaThuong ma) {
        service.create(ma);
        return "redirect:/admin/ma-thuong"; // reload bảng
    }
}