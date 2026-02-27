package com.fpoly.controller;

import com.fpoly.model.NguoiDung;
import com.fpoly.model.NhomDich;
import com.fpoly.model.ThanhVienNhomDich;
import com.fpoly.repository.NhomDichRepository;
import com.fpoly.repository.ThanhVienNhomDichRepository;
import com.fpoly.security.SecurityUtil;
import com.fpoly.service.NhomDichService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/DustNovel")
@RequiredArgsConstructor
public class NhomDichController {

    private final NhomDichRepository nhomDichRepository;
    @Autowired
    ThanhVienNhomDichRepository thanhVienRepo;
    private final NhomDichService nhomDichService;
    private final SecurityUtil securityUtil;


    @GetMapping("/nhom-dich")
    public String hienThiDanhSach(Model model) {

        List<NhomDich> danhSach = 
                nhomDichRepository.findByTrangThai("HOAT_DONG");

        model.addAttribute("danhSachNhom", danhSach);
        model.addAttribute("content", "congcu/guild");
        model.addAttribute("title", "Danh sách nhóm dịch");

        return "layout/main";
    }
    @GetMapping("/dang-ky-nhom")
    public String hienThiForm(Model model) {

        model.addAttribute("content", "congcu/tao-guild");
        model.addAttribute("title", "Tạo nhóm dịch mới");

        return "layout/main";
    }

    // Xử lý submit
    
    @PostMapping("/dang-ky-nhom")
    public String taoNhom(@RequestParam String tenNhom,
                          @RequestParam String moTa) {

        NguoiDung user = securityUtil.getCurrentUserFromDB();

        if (user == null) {
            return "redirect:/DustNovel/login";
        }

        nhomDichService.taoNhom(tenNhom, moTa, user);

        return "redirect:/DustNovel/nhom-dich";
    }
    
    @GetMapping("/nhom-dich/{id}")
    public String chiTietNhom(@PathVariable Long id, Model model) {

        NhomDich nhom = nhomDichService.findById(id);
        if (nhom == null) return "redirect:/DustNovel/nhom-dich";

        NguoiDung user = securityUtil.getCurrentUserFromDB();

        boolean laTruongNhom = false;
        boolean daGuiYeuCau = false;

        if (user != null) {
            laTruongNhom = user.getId()
                    .equals(nhom.getTruongNhom().getId());

            daGuiYeuCau = nhomDichService.daGuiYeuCau(nhom, user);
        }

        if (laTruongNhom) {
            model.addAttribute("danhSachYeuCau",
                    nhomDichService.getYeuCauChoDuyet(nhom));
        }

        List<ThanhVienNhomDich> danhSachThanhVien =
                thanhVienRepo.findByNhomDichAndTrangThai(nhom, "DA_DUYET");

        System.out.println("Thành viên: " + danhSachThanhVien.size());

        // 🔥 THIẾU CHÍNH LÀ DÒNG NÀY
        model.addAttribute("danhSachThanhVien", danhSachThanhVien);

        model.addAttribute("nhom", nhom);
        model.addAttribute("laTruongNhom", laTruongNhom);
        model.addAttribute("daGuiYeuCau", daGuiYeuCau);

        model.addAttribute("content", "congcu/index-guild");
        model.addAttribute("title", nhom.getTenNhom());

        return "layout/main";
    }
    
    @PostMapping("/nhom-dich/{id}/yeu-cau")
    public String yeuCauThamGia(@PathVariable Long id) {

        NguoiDung user = securityUtil.getCurrentUserFromDB();
        if (user == null)
            return "redirect:/DustNovel/login";

        NhomDich nhom = nhomDichService.findById(id);
        if (nhom == null)
            return "redirect:/DustNovel/nhom-dich";

        nhomDichService.guiYeuCauThamGia(nhom, user);

        return "redirect:/DustNovel/nhom-dich/" + id;
    }
    
    @PostMapping("/nhom-dich/duyet/{loiMoiId}")
    public String duyet(@PathVariable Long loiMoiId,
                        @RequestParam Long nhomId) {

        nhomDichService.duyetYeuCau(loiMoiId);

        return "redirect:/DustNovel/nhom-dich/" + nhomId;
    }
    
    @PostMapping("/nhom-dich/tu-choi/{loiMoiId}")
    public String tuChoi(@PathVariable Long loiMoiId,
                         @RequestParam Long nhomId) {

        nhomDichService.tuChoiYeuCau(loiMoiId);

        return "redirect:/DustNovel/nhom-dich/" + nhomId;
    }
    @PostMapping("/nhom-dich/kick/{id}")
    public String kickThanhVien(@PathVariable Long id,
                                @RequestParam Long nhomId) {

        ThanhVienNhomDich tv = thanhVienRepo.findById(id).orElse(null);
        if (tv == null) {
        	return "redirect:/DustNovel/nhom-dich/" + nhomId;
        }

        NguoiDung currentUser = securityUtil.getCurrentUserFromDB();

        if (!tv.getNhomDich().getTruongNhom().getId()
                .equals(currentUser.getId())) {

        	return "redirect:/DustNovel/nhom-dich/" + nhomId;
        }

        tv.setTrangThai("DA_KICK");
        thanhVienRepo.save(tv);

        return "redirect:/DustNovel/nhom-dich/" + nhomId;
    }
}