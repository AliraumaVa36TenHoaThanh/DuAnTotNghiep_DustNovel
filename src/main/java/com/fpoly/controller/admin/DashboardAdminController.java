package com.fpoly.controller.admin;

import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.repository.TruyenRepository;
import com.fpoly.repository.ChuongRepository;
import com.fpoly.service.admin.ThongTinNapService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        
     // ===== LẤY DOANH THU GỐC =====
        List<Object[]> data = thongTinNapService.getDoanhThuTheoNgay();

        // ===== TÍNH TỔNG (CHƯA LỌC) =====
        long tongTien = 0;
        for (Object[] item : data) {
            if (item[1] != null) {
                tongTien += ((Number) item[1]).longValue();
            }
        }
        
        model.addAttribute("tongTienFilter", tongTien);

        // ===== DÙNG SERVICE (QUAN TRỌNG) =====
        model.addAttribute("topUsers",
                thongTinNapService.getTopToken());

//        model.addAttribute("doanhThuNgay",
//                thongTinNapService.getDoanhThuTheoNgay());
        
        model.addAttribute("doanhThuNgay", data);

        model.addAttribute("topNap",
                thongTinNapService.getTopNapNhieu());

        model.addAttribute("content", "view/admin/menu/dashboard");
        model.addAttribute("title", "Dashboard");

        return "layout/admin_base";
    }
    
    
    @GetMapping("/dba/dashboard/locdoanhthu")
    public String dashboardLocDoanhThu(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer quarter,
            Model model) {

        // ===== THỐNG KÊ =====
        model.addAttribute("totalUsers", nguoiDungRepository.count());
        model.addAttribute("totalTruyen", truyenRepository.count());
        model.addAttribute("totalChuong", chuongRepository.count());

        // ===== DATA GỐC =====
        var doanhThuGoc = thongTinNapService.getDoanhThuTheoNgay();

        // ===== FILTER =====
        var doanhThuFilter = doanhThuGoc;

        if (type != null && !type.isEmpty()) {

            doanhThuFilter = doanhThuGoc.stream().filter(item -> {

                if (item[0] == null) return false;

                var ngay = ((java.sql.Date) item[0]).toLocalDate();

                switch (type) {

                    case "day":
                        return date != null && ngay.toString().equals(date);

                    case "month":
                        return month != null && year != null &&
                               ngay.getMonthValue() == month &&
                               ngay.getYear() == year;

                    case "year":
                        return year != null &&
                               ngay.getYear() == year;

                    case "quarter":
                        if (year == null || quarter == null) return false;

                        int q = (ngay.getMonthValue() - 1) / 3 + 1;
                        return q == quarter && ngay.getYear() == year;
                }

                return true;

            }).toList();
        }

        // ===== TÍNH TỔNG =====
        double tongTienFilter = doanhThuFilter.stream()
                .mapToDouble(item -> ((Number) item[1]).doubleValue())
                .sum();

        double tongTienGoc = doanhThuGoc.stream()
                .mapToDouble(item -> ((Number) item[1]).doubleValue())
                .sum();

        // ===== ADD MODEL =====
        model.addAttribute("doanhThuNgay", doanhThuGoc);
        model.addAttribute("doanhThuNgayFilter", doanhThuFilter);
        model.addAttribute("tongTienFilter", tongTienFilter);
        model.addAttribute("tongTien", tongTienGoc);

        model.addAttribute("topUsers",
                thongTinNapService.getTopToken());

        model.addAttribute("topNap",
                thongTinNapService.getTopNapNhieu());

        model.addAttribute("content", "view/admin/menu/dashboard");
        model.addAttribute("title", "Dashboard");

        return "layout/admin_base";
    }
    
    
}