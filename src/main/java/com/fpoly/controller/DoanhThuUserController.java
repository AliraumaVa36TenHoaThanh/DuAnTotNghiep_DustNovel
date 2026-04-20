package com.fpoly.controller;

import com.fpoly.dto.DoanhThuTruyenDTO;
import com.fpoly.dto.ChiTietMuaChuongDTO;
import com.fpoly.model.NguoiDung;
import com.fpoly.model.Truyen;
import com.fpoly.model.enums.LoaiTruyen;
import com.fpoly.security.SecurityUtil;
import com.fpoly.service.DoanhThuService;
import com.fpoly.service.TruyenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/dbu/doanh-thu")
@PreAuthorize("isAuthenticated()")
public class DoanhThuUserController {

    @Autowired
    private DoanhThuService doanhThuService;

    @Autowired
    private TruyenService truyenService;

    @Autowired
    private SecurityUtil securityUtil;

    // 1. Màn hình tổng quan
    @GetMapping
    public String xemDoanhThuTongQuan(Model model) {
        NguoiDung currentUser = securityUtil.getCurrentUserFromDB();
        
        List<DoanhThuTruyenDTO> danhSach = doanhThuService.layThongKeDoanhThu(currentUser.getId());
        
        // Tính toán số liệu cho các Card trên cùng
        long tongTatCa = 0;
        long tongSangTac = 0;
        long tongDich = 0;

        for (DoanhThuTruyenDTO dt : danhSach) {
            tongTatCa += dt.getTongDoanhThu();
            if (dt.getLoaiTruyen() == LoaiTruyen.SÁNG_TÁC) {
                tongSangTac += dt.getTongDoanhThu();
            } else if (dt.getLoaiTruyen() == LoaiTruyen.DỊCH) {
                tongDich += dt.getTongDoanhThu();
            }
        }

        model.addAttribute("danhSachDoanhThu", danhSach);
        model.addAttribute("tongTatCa", tongTatCa);
        model.addAttribute("tongSangTac", tongSangTac);
        model.addAttribute("tongDich", tongDich);
        
        // Đẩy content vào layout
        model.addAttribute("content", "view/client/truyen/DoanhThuUser");
        return "layout/cilent_base";
    }

    // 2. Màn hình chi tiết của 1 truyện
    @GetMapping("/{truyenId}")
    public String xemChiTietDoanhThu(@PathVariable Long truyenId, Model model) {
        // Kiểm tra bảo mật (User này có phải chủ truyện không) - Ông tự check nhé
        Truyen truyen = truyenService.findById(truyenId);
        
        List<ChiTietMuaChuongDTO> chiTiet = doanhThuService.layChiTietTruyen(truyenId);
        
        long tongDoanhThuBoNay = chiTiet.stream().mapToLong(ChiTietMuaChuongDTO::getSoToken).sum();

        model.addAttribute("truyen", truyen);
        model.addAttribute("tongDoanhThuBoNay", tongDoanhThuBoNay);
        model.addAttribute("chiTiet", chiTiet);

        model.addAttribute("content", "view/client/truyen/ChiTietDoanhThu");
        return "layout/cilent_base";
    }
}