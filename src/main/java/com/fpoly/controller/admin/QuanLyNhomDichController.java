package com.fpoly.controller.admin;

import java.util.List;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.fpoly.model.NhomDich;
import com.fpoly.repository.NhomDichRepository;

@Controller
@RequestMapping("/dba/nhom-dich")
public class QuanLyNhomDichController {

    @Autowired
    private NhomDichRepository nhomDichRepository;

    // =============================
    // LIST NHÓM DỊCH
    // =============================
    @GetMapping
    public String listNhomDich(Model model) {

        List<NhomDich> listNhom = nhomDichRepository.findAll();

        model.addAttribute("listNhomDich", listNhom);
        model.addAttribute("content", "/view/admin/nhomDich/QuanLyNhomDich");
        model.addAttribute("title", "Quản Lý Nhóm Dịch");

        return "/layout/admin_base";
    }

// // =============================
// // CHI TIẾT NHÓM
// // =============================
// @GetMapping("/chi-tiet/{id}")
// public String chiTiet(@PathVariable Long id, Model model) {
//
//     NhomDich nhom = nhomDichRepository.findById(id)
//             .orElseThrow(() -> new RuntimeException("Không tìm thấy nhóm"));
//
//     model.addAttribute("nhom", nhom);
//     model.addAttribute("laTruongNhom", false); // tránh lỗi thymeleaf null boolean
//     model.addAttribute("content", "congcu/index-guild"); // bỏ dấu /
//     model.addAttribute("title", "Chi Tiết Nhóm Dịch");
//
//     return "layout/admin_base"; // bỏ dấu /
// }
    
//================= FORM SỬA =================
	@GetMapping("/sua/{id}")
	public String formSua(@PathVariable Long id, Model model) {

		NhomDich nhom = nhomDichRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy nhóm"));

		model.addAttribute("nhom", nhom);
		model.addAttribute("content", "/view/admin/nhomDich/UpdateNhomDich");
		model.addAttribute("title", "Sửa Nhóm Dịch");

		return "/layout/admin_base";
	}

//================= UPDATE =================
	@PostMapping("/sua/{id}")
	public String capNhat(@PathVariable Long id,
	                      @ModelAttribute("nhom") NhomDich nhomForm,
	                      Model model) {

	    boolean hasError = false;

	    // validate tên nhóm
	    if (nhomForm.getTenNhom() == null || nhomForm.getTenNhom().trim().isEmpty()) {
	        model.addAttribute("errorTenNhom", "Tên nhóm không được để trống");
	        hasError = true;
	    }

	    // validate mô tả
	    if (nhomForm.getMoTa() == null || nhomForm.getMoTa().trim().isEmpty()) {
	        model.addAttribute("errorMoTa", "Mô tả không được để trống");
	        hasError = true;
	    }

	    // nếu có lỗi -> quay lại form sửa
	    if (hasError) {
	        model.addAttribute("nhom", nhomForm);
	        model.addAttribute("content", "/view/admin/nhomDich/UpdateNhomDich");
	        model.addAttribute("title", "Sửa Nhóm Dịch");
	        return "/layout/admin_base";
	    }

	    // tìm nhóm trong database
	    NhomDich nhom = nhomDichRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Không tìm thấy nhóm"));

	    // cập nhật field cho phép
	    nhom.setTenNhom(nhomForm.getTenNhom());
	    nhom.setMoTa(nhomForm.getMoTa());

	    nhomDichRepository.save(nhom);

	    return "redirect:/dba/nhom-dich";
	}

    // =============================
    // XÓA NHÓM
    // =============================
    @GetMapping("/xoa/{id}")
    public String xoaNhom(@PathVariable Long id) {

        nhomDichRepository.deleteById(id);

        return "redirect:/dba/nhom-dich";
    }

}