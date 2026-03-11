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
@RequestMapping("/ma-thuong")
public class MaThuongController {
    @Autowired
    private MaThuongService maThuongService;
    @Autowired
    private NguoiDungRepository nguoiDungRepo;

    @GetMapping("/nhap")
    public String trangNhapCode(Model model) {
        model.addAttribute("title", "Nhập Giftcode");
        return "user/ma_thuong";
    }

    @PostMapping("/nhap-code")
    public String xuLyNhapCodeUser(@RequestParam("code") String code,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/DustNovel/login";
        }

        String tenDangNhap = principal.getName();

        NguoiDung currentUser = nguoiDungRepo
                .findByTenDangNhap(tenDangNhap)
                .orElse(null);

        if (currentUser == null) {
            return "redirect:/DustNovel/login";
        }

        String ketQua = maThuongService
                .xuLyNhapCode(code.trim().toUpperCase(), currentUser);

        if (ketQua.startsWith("SUCCESS:")) {
            String soPhieu = ketQua.split(":")[1];
            redirectAttributes.addFlashAttribute(
                    "successMsg",
                    "Tuyệt vời! Bạn nhận được " + soPhieu + " phiếu thưởng!"
            );
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", ketQua);
        }

        return "redirect:/ma-thuong/nhap";
    }
}

   