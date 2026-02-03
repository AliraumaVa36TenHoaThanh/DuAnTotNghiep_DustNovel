package com.fpoly.controller.admin;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
@Controller
@RequestMapping("/dba")
public class QuanLyNguoiDungController {
	@GetMapping("/")
	public String list(Model model) {
		model.addAttribute("content", "/view/admin/user/index.html");
		return "/layout/admin_base";
	}
    
}
