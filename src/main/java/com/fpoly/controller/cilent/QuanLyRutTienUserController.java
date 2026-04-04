package com.fpoly.controller.cilent;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fpoly.model.NguoiDung;
import com.fpoly.model.RutTien;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.repository.RutTienRepository;
@Controller
@RequestMapping("/dbu")
public class QuanLyRutTienUserController {
	@Autowired
    RutTienRepository rutTienRepo;

    @Autowired
    NguoiDungRepository nguoiDungRepo;
    
    
    @GetMapping("/rut-tien")
    public String listRutTien(Authentication authentication, Model model) {

        String username = authentication.getName();
        Optional<NguoiDung> optionalUser = nguoiDungRepo.findByTenDangNhap(username);

        if (optionalUser.isEmpty()) {
            return "redirect:/login"; 
        }

        NguoiDung user = optionalUser.get();

        List<RutTien> listRutTienUser = rutTienRepo.findByNguoiDungId(user.getId());

        model.addAttribute("listRutTienUser", listRutTienUser);
        model.addAttribute("content", "view/client/nap/user-rut-tien");
        model.addAttribute("title", "Quản Lý Rút Tiền");

        return "layout/admin_base";
    }
}
