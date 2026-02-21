package com.fpoly.controller.admin;

import com.fpoly.AdminService.AdminUserService;
import com.fpoly.model.NguoiDung;
import com.fpoly.repository.NguoiDungRepository;

import org.springframework.beans.factory.annotation.Autowired;
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

    // =============================
    // LIST USER
    // =============================
	    @GetMapping("/user")
    public String list(Model model) {

        model.addAttribute("users", adminUserService.getAllUsers());
        model.addAttribute("content", "/view/admin/user/index");

        return "/layout/admin_base";
    }

    // =============================
    // FORM EDIT
    // =============================
    @GetMapping("/user/editUser/{id}")
    public String editUser(@PathVariable Long id, Model model){

        model.addAttribute("user",
                repo.findById(id).orElseThrow());

        model.addAttribute("content",
                "/view/admin/user/editUser");

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
}
