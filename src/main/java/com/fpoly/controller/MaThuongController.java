package com.fpoly.controller;

import com.fpoly.model.MaThuong;
import com.fpoly.service.MaThuongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/ma-thuong")
@PreAuthorize("hasRole('ADMIN')")
public class MaThuongController {

	@Autowired
	private MaThuongService maThuongService;
	@GetMapping
	public String danhSach(Model model) {

		model.addAttribute("dsMaThuong", maThuongService.layDanhSachMaThuong());

		model.addAttribute("content", "/view/admin/ma-thuong/index");

		model.addAttribute("title", "Quản Lý Mã Thưởng");

		return "/layout/admin_base";
	}

	@GetMapping("/them")
	public String formThem(Model model) {

		model.addAttribute("maThuong", new MaThuong());
		model.addAttribute("content", "/view/admin/ma-thuong/add");

		model.addAttribute("title", "Thêm Mã Thưởng");

		return "/layout/admin_base";
	}

	@PostMapping("/them")
	public String them(@ModelAttribute MaThuong maThuong, RedirectAttributes ra) {

		try {
			maThuongService.themMaThuong(maThuong);
			ra.addFlashAttribute("successMsg", "Thêm mã thưởng thành công!");
		} catch (Exception e) {
			ra.addFlashAttribute("errorMsg", e.getMessage());
			return "redirect:/dba/ma-thuong/them";
		}

		return "redirect:/admin/ma-thuong";
	}

	@GetMapping("/sua/{id}")
	public String formSua(@PathVariable Long id, Model model, RedirectAttributes ra) {

		MaThuong mt = maThuongService.layMaThuongTheoId(id);

		if (mt == null) {
			ra.addFlashAttribute("errorMsg", "Không tìm thấy mã thưởng!");
			return "redirect:/dba/ma-thuong";
		}

		model.addAttribute("maThuong", mt);
		model.addAttribute("content", "/view/admin/ma-thuong/add");

		model.addAttribute("title", "Sửa Mã Thưởng");

		return "/layout/admin_base";
	}

	@PostMapping("/doi-trang-thai/{id}")
	public String doiTrangThai(@PathVariable Long id, RedirectAttributes ra) {

		maThuongService.doiTrangThai(id);

		ra.addFlashAttribute("successMsg", "Đã thay đổi trạng thái!");

		return "redirect:/admin/ma-thuong";
	}
}