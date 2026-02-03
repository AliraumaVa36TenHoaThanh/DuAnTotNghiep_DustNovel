package com.fpoly.controller;

import com.fpoly.model.Truyen;
import com.fpoly.model.enums.LoaiTruyen;
import com.fpoly.model.NguoiDung;
import com.fpoly.service.TruyenService;
import com.fpoly.service.ChuongService;
import com.fpoly.service.TapService;
import com.fpoly.service.TheLoaiService;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.repository.TheLoaiRepository;
import com.fpoly.repository.TruyenRepository;
import com.fpoly.security.CustomUserDetails;
import com.fpoly.security.SecurityUtil;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/DustNovel")
public class TruyenController {
		@Autowired
	     TruyenService truyenService;
	    @Autowired
	     ChuongService chuongService;
	    @Autowired
	     NguoiDungRepository nguoiDungRepo;
	    @Autowired
	    TheLoaiRepository theLoaiRepo;	
	    @Autowired
	    TruyenRepository truyenRepo;
	    @Autowired
	    SecurityUtil securityUtil;
	    @Autowired
	    TheLoaiService tlSer;
	    @Autowired
	    TapService tapService;
	    
//	    @GetMapping("/truyen/{id:\\d+}")
//	    public String detail(@PathVariable Long id, Model model) {
//	    	
//	    	Truyen truyen = truyenService.findById(id);
//	        NguoiDung user = securityUtil.getCurrentUserFromDB(); 
//	        model.addAttribute("currentUser", user); 
//
//	        model.addAttribute("truyen", truyen);
//	        model.addAttribute("dsTap", tapService.findByTruyen(id));
//	        model.addAttribute("content", "truyen/detail");
//	        return "layout/main";
//	    }
	    @GetMapping("/truyen/{id:\\d+}")
	    public String detail(@PathVariable Long id, Model model) {

	        Truyen truyen = truyenService.findById(id);
	        if (truyen == null) return "redirect:/DustNovel/home";

	        NguoiDung user = securityUtil.getCurrentUserFromDB();

	        model.addAttribute("currentUser", user);
	        model.addAttribute("truyen", truyen);

	        model.addAttribute("dsTap", tapService.findByTruyen(id));

	        model.addAttribute("content", "truyen/detail");
	        return "layout/main";
	    }

	    @GetMapping("/themtruyen")
	    public String showAddForm(Model model) {

	        model.addAttribute("truyen", new Truyen());
	        model.addAttribute("dsTheLoai", theLoaiRepo.findAll());
	        model.addAttribute("content", "truyen/add");
	        model.addAttribute("title", "Thêm truyện");

	        return "layout/main";
	    }
	    @PostMapping("/themtruyen")
	    public String addTruyen(
	            @ModelAttribute Truyen truyen,
	            @RequestParam List<Long> theLoaiIds
	    ) {
	        CustomUserDetails cud =
	        	    (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

	        Long UserId = cud.getId();          
	        NguoiDung user = cud.getUser();     


	        truyen.setNguoiDang(user);

	        if (truyen.getAnhBia() == null || truyen.getAnhBia().isBlank()) {
	            truyen.setAnhBia("/images/aria.jpg");
	        }

	        truyenService.save(truyen, theLoaiIds);
	        return "redirect:/DustNovel/home";
	    }

	    @GetMapping("/truyen/tim-kiem")
	    public String timKiemTruyen(
	            @RequestParam("keyword") String keyword,
	            Model model
	    ) {

	        List<Truyen> truyens = truyenRepo
	                .findByTenTruyenContainingIgnoreCase(keyword);

	        model.addAttribute("keyword", keyword);
	        model.addAttribute("truyens", truyens);
	        model.addAttribute(
	                "title",
	                "DustNovel | Tìm kiếm truyện: " + keyword
	            );
	        model.addAttribute("content", "truyen/tim-kiem");

	        return "layout/main";
	    }
	    
	    @PreAuthorize("@permissionService.canDeleteTruyen(#id)")
	    @PostMapping("/truyen/{ten_truyen}/xoa/{id}")
	    public String xoaTruyen(@PathVariable Long id) {
	        truyenService.xoaTruyen(id);
	        return "redirect:/DustNovel/home";
	    }
	    
	    @PreAuthorize("@permissionService.canEditTruyen(#id)")
	    @GetMapping("/truyen/{tenTruyen}/sua/{id}")
	    public String formSua(
	            @PathVariable String tenTruyen,
	            @PathVariable Long id,
	            Model model) {

	        model.addAttribute("truyen", truyenService.findById(id));
	        model.addAttribute("content", "truyen/edit");
	        model.addAttribute("title", "Sửa truyện");
	        return "layout/main";
	    }
	    
	    @PreAuthorize("@permissionService.canEditTruyen(#id)")
	    @PostMapping("/truyen/{tenTruyen}/sua/{id}")
	    public String sua(
	            @PathVariable String tenTruyen,
	            @PathVariable Long id,
	            @ModelAttribute Truyen truyen) {

	        truyen.setId(id);
	        truyenService.suaTruyen(id, truyen);

	        return "redirect:/DustNovel/truyen/" + tenTruyen + "/" + id;
	    }
	    @GetMapping("/truyen/tim-kiem-nang-cao")
	    public String timKiemNangCao(
	            @RequestParam(required = false) String tenTruyen,
	            @RequestParam(required = false) String tenTacGia,
	            @RequestParam(required = false) Boolean showTag18,
	            @RequestParam(required = false) LoaiTruyen loaiTruyen,
	            @RequestParam Map<String, String> params,
	            Model model
	    ) {

	        boolean isSearch =
	                tenTruyen != null ||
	                tenTacGia != null ||
	                loaiTruyen != null ||
	                showTag18 != null ||
	                params.keySet().stream().anyMatch(k -> k.startsWith("theLoai["));

	        if (isSearch) {
	            List<Truyen> ketQua = truyenService.timKiemNangCao(
	                    tenTruyen,
	                    tenTacGia,
	                    loaiTruyen,
	                    params,
	                    showTag18
	            );
	            model.addAttribute("dsTruyen", ketQua);
	        }

	        model.addAttribute("searched", isSearch);
	        model.addAttribute("theLoais", tlSer.getAllTheLoai());
	        model.addAttribute("title", "DustNovel | Tìm kiếm nâng cao");
	        model.addAttribute("content", "truyen/tim-kiem-nang-cao");

	        return "layout/main";
	    }

}
