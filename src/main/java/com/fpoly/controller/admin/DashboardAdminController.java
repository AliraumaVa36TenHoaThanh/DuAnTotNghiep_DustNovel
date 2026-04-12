package com.fpoly.controller.admin;

import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.repository.TruyenRepository;
import com.fpoly.repository.ChuongRepository;
import com.fpoly.service.admin.ThongTinNapService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardAdminController {

    private final NguoiDungRepository nguoiDungRepository;
    private final TruyenRepository truyenRepository;
    private final ChuongRepository chuongRepository;
    private final ThongTinNapService thongTinNapService;

    @GetMapping("/dba/dashboard")
    public String dashboard(Model model) {

        // ===== THỐNG KÊ =====
        model.addAttribute("totalUsers", nguoiDungRepository.count());
        model.addAttribute("totalTruyen", truyenRepository.count());
        model.addAttribute("totalChuong", chuongRepository.count());

        // ===== DÙNG SERVICE (QUAN TRỌNG) =====
        model.addAttribute("topUsers",
                thongTinNapService.getTopToken());

        model.addAttribute("doanhThuNgay",
                thongTinNapService.getDoanhThuTheoNgay());

        model.addAttribute("topNap",
                thongTinNapService.getTopNapNhieu());

        model.addAttribute("content", "view/admin/menu/dashboard");
        model.addAttribute("title", "Dashboard");

        return "layout/admin_base";
    }
}