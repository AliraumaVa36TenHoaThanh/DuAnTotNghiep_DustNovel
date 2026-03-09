package com.fpoly.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;

import com.fpoly.model.NguoiDung;
import com.fpoly.model.Truyen;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.repository.TruyenRepository;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/quang-cao")
public class QuangCaoController {	
	@Autowired
    TruyenRepository truyenRepository;
	@Autowired
    NguoiDungRepository nguoiDungRepository;
	
	@GetMapping("/")
    public String quangCao(Authentication authentication,Model model) {
		
		// Lấy username của người đang đăng nhập từ Spring Security
		String username = authentication.getName();
        
		// Tìm người dùng trong database theo tên đăng nhập
	    Optional<NguoiDung> userOpt = nguoiDungRepository.findByTenDangNhap(username);
	    
	    // Nếu không tìm thấy user thì chuyển về trang login
	    if(userOpt.isEmpty()){
	        return "redirect:/login";
	    }
	    // Lấy object NguoiDung từ Optional
	    NguoiDung user = userOpt.get();
	    
	    // Lấy danh sách truyện mà user này đã đăng (theo id của người đăng)
	    List<Truyen> listTruyen = truyenRepository.findByNguoiDang_Id(user.getId());
        
	    // Đưa danh sách truyện sang view để hiển thị
        model.addAttribute("listTruyen", listTruyen);
        
        // Xác định file giao diện sẽ load vào layout chính
		model.addAttribute("content", "/QuangCao/QuangCao");
		
		 // Trả về layout chính
        return "/layout/main";
    }
	
	@GetMapping("/thueQuangCao")
	public String ThueQuangCao(Authentication authentication, Model model) {
		String username = authentication.getName();

		Optional<NguoiDung> userOpt = nguoiDungRepository.findByTenDangNhap(username);

		if (userOpt.isEmpty()) {
			return "redirect:/login";
		}

		NguoiDung user = userOpt.get();

		List<Truyen> listTruyen = truyenRepository.findByNguoiDang_Id(user.getId());

		model.addAttribute("listTruyen", listTruyen);
		model.addAttribute("content", "/QuangCao/ThueQuangCao");
		return "/layout/main";
	}

}
