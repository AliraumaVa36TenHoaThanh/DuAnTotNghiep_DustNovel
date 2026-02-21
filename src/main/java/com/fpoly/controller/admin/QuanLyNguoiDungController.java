package com.fpoly.controller.admin;

import com.fpoly.AdminService.AdminUserService;
import com.fpoly.model.NguoiDung;
import com.fpoly.repository.NguoiDungRepository;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.fpoly.model.Truyen;
import com.fpoly.repository.TruyenRepository;
import com.fpoly.security.CustomUserDetails;
@Controller
@RequestMapping("/dba")
public class QuanLyNguoiDungController {
	@Autowired
	TruyenRepository truyenRepo;
	@GetMapping("/user")
	public String list(Model model) {
		model.addAttribute("content", "/view/admin/user/index.html");
		return "/layout/admin_base";
	}
	
	@GetMapping("/user/truyen")
	public String truyenAdmin(Model model, Authentication authentication) {

	    CustomUserDetails userDetails =
	            (CustomUserDetails) authentication.getPrincipal();
	    List<Truyen> listTruyen = truyenRepo.findAll();
	    model.addAttribute("listTruyenUser", listTruyen);
	    model.addAttribute("content", "view/admin/truyen/quanLyTruyen");
	    model.addAttribute("title", "Quản Lý Truyện");
	    return "layout/admin_base";
	}

}
