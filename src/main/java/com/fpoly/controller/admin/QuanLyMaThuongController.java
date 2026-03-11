package com.fpoly.controller.admin;
import java.security.Principal;
import com.fpoly.model.MaThuong;
import com.fpoly.model.NguoiDung;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.repository.PhieuThuongRepository;
// Lưu ý: Import class SecurityUtil theo đúng đường dẫn project của cậu nhé
// import com.fpoly.security.SecurityUtil; 
import com.fpoly.service.MaThuongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/ma-thuong")
public class QuanLyMaThuongController {
	@Autowired
    private PhieuThuongRepository phieuThuongRepo;
    @Autowired
    private MaThuongService maThuongService;
    // THÊM DÒNG NÀY VÀO:
    @Autowired
    private NguoiDungRepository nguoiDungRepo;

    @GetMapping
    public String index(Model model) {

        model.addAttribute("dsMaThuong",
                maThuongService.layDanhSachMaThuong());

        model.addAttribute("content",
                "/view/admin/ma-thuong/index");

        model.addAttribute("title",
                "Quản Lý Mã Thưởng");

        return "/layout/admin_base";
    }

    // ================================
    // FORM THÊM
    // ================================
    @GetMapping("/them")
    public String formThem(Model model) {

        model.addAttribute("maThuong", new MaThuong());
        model.addAttribute("content",
                "/view/admin/ma-thuong/add");

        model.addAttribute("title",
                "Thêm Mã Thưởng");

        return "/layout/admin_base";
    }

    // ================================
    // LƯU THÊM
    // ================================
    @PostMapping("/them")
    public String them(@ModelAttribute MaThuong maThuong,
                       RedirectAttributes ra) {

        try {
            maThuongService.themMaThuong(maThuong);
            ra.addFlashAttribute("successMsg",
                    "Thêm mã thưởng thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg",
                    e.getMessage());
            return "redirect:/dba/ma-thuong/them";
        }

        return "redirect:/admin/ma-thuong";
    }

    // ================================
    // FORM SỬA
    // ================================
    @GetMapping("/sua/{id}")
    public String formSua(@PathVariable Long id,
                          Model model,
                          RedirectAttributes ra) {

        MaThuong mt = maThuongService.layMaThuongTheoId(id);

        if (mt == null) {
            ra.addFlashAttribute("errorMsg",
                    "Không tìm thấy mã thưởng!");
            return "redirect:/dba/ma-thuong";
        }

        model.addAttribute("maThuong", mt);
        model.addAttribute("content",
                "/view/admin/ma-thuong/add");

        model.addAttribute("title",
                "Sửa Mã Thưởng");

        return "/layout/admin_base";
    }
   
}

   