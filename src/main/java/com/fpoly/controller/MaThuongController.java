package com.fpoly.controller;
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
public class MaThuongController {
	@Autowired
    private PhieuThuongRepository phieuThuongRepo;
    @Autowired
    private MaThuongService maThuongService;
    // THÊM DÒNG NÀY VÀO:
    @Autowired
    private NguoiDungRepository nguoiDungRepo;
    // @Autowired
    // private SecurityUtil securityUtil; // Bỏ comment dòng này nếu project cậu dùng SecurityUtil để lấy user

    /* ========================================================
     * 1. PHẦN DÀNH CHO ADMIN (QUẢN LÝ MÃ)
     * ======================================================== */

    // HIỂN THỊ DANH SÁCH
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/ma-thuong")
    public String danhSachMaThuong(Model model) {
        model.addAttribute("dsMaThuong", maThuongService.layDanhSachMaThuong());
        // Trỏ đúng vào file templates/view/admin/ma-thuong/index.html
        return "view/admin/ma-thuong/index"; 
    }

    // HIỂN THỊ FORM THÊM
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/ma-thuong/them")
    public String hienFormThem(Model model) {
        model.addAttribute("maThuong", new MaThuong());
        model.addAttribute("title", "Thêm Mã Thưởng");
        // Trỏ đúng vào file templates/view/admin/ma-thuong/add.html
        return "view/admin/ma-thuong/add";
    }

    // XỬ LÝ LƯU THÊM MỚI
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/ma-thuong/them")
    public String xuLyThem(@ModelAttribute MaThuong maThuong, RedirectAttributes redirectAttributes) {
        try {
            maThuongService.themMaThuong(maThuong);
            redirectAttributes.addFlashAttribute("successMsg", "Thêm mã thưởng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/admin/ma-thuong/them";
        }
        // Redirect về URL danh sách (KHÔNG có chữ view)
        return "redirect:/admin/ma-thuong";
    }

    // HIỂN THỊ FORM SỬA
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/ma-thuong/sua/{id}")
    public String hienFormSua(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        MaThuong maThuong = maThuongService.layMaThuongTheoId(id);
        if (maThuong == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "Không tìm thấy mã thưởng!");
            return "redirect:/admin/ma-thuong";
        }
        model.addAttribute("maThuong", maThuong);
        model.addAttribute("title", "Sửa Mã Thưởng");
        // Tận dụng luôn file templates/view/admin/ma-thuong/add.html làm form sửa
        return "view/admin/ma-thuong/add";
    }

    // XỬ LÝ LƯU SỬA
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/ma-thuong/sua/{id}")
    public String xuLySua(@PathVariable Long id, @ModelAttribute MaThuong maThuong, RedirectAttributes redirectAttributes) {
        try {
            maThuongService.suaMaThuong(id, maThuong);
            redirectAttributes.addFlashAttribute("successMsg", "Cập nhật mã thưởng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/admin/ma-thuong/sua/" + id;
        }
        return "redirect:/admin/ma-thuong";
    }

    // XỬ LÝ ĐỔI TRẠNG THÁI (KHÓA/MỞ KHÓA)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/ma-thuong/doi-trang-thai/{id}")
    public String doiTrangThai(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        maThuongService.doiTrangThai(id);
        redirectAttributes.addFlashAttribute("successMsg", "Đã thay đổi trạng thái mã thưởng!");
        return "redirect:/admin/ma-thuong";
    }


    /* ========================================================
     * 2. PHẦN DÀNH CHO USER (NHẬP MÃ ĐỔI THƯỞNG)
     * ======================================================== */

//    @GetMapping("/DustNovel/nhap-code")
//    public String trangNhapCode(Model model) {
//        model.addAttribute("title", "Nhập Giftcode");
//        // Giả sử file của user nằm ở templates/view/user/nhap-ma.html
//        // Nếu đường dẫn khác, cậu sửa lại chỗ này nhé!
//        return "view/user/nhap-ma"; 
//    }
//
//    @PostMapping("/DustNovel/nhap-code")
//    public String xuLyNhapCodeUser(@RequestParam("code") String code, RedirectAttributes redirectAttributes) {
//        
//        // Chỗ này cậu thay bằng code lấy User đang đăng nhập của project cậu nhé
//        // NguoiDung currentUser = securityUtil.getCurrentUserFromDB();
//        NguoiDung currentUser = null; // Tạm để null, cậu nhớ sửa lại dòng này!
//
//        if (currentUser == null) {
//            return "redirect:/DustNovel/login";
//        }
//
//        String ketQua = maThuongService.xuLyNhapCode(code.trim().toUpperCase(), currentUser);
//
//        if (ketQua.startsWith("SUCCESS:")) {
//            String soPhieu = ketQua.split(":")[1];
//            redirectAttributes.addFlashAttribute("successMsg", "Tuyệt vời! Bạn nhận được " + soPhieu + " phiếu thưởng!");
//        } else {
//            redirectAttributes.addFlashAttribute("errorMsg", ketQua);
//        }
//
//        return "redirect:/DustNovel/nhap-code";
//    }
    
    @GetMapping("/DustNovel/nhap-code")
    public String trangNhapCode(Model model) {
        model.addAttribute("title", "Nhập Giftcode");
        // SỬA DÒNG NÀY: Trỏ đúng vào file user/ma-thuong.html
        return "user/ma_thuong";
    }

//    @PostMapping("/DustNovel/nhap-code")
//    public String xuLyNhapCodeUser(@RequestParam("code") String code, RedirectAttributes redirectAttributes) {
//        
//        // Chỗ này cậu thay bằng code lấy User đang đăng nhập của project cậu nhé
//        // NguoiDung currentUser = securityUtil.getCurrentUserFromDB();
//        NguoiDung currentUser = null; // Tạm để null, cậu nhớ sửa lại dòng này!
//
//        if (currentUser == null) {
//            return "redirect:/DustNovel/login";
//        }
//
//        String ketQua = maThuongService.xuLyNhapCode(code.trim().toUpperCase(), currentUser);
//
//        if (ketQua.startsWith("SUCCESS:")) {
//            String soPhieu = ketQua.split(":")[1];
//            redirectAttributes.addFlashAttribute("successMsg", "Tuyệt vời! Bạn nhận được " + soPhieu + " phiếu thưởng!");
//        } else {
//            redirectAttributes.addFlashAttribute("errorMsg", ketQua);
//        }
//
//        return "redirect:/DustNovel/nhap-code";
//    }
    @PostMapping("/DustNovel/nhap-code")
    public String xuLyNhapCodeUser(@RequestParam("code") String code, 
                                   Principal principal, 
                                   RedirectAttributes redirectAttributes) {
       
        if (principal == null) {
            return "redirect:/DustNovel/login";
        }
        String tenDangNhap = principal.getName();

        NguoiDung currentUser = nguoiDungRepo.findByTenDangNhap(tenDangNhap).orElse(null);

        if (currentUser == null) {
            return "redirect:/DustNovel/login";
        }
        
        String ketQua = maThuongService.xuLyNhapCode(code.trim().toUpperCase(), currentUser);

        if (ketQua.startsWith("SUCCESS:")) {
            String soPhieu = ketQua.split(":")[1];
            redirectAttributes.addFlashAttribute("successMsg", "Tuyệt vời! Bạn nhận được " + soPhieu + " phiếu thưởng!");
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", ketQua);
        }

        return "redirect:/DustNovel/nhap-code";
    }
}