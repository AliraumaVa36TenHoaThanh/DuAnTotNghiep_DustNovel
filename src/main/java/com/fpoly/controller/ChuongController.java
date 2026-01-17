package com.fpoly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.fpoly.model.Chuong;
import com.fpoly.model.NguoiDung;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.security.CustomUserDetails;
import com.fpoly.service.ChuongService;
import com.fpoly.service.TruyenService;

@Controller
@RequestMapping("/DustNovel/chuong")
public class ChuongController {

	@Autowired
	ChuongService chuongService;
	@Autowired
	 TruyenService truyenService;
	@Autowired
	 NguoiDungRepository nguoiDungRepo;



    @GetMapping("/{id}")
    public String read(@PathVariable Long id, Model model) {
        Chuong chuong = chuongService.findById(id);
        
        model.addAttribute("chuong", chuong);
        model.addAttribute("chuongTruoc", chuongService.chuongTruoc(chuong));
        model.addAttribute("chuongSau", chuongService.chuongSau(chuong));
        model.addAttribute("content", "truyen/chapter");
        model.addAttribute("title", chuong.getTieuDe());
        return "layout/main";
    }
    @PreAuthorize("@permissionService.canAddChuong(#truyenId)")
    @GetMapping("/them/{truyenId}")
    public String showAddForm(@PathVariable Long truyenId, Model model) {
        Chuong chuong = new Chuong();
        chuong.setTruyen(truyenService.findById(truyenId));
        model.addAttribute("chuong", chuong);
        model.addAttribute("content", "chuong/add");
        model.addAttribute("title", "Thêm chương");
        return "layout/main";
    }
    
    @PreAuthorize("@permissionService.canAddChuong(#chuong.truyen.id)")
    @PostMapping("/them")
    public String add(@ModelAttribute Chuong chuong) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails cud = (CustomUserDetails) auth.getPrincipal();
        NguoiDung user = cud.getUser();

        chuong.setNguoiDang(user);

        int nextSo = chuongService.getNextSoChuong(
                chuong.getTruyen().getId()
        );
        chuong.setSoChuong(nextSo);

        chuongService.save(chuong);
        return "redirect:/DustNovel/truyen/" + chuong.getTruyen().getId();
    }

}
