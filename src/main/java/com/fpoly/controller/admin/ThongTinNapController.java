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
}