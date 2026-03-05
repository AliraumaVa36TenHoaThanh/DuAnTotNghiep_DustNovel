package com.fpoly.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.fpoly.model.NguoiDung;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.service.BookmarkService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/DustNovel/bookmark")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final NguoiDungRepository nguoiDungRepo;

    @PostMapping("/save")
    @ResponseBody
    public String saveBookmark(
            @RequestParam Long chuongId,
            @RequestParam Integer viTri,
            @RequestParam Float phanTram,
            @RequestParam String text,
            Principal principal) {

        NguoiDung user = nguoiDungRepo
                .findByTenDangNhap(principal.getName())
                .orElseThrow();

        bookmarkService.saveBookmark(
                user.getId(),
                chuongId,
                viTri,
                phanTram,
                text
        );

        return "ok";
    }

    @GetMapping
    public String bookmarkPage(Model model, Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        NguoiDung user = nguoiDungRepo
                .findByTenDangNhap(principal.getName())
                .orElseThrow();

        model.addAttribute(
                "dsBookmark",
                bookmarkService.getUserBookmarks(user.getId())
        );

     // THÊM DÒNG NÀY ĐỂ TRUYỀN TÊN VIEW CHO LAYOUT CHÍNH
        model.addAttribute("content", "chuong/bookmark"); 
        
        // TRẢ VỀ FILE LAYOUT CHÍNH (Chứ không phải trả thẳng về file bookmark nữa)
        return "layout/main";
    }

    @PostMapping("/delete/{id}")
    public String deleteBookmark(@PathVariable Long id) {

        bookmarkService.deleteBookmark(id);

        return "redirect:/DustNovel/bookmark";
    }
}