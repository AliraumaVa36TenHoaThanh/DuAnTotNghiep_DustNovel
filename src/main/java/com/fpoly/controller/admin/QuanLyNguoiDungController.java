package com.fpoly.controller.admin;


import com.fpoly.AdminService.AdminUserService;
import com.fpoly.model.NguoiDung;
import com.fpoly.repository.NguoiDungRepository;
import java.util.List;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.fpoly.service.TheLoaiService;
import com.fpoly.service.TruyenService;
import com.fpoly.service.admin.TruyenAdminService;
import com.fpoly.model.TheLoai;
import com.fpoly.model.Truyen;
import com.fpoly.repository.TheLoaiRepository;
import com.fpoly.repository.TruyenRepository;
import com.fpoly.security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.fpoly.model.enums.StatusTheLoai;
import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@RequestMapping("/dba")
public class QuanLyNguoiDungController {
	@Autowired
	TruyenRepository truyenRepo;
	@Autowired
	TruyenAdminService truyenAdminSer;	
	@Autowired
	TheLoaiRepository theLoaiRepository;
	@Autowired
	TruyenRepository truyenRepository;
	@Autowired
	TruyenService truyenService;
	@Autowired
	TheLoaiRepository theLoaiRepo;


	@GetMapping("/user")
	public String list(Model model) {
		model.addAttribute("content", "/view/admin/user/index.html");
		return "/layout/admin_base";
	}

	@GetMapping("/user/truyen")
	public String truyenAdmin(Model model) {
		List<Truyen> listTruyen = truyenRepo.findAll();
		model.addAttribute("listTruyenUser", listTruyen);
		model.addAttribute("content", "view/admin/truyen/quanLyTruyen");
		return "layout/admin_base";
	}

	@GetMapping("/user/truyen/xoa/{id}")
	public String xoaTruyen(@PathVariable Long id) {
		truyenAdminSer.xoaTruyen(id);
		return "redirect:/dba/user/truyen";
	}
	
	@GetMapping("/user/truyen/them")
	public String formThemTruyenAdmin(Model model) {

	    model.addAttribute("truyen", new Truyen()); 
	    model.addAttribute("dsTheLoai", theLoaiRepo.findByStatusTheLoai(StatusTheLoai.ON));	
	    model.addAttribute("content", "view/admin/truyen/ThemTruyenAdmin");

	    return "layout/admin_base";
	}
	
	@PostMapping("/user/truyen/them")
	public String themTruyen(
	        @ModelAttribute Truyen truyen,
	        @RequestParam(value = "theLoaiIds", required = false) List<Long> theLoaiIds,
	        @RequestParam(value = "file", required = false) MultipartFile file, Model model
	) throws IOException {

	    CustomUserDetails cud =
	            (CustomUserDetails) SecurityContextHolder
	                    .getContext()
	                    .getAuthentication()
	                    .getPrincipal();

	    NguoiDung user = cud.getUser();
	    truyen.setNguoiDang(user);

	    //  THÊM PHẦN UPLOAD 
	    if (file != null && !file.isEmpty()) {

	        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

	        String uploadDir = System.getProperty("user.dir")
	                + "/src/main/resources/static/uploads/truyen/";

	        File dir = new File(uploadDir);
	        if (!dir.exists()) dir.mkdirs();

	        file.transferTo(new File(uploadDir + fileName));

	        truyen.setAnhBia("/uploads/truyen/" + fileName);
	    }

	    // Nếu không upload file và không nhập link
	    if (truyen.getAnhBia() == null || truyen.getAnhBia().isBlank()) {
	        truyen.setAnhBia("/images/aria.jpg");
	    }

	    if (theLoaiIds == null || theLoaiIds.isEmpty()) {

	        model.addAttribute("error", "Vui lòng chọn ít nhất một thể loại!");
	        model.addAttribute("dsTheLoai", theLoaiRepo.findByStatusTheLoai(StatusTheLoai.ON));
	        model.addAttribute("truyen", truyen);
	        model.addAttribute("content", "view/admin/truyen/ThemTruyenAdmin");

	        return "layout/admin_base";
	    }

	    truyenService.save(truyen, theLoaiIds);

	    return "redirect:/DustNovel/home";
	}
	
	
	
	@GetMapping("/truyen-the-loai")
	public String danhSachTheLoai(Model model) {

	    List<TheLoai> listTheLoai = theLoaiRepository.findByStatusTheLoai(StatusTheLoai.ON);;

	    model.addAttribute("listTheLoai", listTheLoai);
	    model.addAttribute("content", "view/admin/truyen/ListTheLoai");

	    return "layout/admin_base";
	}
	
	
	@GetMapping("/truyen-the-loai/them")
    public String formThemTheLoai(Model model) {
        model.addAttribute("theLoai", new TheLoai());
        model.addAttribute("content", "view/admin/truyen/ThemTheLoai");
        return "layout/admin_base";
    }	
	@PostMapping("/truyen-the-loai/them")
    public String xuLyThemTheLoai(@ModelAttribute TheLoai theLoai, Model model) {
		// check rỗng
	    if (theLoai.getTenTheLoai() == null || theLoai.getTenTheLoai().trim().isEmpty()) {
	        model.addAttribute("errorTenTheLoai", "Tên thể loại không được để trống");
	        model.addAttribute("content", "view/admin/truyen/ThemTheLoai");
	        return "layout/admin_base";
	    }

	    // check trùng
	    if (theLoaiRepository.existsByTenTheLoai(theLoai.getTenTheLoai())) {
	        model.addAttribute("errorTenTheLoai", "Tên thể loại đã tồn tại");
	        model.addAttribute("content", "view/admin/truyen/ThemTheLoai");
	        return "layout/admin_base";
	    }
	    theLoai.setStatusTheLoai(StatusTheLoai.ON);
        theLoaiRepository.save(theLoai);
        return "redirect:/dba/truyen-the-loai";
    }
	
	@GetMapping("/truyen-the-loai/check-ten")
	@ResponseBody
	public boolean checkTen(@RequestParam String ten) {
	    return theLoaiRepository.existsByTenTheLoai(ten);
	}
	
	
	@GetMapping("/truyen-the-loai/chi-tiet/{id}")
	public String xemTruyenTheoTheLoai(
	        @PathVariable("id") Long idTheLoai,Model model) {
	    List<Truyen> listTruyen = truyenRepository.findTruyenByTheLoaiId(idTheLoai);
	    TheLoai theLoai =  theLoaiRepository.findById(idTheLoai).orElse(null);
	    model.addAttribute("listTruyenXem", listTruyen);
	    model.addAttribute("theLoai", theLoai);
	    model.addAttribute("content", "view/admin/truyen/ListXemTheLoai");
	    return "layout/admin_base";
	}
	
    
	@GetMapping("/truyen-the-loai/sua/{id}")
	public String formSuaTheLoai(@PathVariable Long id, Model model) {
	    TheLoai theLoai = theLoaiRepository.findById(id).orElse(null);
	    model.addAttribute("theLoai", theLoai);
	    model.addAttribute("content", "view/admin/truyen/SuaTheLoai");
	    return "layout/admin_base";
	}	
	@PostMapping("/truyen-the-loai/sua")
	public String xuLySuaTheLoai(@ModelAttribute TheLoai theLoai, Model model, RedirectAttributes redirectAttributes) {
		
		if (theLoai.getTenTheLoai() == null || theLoai.getTenTheLoai().trim().isEmpty()) {
	        model.addAttribute("errorTenTheLoai", "Tên thể loại không được để trống");
	        model.addAttribute("content", "view/admin/truyen/SuaTheLoai");
	        return "layout/admin_base";
	    }
		
		if (theLoaiRepository.existsByTenTheLoaiAndIdNot(
	            theLoai.getTenTheLoai(),
	            theLoai.getId())) {
	        model.addAttribute("errorTenTheLoai", "Tên thể loại đã tồn tại");
	        model.addAttribute("content", "view/admin/truyen/SuaTheLoai");
	        return "layout/admin_base";
	    }
		theLoai.setStatusTheLoai(StatusTheLoai.ON);
	    theLoaiRepository.save(theLoai);
	    return "redirect:/dba/truyen-the-loai";
	}
	

    
	@GetMapping("/truyen-the-loai/delete/{id}")
	public String xoaTam(@PathVariable Long id,
	                     RedirectAttributes redirectAttributes) {

	    TheLoai tl = theLoaiRepository.findById(id).orElse(null);
	    if (tl != null) {
	        tl.setStatusTheLoai(StatusTheLoai.OFF);
	        theLoaiRepository.save(tl);
	    }
	    return "redirect:/dba/truyen-the-loai";
	}
	
		
	
	@GetMapping("/truyen-the-loai/da-tam-dung")
	public String danhSachTamDung(Model model) {

	    List<TheLoai> listTamDung =
	            theLoaiRepository.findByStatusTheLoai(StatusTheLoai.OFF);

	    model.addAttribute("listTheLoaiDaDung", listTamDung);
	    model.addAttribute("content", "view/admin/truyen/ListTheLoaiDaTamDung");

	    return "layout/admin_base";
	}


	@GetMapping("/truyen-the-loai/khoi-phuc/{id}")
	public String khoiPhuc(@PathVariable Long id,
	                       RedirectAttributes redirectAttributes) {

	    TheLoai tl = theLoaiRepository.findById(id).orElse(null);

	    if (tl != null) {
	        tl.setStatusTheLoai(StatusTheLoai.ON);
	        theLoaiRepository.save(tl);
	    }

	    return "redirect:/dba/truyen-the-loai/da-tam-dung";
	}
	
	
	@GetMapping("/truyen-the-loai/xoa-han/{id}")
	public String xoaHan(@PathVariable Long id) {
	    theLoaiRepository.deleteById(id);
	    return "redirect:/dba/truyen-the-loai/da-tam-dung";
	}

	
}
