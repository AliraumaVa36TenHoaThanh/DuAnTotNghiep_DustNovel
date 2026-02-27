package com.fpoly.controller;

import com.fpoly.model.BinhLuan;
import com.fpoly.security.CustomUserDetails;
import com.fpoly.service.BinhLuanService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/DustNovel/binh-luan")
public class BinhLuanController {

    private final BinhLuanService binhLuanService;

    public BinhLuanController(BinhLuanService binhLuanService) {
        this.binhLuanService = binhLuanService;
    }

    // ================= THÊM =================
    @PostMapping("/add")
    public String addComment(@RequestParam Long truyenId,
                             @RequestParam String noiDung,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return "redirect:/DustNovel/login";
        }

        binhLuanService.save(truyenId, userDetails.getId(), noiDung);

        return "redirect:/DustNovel/truyen/" + truyenId;
    }
    @PostMapping("/chuong/add")
    public String commentChuong(@RequestParam Long chuongId,
                                @RequestParam String noiDung,
                                @AuthenticationPrincipal CustomUserDetails user) {

        binhLuanService.save(chuongId, user.getId(), noiDung);

        return "redirect:/DustNovel/chuong/" + chuongId;
    }
    // ================= REPLY =================
    @PostMapping("/reply")
    public String replyComment(@RequestParam Long truyenId,
                               @RequestParam Long parentId,
                               @RequestParam String noiDung,
                               @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return "redirect:/DustNovel/login";
        }

        binhLuanService.reply(truyenId,
                              userDetails.getId(),
                              parentId,
                              noiDung);

        return "redirect:/DustNovel/truyen/" + truyenId;
    }
    
    // ================= delete =================
    @PostMapping("/delete")
    public String deleteComment(@RequestParam Long commentId,
                                @RequestParam Long truyenId,
                                @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return "redirect:/DustNovel/login";
        }

        Long currentUserId = userDetails.getId();

        boolean isAdmin = userDetails.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        binhLuanService.delete(commentId, currentUserId, isAdmin);

        return "redirect:/DustNovel/truyen/" + truyenId;
    }

    // ================= UPDATE =================
    @PostMapping("/update")
    public String updateComment(@RequestParam Long commentId,
                                @RequestParam Long truyenId,
                                @RequestParam String noiDung,
                                @AuthenticationPrincipal CustomUserDetails user) {

        boolean isAdmin = user.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        binhLuanService.update(commentId,
                               noiDung,
                               user.getId(),
                               isAdmin);

        return "redirect:/DustNovel/truyen/" + truyenId;
    }
    
    
}
