package com.fpoly.controller.admin;


import com.fpoly.AdminService.AdminUserService;
import com.fpoly.model.NguoiDung;
import com.fpoly.model.Truyen;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.repository.TruyenRepository;
import com.fpoly.security.CustomUserDetails;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dba")
public class QuanLyNguoiDungController {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private NguoiDungRepository repo;

    @Autowired
    private TruyenRepository truyenRepo;

    // =============================
    // LIST USER
    // =============================
    @GetMapping("/user")
    public String list(Model model) {

        model.addAttribute("users", adminUserService.getAllUsers());
        model.addAttribute("content", "/view/admin/user/index");
        model.addAttribute("title", "Quản Lý Người Dùng");

        return "/layout/admin_base";
    }

    // =============================
    // FORM EDIT USER
    // =============================
    @GetMapping("/user/editUser/{id}")
    public String editUser(@PathVariable Long id, Model model){

        model.addAttribute("user",
                repo.findById(id).orElseThrow());

        model.addAttribute("content",
                "/view/admin/user/editUser");
        model.addAttribute("title", "Chỉnh Sửa Người Dùng");

        return "/layout/admin_base";
    }

    // =============================
    // UPDATE USER
    // =============================
    @PostMapping("/user/update")
    public String updateUser(@ModelAttribute NguoiDung user) {

        adminUserService.saveUser(user);

        return "redirect:/dba/user";
    }

    // =============================
    // DELETE USER
    // =============================
    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Long id) {

        adminUserService.deleteUser(id);

        return "redirect:/dba/user";
    }

    // =============================
    // QUẢN LÝ TRUYỆN
    // =============================
    @GetMapping("/user/truyen")
    public String truyenAdmin(Model model, Authentication authentication) {

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        List<Truyen> listTruyen = truyenRepo.findAll();

        model.addAttribute("listTruyenUser", listTruyen);
        model.addAttribute("content", "/view/admin/truyen/quanLyTruyen");
        model.addAttribute("title", "Quản Lý Truyện");

        return "/layout/admin_base";
    }
}
