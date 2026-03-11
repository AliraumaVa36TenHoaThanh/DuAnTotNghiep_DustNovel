package com.fpoly.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;

import com.fpoly.model.Banner;
import com.fpoly.model.NguoiDung;
import com.fpoly.model.Truyen;
import com.fpoly.repository.BannerRepository;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.repository.ThueBannerRepository;
import com.fpoly.repository.TruyenRepository;

import org.springframework.ui.Model;
import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/quang-cao")
public class QuangCaoController {	
	@Autowired
    TruyenRepository truyenRepository;
	@Autowired
    NguoiDungRepository nguoiDungRepository;
	@Autowired
	BannerRepository bannerRepository;
	@Autowired
	ThueBannerRepository thueBannerRepository;
	
	
	
	@GetMapping("/")
    public String quangCao(Authentication authentication,Model model) {
		
		String username = authentication.getName();
        
	    Optional<NguoiDung> userOpt = nguoiDungRepository.findByTenDangNhap(username);
	    
	    if(userOpt.isEmpty()){
	        return "redirect:/login";
	    }
	    NguoiDung user = userOpt.get();
	    
	    List<Truyen> listTruyen = truyenRepository.findByNguoiDang_Id(user.getId());
	    List<Banner> listBanner = bannerRepository.findAll();
        
        model.addAttribute("listTruyen", listTruyen);       
        model.addAttribute("listBanner", listBanner);
		model.addAttribute("content", "/QuangCao/QuangCao");
		
        return "/layout/main";
    }
	
	
	
	
	@PostMapping("/TaoQuangCao")
	public String taoQuangCao(
	        Authentication authentication,
	        @RequestParam Long truyenId,
	        @RequestParam String viTri,
	        @RequestParam("file") MultipartFile file,
	        @RequestParam Long tokenMoiNgay,
	        @RequestParam String startDate,
	        @RequestParam String endDate,
	        Model model) throws IOException {

	    String fileName = System.currentTimeMillis() + "_banner_" + file.getOriginalFilename();

	    String uploadDir = System.getProperty("user.dir") + "/uploads/banner/";

	    File dir = new File(uploadDir);
	    if (!dir.exists()) {
	        dir.mkdirs(); 
	    }

	    File saveFile = new File(uploadDir + fileName);
	    file.transferTo(saveFile);

	    Banner banner = new Banner();
	    banner.setTruyenId(truyenId);
	    banner.setViTri(viTri);

	    banner.setAnhBanner("/uploads/banner/" + fileName);

	    banner.setTokenMoiNgay(tokenMoiNgay);
	    banner.setNgayBatDau(LocalDate.parse(startDate));
	    banner.setNgayKetThuc(LocalDate.parse(endDate));
	    banner.setTrangThai("HOAT_DONG");

	    bannerRepository.save(banner);

	    return "redirect:/quang-cao/";
	}
	
	
	
	
	
	@GetMapping("LichSu/QuangCao")
	public String LichSuQuangCao(Authentication authentication, Model model) {
		String username = authentication.getName();

	    Optional<NguoiDung> userOpt = nguoiDungRepository.findByTenDangNhap(username);

	    if(userOpt.isEmpty()){
	        return "redirect:/login";
	    }

	    NguoiDung user = userOpt.get();

	    List<Truyen> listTruyen = truyenRepository.findByNguoiDang_Id(user.getId());

	    List<Banner> listBanner = bannerRepository.findByTruyenIdIn(
	            listTruyen.stream().map(Truyen::getId).toList()
	    );

	    model.addAttribute("listBanner", listBanner);
		model.addAttribute("content", "/QuangCao/LichSuQuangCao");
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
		model.addAttribute("content", "quangcao/ThueQuangCao");
		return "/layout/main";
	}

	
	@GetMapping("LichSu/ThueQuangCao")
	public String LichSuThueQuangCao(Authentication authentication, Model model) {
		model.addAttribute("content", "/QuangCao/LichSuThueQuangCao");
		return "/layout/main";
	}

}
