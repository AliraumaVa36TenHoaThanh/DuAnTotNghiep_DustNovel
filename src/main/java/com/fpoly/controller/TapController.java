package com.fpoly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fpoly.model.Tap;
import com.fpoly.model.Truyen;
import com.fpoly.service.ChuongService;
import com.fpoly.service.TapService;
import com.fpoly.service.TruyenService;

@Controller
@RequestMapping("/DustNovel/tap")
public class TapController {
	@Autowired
	TruyenService truyenService;
    @Autowired
    private TapService tapService;
    @Autowired 
    ChuongService chuongService;
    
//    @GetMapping("/{tapId}")
//    public String detail(
//            @PathVariable Long truyenId,
//            @PathVariable Long tapId,
//            Model model
//    ) {
//        Tap tap = tapService.findById(tapId);
//        if (tap == null || !tap.getTruyen().getId().equals(truyenId))
//            return "redirect:/DustNovel/home";
//
//        model.addAttribute("tap", tap);
//        model.addAttribute("dsChuong",chuongService.findByTap(tapId));
//
//        model.addAttribute("content", "tap/detail");
//        return "layout/main";
//    }
    @GetMapping("/{tapId}")
    public String detail(@PathVariable Long tapId, Model model) {
        Tap tap = tapService.findById(tapId);
        if (tap == null) return "redirect:/DustNovel/home";

        model.addAttribute("tap", tap);
        model.addAttribute("dsChuong", chuongService.findByTap(tapId));
        model.addAttribute("content", "tap/detail");
        return "layout/main";
    }

    
//    @PreAuthorize("@permissionService.canManageTapByTruyen(#truyenId)")
//    @GetMapping("/them/{truyenId}")
//    public String showAdd(@PathVariable Long truyenId, Model model) {
//
//        model.addAttribute("truyenId", truyenId);
//        model.addAttribute("content", "tap/add");
//        model.addAttribute("title", "Thêm tập");
//
//        return "layout/main";
//    }
    
    @PreAuthorize("@permissionService.canManageTapByTruyen(#truyenId)")
    @GetMapping("/them/{truyenId}")
    public String showAddTap(@PathVariable Long truyenId, Model model) {

        Truyen truyen = truyenService.findById(truyenId);
        if (truyen == null) return "redirect:/DustNovel/home";

        Tap tap = new Tap();
        tap.setTruyen(truyen);

        model.addAttribute("tap", tap);
        model.addAttribute("content", "tap/addTap");
        model.addAttribute("title", "Thêm tập");

        return "layout/main";
    }

    
    
    @PreAuthorize("@permissionService.canManageTapByTruyen(#truyenId)")
    @PostMapping("/them")
    public String add(@RequestParam Long truyenId,
                      @RequestParam String tenTap) {

        tapService.add(truyenId, tenTap);
        return "redirect:/DustNovel/truyen/" + truyenId;
    }

    /* ========== SỬA TẬP ========== */
    @PreAuthorize("@permissionService.canManageTap(#tapId)")
    @GetMapping("/sua/{tapId}")
    public String showEdit(@PathVariable Long tapId, Model model) {

        Tap tap = tapService.findById(tapId);
        if (tap == null) return "redirect:/DustNovel/home";

        model.addAttribute("tap", tap);
        model.addAttribute("content", "tap/edit");
        model.addAttribute("title", "Sửa tập");

        return "layout/main";
    }

    @PreAuthorize("@permissionService.canManageTap(#tapId)")
    @PostMapping("/sua")
    public String edit(@RequestParam Long tapId,
                       @RequestParam String tenTap) {

        Tap tap = tapService.update(tapId, tenTap);
        return "redirect:/DustNovel/truyen/" + tap.getTruyen().getId();
    }

    /* ========== XOÁ TẬP ========== */
    @PreAuthorize("@permissionService.canManageTap(#tapId)")
    @PostMapping("/xoa/{tapId}")
    public String delete(@PathVariable Long tapId) {

        Tap tap = tapService.findById(tapId);
        Long truyenId = tap.getTruyen().getId();

        tapService.delete(tapId);
        return "redirect:/DustNovel/truyen/" + truyenId;
    }
   

}

