package com.fpoly.controller.admin;
import java.security.Principal;
import com.fpoly.model.MaThuong;
import com.fpoly.model.NguoiDung;
import com.fpoly.model.enums.StatusMaThuong;
import com.fpoly.repository.MaThuongRepository;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.repository.PhieuThuongRepository;
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
    private MaThuongRepository maThuongRepo;
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

    @GetMapping("/them")
    public String formThem(Model model) {

        model.addAttribute("maThuong", new MaThuong());
        model.addAttribute("content",
                "/view/admin/ma-thuong/add");

        model.addAttribute("title",
                "Thêm Mã Thưởng");

        return "/layout/admin_base";
    }

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

    @PostMapping("/doi-trang-thai/{id}")
    public String doiTrangThai(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        MaThuong ma = maThuongRepo.findById(id).orElse(null);

        if (ma == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "Không tìm thấy mã!");
            return "redirect:/admin/ma-thuong";
        }

        if (ma.getStatusMaThuong() == StatusMaThuong.ON) {
            ma.setStatusMaThuong(StatusMaThuong.OFF);
        } else {
            ma.setStatusMaThuong(StatusMaThuong.ON);
        }

        maThuongRepo.save(ma);

        redirectAttributes.addFlashAttribute("successMsg", "Đã cập nhật trạng thái!");
        return "redirect:/admin/ma-thuong";
    }


    @GetMapping("/sua/{id}")
    public String hienThiFormSua(@PathVariable Long id, Model model) {

        MaThuong ma = maThuongRepo.findById(id).orElse(null);

        if (ma == null) {
            return "redirect:/admin/ma-thuong";
        }

        model.addAttribute("maThuong", ma);

        // 👇 KEY POINT
        model.addAttribute("content", "view/admin/ma-thuong/update");

        return "layout/admin_base";
    }
    
    @PostMapping("/sua")
    public String xuLySua(@ModelAttribute("maThuong") MaThuong form,
                          RedirectAttributes redirectAttributes) {

        MaThuong old = maThuongRepo.findById(form.getId()).orElseThrow();

        old.setSoPhieuThuong(form.getSoPhieuThuong());
        old.setSoLuongNhap(form.getSoLuongNhap());
        old.setNgayHetHan(form.getNgayHetHan());

        maThuongRepo.save(old);

        redirectAttributes.addFlashAttribute("successMsg", "Cập nhật thành công!");
        return "redirect:/admin/ma-thuong";
    }
}

   