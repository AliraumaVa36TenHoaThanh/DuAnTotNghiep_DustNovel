package com.fpoly.controller;

import com.fpoly.model.Truyen;
import com.fpoly.model.NguoiDung;
import com.fpoly.service.TruyenService;
import com.fpoly.service.ChuongService;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.repository.TheLoaiRepository;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/DustNovel/truyen")
public class TruyenController {
		@Autowired
	     TruyenService truyenService;
	    @Autowired
	     ChuongService chuongService;
	    @Autowired
	     NguoiDungRepository nguoiDungRepo;
	    @Autowired
	    TheLoaiRepository theLoaiRepo;	
	    
	    @GetMapping("/{id:\\d+}")
	    public String detail(@PathVariable Long id, Model model) {
	        model.addAttribute("title", "Chi tiết truyện");
	        model.addAttribute("content", "truyen/detail");
	        model.addAttribute("truyen", truyenService.findById(id));
	        model.addAttribute("dsChuong", chuongService.findByTruyen(id));

	        return "layout/main";
	    }
	    @GetMapping("/them")
	    public String showAddForm(Model model) {

	        model.addAttribute("truyen", new Truyen());
	        model.addAttribute("dsTheLoai", theLoaiRepo.findAll());
	        model.addAttribute("content", "truyen/add");
	        model.addAttribute("title", "Thêm truyện");

	        return "layout/main";
	    }
	    @PostMapping("/them")
	    public String addTruyen(
	            @ModelAttribute Truyen truyen,
	            @RequestParam List<Long> theLoaiIds,
	            Principal principal
	    ) {
	    	
	        NguoiDung user = nguoiDungRepo.findById(2L).orElseThrow();
	        truyen.setNguoiDang(user);

	        if (truyen.getAnhBia() == null || truyen.getAnhBia().isBlank()) {
	            truyen.setAnhBia("/images/default.png");
	        }

	        truyenService.save(truyen, theLoaiIds);
	        return "redirect:/DustNovel/home";
	    }
}
