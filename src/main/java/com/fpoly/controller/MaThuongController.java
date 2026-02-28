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
@RequestMapping("/admin/ma-thuong")
@PreAuthorize("hasRole('ADMIN')")
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

        return "redirect:/admin/ma-thuong";
    }
}