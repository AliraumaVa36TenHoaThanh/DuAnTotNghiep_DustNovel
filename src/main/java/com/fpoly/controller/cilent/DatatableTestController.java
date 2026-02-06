package com.fpoly.controller.cilent;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import java.util.List;
import com.fpoly.model.Truyen;
import com.fpoly.model.enums.*;
import com.fpoly.repository.TruyenRepository;
import com.fpoly.security.CustomUserDetails;

@Controller
@RequestMapping("/dbu")
public class DatatableTestController {
	@Autowired
	private TruyenRepository truyenRepository;

	@GetMapping("/")
	public String list(Model model) {
		model.addAttribute("content", "/view/client/truyen/index.html");
		return "/layout/cilent_base";
	}

	@GetMapping("/truyen-sang-tac")
	public String truyenSangTac(Model model, Authentication authentication) {

	    CustomUserDetails userDetails =
	            (CustomUserDetails) authentication.getPrincipal();

	    Long userId = userDetails.getNguoiDung().getId();

	    List<Truyen> listTruyen =
	            truyenRepository.findByUserAndLoai(
	                    userId,
	                    LoaiTruyen.SÁNG_TÁC
	            );

	    model.addAttribute("listTruyen", listTruyen);
	    model.addAttribute("content", "view/client/truyen/sangTac");
	    model.addAttribute("title", "Truyện sáng tác");

	    return "layout/cilent_base";
	}


	@GetMapping("/truyen-dich")
	public String truyenDich(Model model, Authentication authentication) {

	    CustomUserDetails userDetails =
	            (CustomUserDetails) authentication.getPrincipal();

	    Long userId = userDetails.getNguoiDung().getId();

	    List<Truyen> listTruyen =
	            truyenRepository.findByUserAndLoai(
	                    userId,
	                    LoaiTruyen.DỊCH   
	            );

	    model.addAttribute("listTruyenDich", listTruyen);
	    model.addAttribute("content", "view/client/truyen/dich");
	    model.addAttribute("title", "Truyện dịch");

	    return "layout/cilent_base";
	}
	
	@GetMapping("/truyen")
	public String truyenAdmin(Model model) {
	    List<Truyen> listTruyen = truyenRepository.findAll(); 
	    model.addAttribute("listTruyenUser", listTruyen);
	    return "layout/admin_base"; 
	}

}
