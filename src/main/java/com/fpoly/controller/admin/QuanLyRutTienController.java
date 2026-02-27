package com.fpoly.controller.admin;

import com.fpoly.model.RutTien;
import com.fpoly.model.NguoiDung;
import com.fpoly.repository.RutTienRepository;
import com.fpoly.repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/dba")
public class QuanLyRutTienController {

    @Autowired
    RutTienRepository rutTienRepo;

    @Autowired
    NguoiDungRepository nguoiDungRepo;

    // ===== DANH SÁCH =====
    @GetMapping("/rut-tien")
    public String listRutTien(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String trangThai,
            Model model) {

        List<RutTien> listRutTien;

        if ((username != null && !username.isEmpty()) &&
            (trangThai != null && !trangThai.isEmpty())) {

            listRutTien = rutTienRepo
                    .findByNguoiDung_TenDangNhapContainingIgnoreCaseAndTrangThai(
                            username, trangThai);

        } else if (username != null && !username.isEmpty()) {

            listRutTien = rutTienRepo
                    .findByNguoiDung_TenDangNhapContainingIgnoreCase(username);

        } else if (trangThai != null && !trangThai.isEmpty()) {

            listRutTien = rutTienRepo.findByTrangThai(trangThai);

        } else {

            listRutTien = rutTienRepo.findAll();
        }

        model.addAttribute("listRutTien", listRutTien);
        model.addAttribute("content", "view/admin/nap/admin-rut-tien");
        model.addAttribute("title", "Quản Lý Rút Tiền");

        model.addAttribute("username", username);
        model.addAttribute("trangThai", trangThai);

        return "layout/admin_base";
    }

    // ===== DUYỆT =====
    @GetMapping("/rut-tien/duyet/{id}")
    public String duyetRut(@PathVariable Long id) {

        RutTien rut = rutTienRepo.findById(id).orElse(null);
        if (rut == null) return "redirect:/dba/rut-tien";

        if (!"CHO_DUYET".equals(rut.getTrangThai()))
            return "redirect:/dba/rut-tien";

        NguoiDung user = rut.getNguoiDung();

        // 🔥 Trừ token người dùng
        if (user.getToken() < rut.getSoToken())
            return "redirect:/dba/rut-tien";

        user.setToken(user.getToken() - rut.getSoToken());
        nguoiDungRepo.save(user);

        rut.setTrangThai("DA_TRA");
        rutTienRepo.save(rut);

        return "redirect:/dba/rut-tien";
    }

    // ===== TỪ CHỐI =====
    @GetMapping("/rut-tien/tu-choi/{id}")
    public String tuChoiRut(@PathVariable Long id) {

        RutTien rut = rutTienRepo.findById(id).orElse(null);
        if (rut == null) return "redirect:/dba/rut-tien";

        if (!"CHO_DUYET".equals(rut.getTrangThai()))
            return "redirect:/dba/rut-tien";

        rut.setTrangThai("TU_CHOI");
        rutTienRepo.save(rut);

        return "redirect:/dba/rut-tien";
    }
}