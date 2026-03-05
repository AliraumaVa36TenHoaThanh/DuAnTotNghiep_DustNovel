package com.fpoly.controller;

import com.fpoly.model.LoiMoiNhomDich;
import com.fpoly.model.NguoiDung;
import com.fpoly.model.NhomDich;
import com.fpoly.model.ThanhVienNhomDich;
import com.fpoly.repository.LoiMoiNhomDichRepository;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private final LoiMoiNhomDichRepository loiMoiRepo;


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
                          @RequestParam String moTa,
                          RedirectAttributes redirectAttributes) {

        NguoiDung user = securityUtil.getCurrentUserFromDB();

        if (user == null) {
            return "redirect:/DustNovel/login";
        }
        
     // 🔥 Nếu đã là trưởng nhóm
        if (nhomDichRepository.existsByTruongNhom(user)) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Bạn đã tạo 1 nhóm rồi, không thể tạo thêm!"
            );

            return "redirect:/DustNovel/nhom-dich";
        }

//        // 🔥 Nếu đã là thành viên nhóm khác
//        if (thanhVienRepo.existsByNguoiDungAndTrangThai(user, "DA_DUYET")) {
//
//            redirectAttributes.addFlashAttribute(
//                    "errorMessage",
//                    "Bạn đã thuộc 1 nhóm khác!"
//            );
//
//            return "redirect:/DustNovel/nhom-dich";
//        }

        nhomDichService.taoNhom(tenNhom, moTa, user);
        
        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Tạo nhóm thành công!"
        );

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
    public String yeuCauThamGia(@PathVariable Long id,
    		RedirectAttributes redirectAttributes) {

        NguoiDung user = securityUtil.getCurrentUserFromDB();
        if (user == null) {
            return "redirect:/DustNovel/login";
        }
//        if (nhomDichRepository.existsByTruongNhom(user)
//                || thanhVienRepo.existsByNguoiDungAndTrangThai(user, "DA_DUYET")) {
//
//                redirectAttributes.addFlashAttribute(
//                        "errorMessage",
//                        "Bạn đã thuộc 1 nhóm rồi!"
//                );
//
//                return "redirect:/DustNovel/nhom-dich";
//            }
        
     //  Kiểm tra nếu đã là thành viên của bất kỳ nhóm nào (đã duyệt)
        boolean daLaThanhVien =
                thanhVienRepo.existsByNguoiDungAndTrangThai(user, "DA_DUYET");

        // Kiểm tra nếu đang có yêu cầu chờ duyệt ở nhóm khác
        boolean dangChoDuyet =
                loiMoiRepo.existsByNguoiDuocMoiAndTrangThai(user, "CHO_DUYET");

        // Nếu đang chờ duyệt hoặc đã tham gia nhóm khác → chặn
        if (daLaThanhVien || dangChoDuyet) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Bạn đang chờ duyệt hoặc đã tham gia vào nhóm khác rồi!"
            );

            return "redirect:/DustNovel/nhom-dich";
        }

        NhomDich nhom = nhomDichService.findById(id);
        if (nhom == null)
            return "redirect:/DustNovel/nhom-dich";

        nhomDichService.guiYeuCauThamGia(nhom, user);

        return "redirect:/DustNovel/nhom-dich/" + id;
    }
    
    @PostMapping("/nhom-dich/duyet/{loiMoiId}")
    public String duyet(@PathVariable Long loiMoiId,
                        @RequestParam Long nhomId,
                        RedirectAttributes redirectAttributes) {
    	
        LoiMoiNhomDich loiMoi = loiMoiRepo.findById(loiMoiId).orElse(null);
        if (loiMoi != null) {
        	String tenUser = loiMoi.getNguoiMoi().getTenDangNhap();
            nhomDichService.duyetYeuCau(loiMoiId);
             redirectAttributes.addFlashAttribute(
                "successMessage",
                "Đã duyệt yêu cầu của \"" + tenUser + "\""
        );
    }

        return "redirect:/DustNovel/nhom-dich/" + nhomId;
    }
    
    @PostMapping("/nhom-dich/tu-choi/{loiMoiId}")
    public String tuChoi(@PathVariable Long loiMoiId,
                         @RequestParam Long nhomId,
                         RedirectAttributes redirectAttributes) {
    	LoiMoiNhomDich loiMoi = loiMoiRepo.findById(loiMoiId).orElse(null);
    	if (loiMoi != null) {
            String tenUser = loiMoi.getNguoiMoi().getTenDangNhap();
            nhomDichService.tuChoiYeuCau(loiMoiId);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Đã từ chối yêu cầu của \"" + tenUser + "\""
            );
        }

        return "redirect:/DustNovel/nhom-dich/" + nhomId;
    }
    @PostMapping("/nhom-dich/kick/{id}")
    public String kickThanhVien(@PathVariable Long id,
                                @RequestParam Long nhomId,
                                RedirectAttributes redirectAttributes) {

        ThanhVienNhomDich tv = thanhVienRepo.findById(id).orElse(null);
        if (tv == null) {
        	return "redirect:/DustNovel/nhom-dich/" + nhomId;
        }

        NguoiDung currentUser = securityUtil.getCurrentUserFromDB();

        if (!tv.getNhomDich().getTruongNhom().getId()
                .equals(currentUser.getId())) {

        	return "redirect:/DustNovel/nhom-dich/" + nhomId;
        }
        
        String tenUser = tv.getNguoiDung().getTenDangNhap();

//        tv.setTrangThai("DA_KICK");
//        thanhVienRepo.save(tv);
//        
//        loiMoiRepo.deleteByNguoiDuocMoiAndNhomDich(
//                tv.getNguoiDung(),
//                tv.getNhomDich()
//        );
        
        nhomDichService.kickThanhVien(tv);
        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Đã kick \"" + tenUser + "\" ra khỏi nhóm"
        );

        return "redirect:/DustNovel/nhom-dich/" + nhomId;
    }
    
    
    
    
    @PostMapping("/nhom-dich/xoa/{id}")
    public String xoaNhom(@PathVariable Long id,
                          RedirectAttributes redirectAttributes) {

        NhomDich nhom = nhomDichRepository.findById(id).orElse(null);

        if (nhom != null) {
        	String tenNhom = nhom.getTenNhom();
            nhomDichRepository.delete(nhom);

            // ✅ Thông báo thành công
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Nhóm \"" + tenNhom + "\" đã xóa thành công!"
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Nhóm không tồn tại!"
            );
        }

        return "redirect:/DustNovel/nhom-dich";
    }
    
    
    
    @PostMapping("/nhom-dich/roi-nhom/{id}")
    public String roiNhom(@PathVariable("id") Long nhomId,
                          RedirectAttributes redirectAttributes) {

        // Gọi service xử lý update trạng thái + xóa lời mời
        nhomDichService.outNhom(nhomId);

        // Thông báo
        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Bạn đã rời nhóm thành công!"
        );

        // ✅ QUAN TRỌNG: redirect ra danh sách nhóm
        return "redirect:/DustNovel/nhom-dich";
    }
}