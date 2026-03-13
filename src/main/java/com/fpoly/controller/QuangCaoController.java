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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	        @RequestParam(required = false) Long truyenId,
	        @RequestParam(required = false) String viTri,
	        @RequestParam("file") MultipartFile file,
	        @RequestParam(required = false) Long tokenMoiNgay,
	        @RequestParam String startDate,
	        @RequestParam String endDate,
	        Model model, RedirectAttributes redirectAttributes) throws IOException {

	    boolean hasError = false;

	    if(truyenId == null){
	        model.addAttribute("errorTruyen","Vui lòng chọn truyện");
	        hasError = true;
	    }

	    if(viTri == null || viTri.isEmpty()){
	        model.addAttribute("errorViTri","Vui lòng chọn vị trí");
	        hasError = true;
	    }

	    if(file == null || file.isEmpty()){
	        model.addAttribute("errorFile","Vui lòng chọn ảnh banner");
	        hasError = true;
	    }

	    if(tokenMoiNgay == null){
	        model.addAttribute("errorToken","Vui lòng nhập token mỗi ngày");
	        hasError = true;
	    }
	    else if(tokenMoiNgay < 1000){
	        model.addAttribute("errorToken","Token phải lớn hơn hoặc bằng 1000");
	        hasError = true;
	    }
	    else if(tokenMoiNgay > 50000000){
	        model.addAttribute("errorToken","Chỉ được chạy quảng cáo với số token nhỏ hơn hoặc bằng 50000000");
	        hasError = true;
	    }

	    if(startDate == null || startDate.isEmpty()){
	        model.addAttribute("errorStartDate","Vui lòng chọn ngày bắt đầu");
	        hasError = true;
	    }

	    if(endDate == null || endDate.isEmpty()){
	        model.addAttribute("errorEndDate","Vui lòng chọn ngày kết thúc");
	        hasError = true;
	    }

	    if(startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()){

	        LocalDate start = LocalDate.parse(startDate);
	        LocalDate end = LocalDate.parse(endDate);
	        LocalDate today = LocalDate.now();

	        if(start.isBefore(today)){
	            model.addAttribute("errorStartDate","Ngày bắt đầu phải từ hôm nay trở đi");
	            hasError = true;
	        }

	        if(end.isBefore(start)){
	            model.addAttribute("errorEndDate","Ngày kết thúc phải sau ngày bắt đầu");
	            hasError = true;
	        }

	    }

	    String username = authentication.getName();

	    Optional<NguoiDung> userOpt = nguoiDungRepository.findByTenDangNhap(username);

	    if(userOpt.isEmpty()){
	        return "redirect:/login";
	    }

	    NguoiDung user = userOpt.get();

	    List<Truyen> listTruyen = truyenRepository.findByNguoiDang_Id(user.getId());

	    if(hasError){
	        model.addAttribute("listTruyen", listTruyen);
	        model.addAttribute("listBanner", bannerRepository.findAll());
	        model.addAttribute("content", "/QuangCao/QuangCao");
	        model.addAttribute("truyenId", truyenId);
	        model.addAttribute("viTri", viTri);
	        model.addAttribute("tokenMoiNgay", tokenMoiNgay);
	        model.addAttribute("startDate", startDate);
	        model.addAttribute("endDate", endDate);
	        return "/layout/main";
	    }


	    String fileName = System.currentTimeMillis() + "_banner_" + file.getOriginalFilename();

	    String uploadDir = System.getProperty("user.dir") + "/uploads/banner/";

	    File dir = new File(uploadDir);
	    if (!dir.exists()) {
	        dir.mkdirs(); 
	    }

	    File saveFile = new File(uploadDir + fileName);
	    file.transferTo(saveFile);

	    Banner banner = new Banner();
		/* banner.setTruyenId(truyenId); */
	    Truyen truyen = truyenRepository.findById(truyenId).get();
	    banner.setTruyen(truyen);
	    banner.setViTri(viTri);

	    banner.setAnhBanner("/uploads/banner/" + fileName);

	    banner.setTokenMoiNgay(tokenMoiNgay);


	    LocalDate start = LocalDate.parse(startDate);
	    LocalDate end = LocalDate.parse(endDate);
	    LocalDate today = LocalDate.now();

	    banner.setNgayBatDau(start);
	    banner.setNgayKetThuc(end);

	    if (today.isBefore(start)) {
	        banner.setTrangThai("CHO_CHAY");   
	    } 
	    else if (today.isAfter(end)) {
	        banner.setTrangThai("HET_HAN");    
	    } 
	    else {
	        banner.setTrangThai("HOAT_DONG"); 
	    }


	    bannerRepository.save(banner);

	    redirectAttributes.addFlashAttribute("successMessage", "Bạn đã tạo quảng cáo thành công !");

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

		LocalDate today = LocalDate.now(); 
		/* LocalDate today = LocalDate.parse("2026-03-10"); */     /* sửa today nhỏ hơn ngày hiện tại */

	    for (Banner b : listBanner) {

	        if (today.isAfter(b.getNgayKetThuc())) {
	            b.setTrangThai("HET_HAN");
	        } 
	        else if (today.isBefore(b.getNgayBatDau())) {
	            b.setTrangThai("CHO_CHAY");
	        } 
	        else {
	            b.setTrangThai("HOAT_DONG");
	        }

	        bannerRepository.save(b);
	    }

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
