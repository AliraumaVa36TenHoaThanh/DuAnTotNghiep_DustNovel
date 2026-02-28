package com.fpoly.controller;

import com.fpoly.model.Truyen;
import com.fpoly.model.TheLoai;
import com.fpoly.repository.TheLoaiRepository;
import com.fpoly.repository.TruyenRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/DustNovel/the-loai")
public class TheLoaiController {

	@Autowired
	private TheLoaiRepository theLoaiRepository;

	@Autowired
	private TruyenRepository truyenRepository;

	@RequestMapping("/{id}")
	public String xemTheoTheLoai(@PathVariable("id") Long id, Model model) {
		TheLoai theLoai = theLoaiRepository.findById(id).orElse(null);
		if (theLoai == null) {
			return "redirect:/DustNovel/home";
		}
		List<Truyen> truyens = truyenRepository.findByTheLoais_Id(id);
		model.addAttribute("theLoai", theLoai);
		model.addAttribute("truyens", truyens);
        if (theLoai.getStatusTheLoai() != null &&
            theLoai.getStatusTheLoai().name().equals("OFF")) {

            model.addAttribute("warningMessage",
                    " 🛑 Thể loại này đã ngừng hoạt động, nhưng sẽ mở lại trong tương lai ! ");
        }
        
		model.addAttribute("title", "DustNovel | Thể loại: " + theLoai.getTenTheLoai());
		model.addAttribute("content", "truyen/the-loai");
		return "layout/main";
	}
}
