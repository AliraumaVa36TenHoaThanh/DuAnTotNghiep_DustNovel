package com.fpoly.controller.admin;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.fpoly.service.admin.TruyenAdminService;
import com.fpoly.model.TheLoai;
import com.fpoly.model.Truyen;
import com.fpoly.repository.TheLoaiRepository;
import com.fpoly.repository.TruyenRepository;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.fpoly.model.enums.StatusTheLoai;


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
