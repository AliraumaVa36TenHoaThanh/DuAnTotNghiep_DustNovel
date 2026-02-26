package com.fpoly.controller.admin;

import com.fpoly.model.NapTien;
import com.fpoly.model.NguoiDung;
import com.fpoly.repository.NapTienRepository;
import com.fpoly.repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/dba")
public class QuanLyNapTienController {

    @Autowired
    NapTienRepository napTienRepo;

    @Autowired
    NguoiDungRepository nguoiDungRepo;

    // ===== DANH SÁCH =====
    @GetMapping("/nap-tien")
    public String listNapTien(Model model) {

        List<NapTien> listNapTien = napTienRepo.findAll();

        model.addAttribute("listNapTien", listNapTien);
        model.addAttribute("content", "view/admin/nap/admin-nap-tien");
        model.addAttribute("title", "Quản Lý Nạp Tiền");

        return "layout/admin_base";
    }

    // ===== DUYỆT =====
    @GetMapping("/nap-tien/duyet/{id}")
    public String duyetNap(@PathVariable Long id) {

        NapTien nap = napTienRepo.findById(id).orElse(null);
        if (nap == null) return "redirect:/dba/nap-tien";

        if (!"PENDING".equals(nap.getTrangThai()))
            return "redirect:/dba/nap-tien";

        NguoiDung user = nap.getNguoiDung();

        user.setToken(user.getToken() + nap.getSoTokenNhan());
        nguoiDungRepo.save(user);

        nap.setTrangThai("DA_DUYET");
        napTienRepo.save(nap);
        System.out.println("DUYET ID: " + id);

        return "redirect:/dba/nap-tien";
    }

    // ===== TỪ CHỐI =====
    @GetMapping("/nap-tien/tu-choi/{id}")
    public String tuChoiNap(@PathVariable Long id) {

        NapTien nap = napTienRepo.findById(id).orElse(null);
        if (nap == null) return "redirect:/dba/nap-tien";

        if (!"PENDING".equals(nap.getTrangThai()))
            return "redirect:/dba/nap-tien";

        nap.setTrangThai("TU_CHOI");
        napTienRepo.save(nap);

        return "redirect:/dba/nap-tien";
    }
}