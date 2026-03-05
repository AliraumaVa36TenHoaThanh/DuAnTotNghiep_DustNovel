package com.fpoly.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.fpoly.model.NhomDich;
import com.fpoly.repository.NhomDichRepository;

@Controller
@RequestMapping("/dba/nhom-dich")
public class QuanLyNhomDichController {

    @Autowired
    private NhomDichRepository nhomDichRepository;

    // =============================
    // LIST NHÓM DỊCH
    // =============================
    @GetMapping
    public String listNhomDich(Model model) {

        List<NhomDich> listNhom = nhomDichRepository.findAll();

        model.addAttribute("listNhomDich", listNhom);
        model.addAttribute("content", "/view/admin/nhomDich/QuanLyNhomDich");
        model.addAttribute("title", "Quản Lý Nhóm Dịch");

        return "/layout/admin_base";
    }

 // =============================
 // CHI TIẾT NHÓM
 // =============================
 @GetMapping("/chi-tiet/{id}")
 public String chiTiet(@PathVariable Long id, Model model) {

     NhomDich nhom = nhomDichRepository.findById(id)
             .orElseThrow(() -> new RuntimeException("Không tìm thấy nhóm"));

     model.addAttribute("nhom", nhom);
     model.addAttribute("laTruongNhom", false); // tránh lỗi thymeleaf null boolean
     model.addAttribute("content", "congcu/index-guild"); // bỏ dấu /
     model.addAttribute("title", "Chi Tiết Nhóm Dịch");

     return "layout/admin_base"; // bỏ dấu /
 }
    
 // ================= FORM SỬA =================
    @GetMapping("/sua/{id}")
    public String formSua(@PathVariable Long id, Model model) {

        NhomDich nhom = nhomDichRepository.findById(id).orElse(null);

        model.addAttribute("nhom", nhom);
        model.addAttribute("content", "/view/admin/nhomDich/SuaNhom");
        model.addAttribute("title", "Sửa Nhóm Dịch");

        return "/layout/admin_base";
    }

    // ================= UPDATE =================
    @PostMapping("/sua/{id}")
    public String capNhat(@PathVariable Long id,
                          @ModelAttribute NhomDich nhomForm) {

        NhomDich nhom = nhomDichRepository.findById(id).orElse(null);

        if (nhom != null) {
            nhom.setTenNhom(nhomForm.getTenNhom());
            nhom.setTrangThai(nhomForm.getTrangThai());
            nhomDichRepository.save(nhom);
        }

        return "redirect:/dba/nhom-dich";
    }

    // =============================
    // XÓA NHÓM
    // =============================
    @GetMapping("/xoa/{id}")
    public String xoaNhom(@PathVariable Long id) {

        nhomDichRepository.deleteById(id);

        return "redirect:/dba/nhom-dich";
    }

}