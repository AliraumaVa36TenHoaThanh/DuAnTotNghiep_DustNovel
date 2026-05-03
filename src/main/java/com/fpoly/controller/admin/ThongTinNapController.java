package com.fpoly.controller.admin;

import com.fpoly.model.NapTien;
import com.fpoly.model.NguoiDung;
import com.fpoly.repository.NapTienRepository;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.service.admin.ThongTinNapService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dba")
public class ThongTinNapController {

    private final ThongTinNapService thongTinNapService;

    @GetMapping("/thong-tin-nap")
    public String thongTinNap(Model model) {
    	
        List<Object[]> data = thongTinNapService.getDoanhThuTheoNgay();

        model.addAttribute("doanhThuNgay", data);

        long tongTien = 0;
        for (Object[] item : data) {
            if (item[1] != null) {
                tongTien += ((Number) item[1]).longValue();
            }
        }

        model.addAttribute("tongTienFilter", tongTien);

        model.addAttribute("doanhThuNgay",
                thongTinNapService.getDoanhThuTheoNgay());

        model.addAttribute("topNap",
                thongTinNapService.getTopNapNhieu());

        model.addAttribute("topToken",
                thongTinNapService.getTopToken());

        model.addAttribute("content", "view/admin/nap/admin-thong-tin-nap");
        model.addAttribute("title", "Thống Kê Nạp Tiền");
        System.out.println(thongTinNapService.getDoanhThuTheoNgay());

        return "layout/admin_base";
    }
    
    @GetMapping("/thong-tin-nap/filter")
    public String filterThongTinNap(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer quarter,
            Model model) {

        List<Object[]> doanhThuGoc =
                thongTinNapService.getDoanhThuTheoNgay();

        
        List<Object[]> doanhThuFilter = doanhThuGoc;

        if (type != null && !type.isEmpty()) {

            doanhThuFilter = doanhThuFilter.stream().filter(item -> {

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
        
        long tongTien = 0;
        for (Object[] item : doanhThuFilter) {
            if (item[1] != null) {
                tongTien += ((Number) item[1]).longValue();
            }
        }

        model.addAttribute("doanhThuNgay", doanhThuGoc);

        model.addAttribute("doanhThuNgayFilter", doanhThuFilter);
        
        model.addAttribute("tongTienFilter", tongTien);

        model.addAttribute("topNap",
                thongTinNapService.getTopNapNhieu());

        model.addAttribute("topToken",
                thongTinNapService.getTopToken());

        model.addAttribute("content", "view/admin/nap/admin-thong-tin-nap");
        model.addAttribute("title", "Thống Kê Nạp Tiền");

        return "layout/admin_base";
    }
    
}