package com.fpoly.controller;

import com.fpoly.service.BaoCaoTruyenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dba/bao-cao")
@PreAuthorize("hasRole('ADMIN')") 
public class AdminBaoCaoController {

    @Autowired
    private BaoCaoTruyenService baoCaoService;

    @GetMapping
    public String index(@RequestParam(required = false) String keyword,
                        @RequestParam(required = false) String lyDo,
                        Model model) {
//    	model.addAttribute("reports", baoCaoService.layDanhSachBaoCao("", ""));
        model.addAttribute("reports", baoCaoService.layDanhSachBaoCao(keyword, lyDo));
        model.addAttribute("keyword", keyword);
        model.addAttribute("lyDo", lyDo);
        
        model.addAttribute("content", "view/admin/BaoCaoTruyen/BaoCaoTruyen");

        return "layout/admin_base";
    }

    @PostMapping("/xu-ly/{id}")
    public String xuLy(@PathVariable Long id) {
        baoCaoService.xuLyBaoCao(id);
        return "redirect:/dba/bao-cao";
    }

    @PostMapping("/xoa/{id}")
    public String xoa(@PathVariable Long id) {
        baoCaoService.xoaBaoCao(id);
        return "redirect:/dba/bao-cao";
    }
}