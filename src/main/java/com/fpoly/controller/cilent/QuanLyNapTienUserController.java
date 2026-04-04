package com.fpoly.controller.cilent;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fpoly.model.NapTien;
import com.fpoly.model.NguoiDung;
import com.fpoly.repository.NapTienRepository;
import com.fpoly.repository.NguoiDungRepository;

@Controller
@RequestMapping("/dbu")
public class QuanLyNapTienUserController {
	@Autowired
    NapTienRepository napTienRepo;

    @Autowired
    NguoiDungRepository nguoiDungRepo;
    
    
	@GetMapping("/nap-tien")
    public String listNapTien(Authentication authentication,Model model) {
		
		String username = authentication.getName();
		
		NguoiDung user = nguoiDungRepo.findByTenDangNhap(username).orElse(null);
		
		if(user == null) {
			return "redirect:/login";
		}

        List<NapTien> listNapTienUser = napTienRepo.findByNguoiDungId(user.getId());

        model.addAttribute("listNapTienUser", listNapTienUser);
        model.addAttribute("content", "view/client/nap/user-nap-tien");
        model.addAttribute("title", "Quản Lý Nạp Tiền");

        return "layout/cilent_base";
    }
}
