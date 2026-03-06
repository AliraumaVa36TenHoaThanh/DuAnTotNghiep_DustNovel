package com.fpoly.controller.cilent;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fpoly.model.NguoiDung;
import com.fpoly.model.NhomDich;
import com.fpoly.model.ThanhVienNhomDich;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.repository.NhomDichRepository;
import com.fpoly.repository.ThanhVienNhomDichRepository;

@Controller
@RequestMapping("/dbu")
public class MyTeamController {

    @Autowired
    private NhomDichRepository nhomDichRepository;

    @Autowired
    private ThanhVienNhomDichRepository thanhVienNhomDichRepo;
    
    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    // =============================
    // DANH SÁCH NHÓM CỦA TÔI
    // =============================
    @GetMapping("/nhomDichCuaToi")
    public String myTeam(Model model, Principal principal) {

        NguoiDung user = nguoiDungRepository
                .findByTenDangNhap(principal.getName())
                .orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        List<NhomDich> list = nhomDichRepository.findByTruongNhom(user);

        model.addAttribute("listNhomDich", list);
        model.addAttribute("content", "view/client/NhomDich/NhomDichCuaUser");
        model.addAttribute("title", "Nhóm của tôi");

        return "/layout/cilent_base";
    }

 // =============================
 // FORM SỬA
 // =============================
 @GetMapping("/nhomDichCuaToi/sua/{id}")
 public String formSua(@PathVariable Long id, Model model, Principal principal) {

     NguoiDung user = nguoiDungRepository
             .findByTenDangNhap(principal.getName())
             .orElse(null);

     if (user == null) {
         return "redirect:/login";
     }

     NhomDich nhom = nhomDichRepository.findById(id)
             .orElseThrow(() -> new RuntimeException("Không tìm thấy nhóm"));

     if (!nhom.getTruongNhom().getId().equals(user.getId())) {
         return "redirect:/dbu/nhomDichCuaToi";
     }

     List<ThanhVienNhomDich> thanhVienDaDuyet =
             thanhVienNhomDichRepo.findByNhomDichAndTrangThai(nhom, "DA_DUYET");
     
     thanhVienDaDuyet.removeIf(tv -> 
     tv.getNguoiDung().getId().equals(nhom.getTruongNhom().getId())
 );

     model.addAttribute("nhom", nhom);
     model.addAttribute("listThanhVien", thanhVienDaDuyet);
     model.addAttribute("content", "view/client/NhomDich/UpdateNhomDichUser");
     model.addAttribute("title", "Sửa nhóm");

     return "/layout/cilent_base";
 }


 @PostMapping("/nhomDichCuaToi/sua/{id}")
 public String update(@PathVariable Long id,
                      @ModelAttribute("nhom") NhomDich nhomForm,
                      @RequestParam("truongNhomId") Long truongNhomId,
                      Model model,
                      Principal principal,
                      RedirectAttributes redirectAttributes) {

     NguoiDung user = nguoiDungRepository
             .findByTenDangNhap(principal.getName())
             .orElse(null);

     if (user == null) {
         return "redirect:/login";
     }

     NhomDich nhom = nhomDichRepository.findById(id)
             .orElseThrow(() -> new RuntimeException("Không tìm thấy nhóm"));

     if (!nhom.getTruongNhom().getId().equals(user.getId())) {
         return "redirect:/dbu/nhomDichCuaToi";
     }

     boolean hasError = false;

     if (nhomForm.getTenNhom() == null || nhomForm.getTenNhom().trim().isEmpty()) {
         model.addAttribute("errorTenNhom", "Tên nhóm không được để trống");
         hasError = true;
     }

     if (nhomForm.getMoTa() == null || nhomForm.getMoTa().trim().isEmpty()) {
         model.addAttribute("errorMoTa", "Mô tả không được để trống");
         hasError = true;
     }

     NguoiDung truongMoi = nguoiDungRepository.findById(truongNhomId).orElse(null);

     if (truongMoi != null) {

         List<NhomDich> nhomDangLamTruong =
                 nhomDichRepository.findByTruongNhom(truongMoi);

         if (!nhomDangLamTruong.isEmpty()
                 && !truongMoi.getId().equals(nhom.getTruongNhom().getId())) {

             model.addAttribute("errorTruongNhom",
                     "Thành viên " + truongMoi.getTenDangNhap() + " đã là trưởng nhóm của một nhóm khác");

             nhomForm.setTruongNhom(nhom.getTruongNhom());

             model.addAttribute("nhom", nhomForm);
             model.addAttribute("listThanhVien",
                     thanhVienNhomDichRepo.findByNhomDichAndTrangThai(nhom, "DA_DUYET"));
             model.addAttribute("content", "view/client/NhomDich/UpdateNhomDichUser");
             model.addAttribute("title", "Sửa nhóm");

             return "layout/cilent_base";
         }
     }

     if (hasError) {

         List<ThanhVienNhomDich> thanhVienDaDuyet =
                 thanhVienNhomDichRepo.findByNhomDichAndTrangThai(nhom, "DA_DUYET");

         nhomForm.setTruongNhom(nhom.getTruongNhom());

         model.addAttribute("listThanhVien", thanhVienDaDuyet);
         model.addAttribute("nhom", nhomForm);
         model.addAttribute("content", "view/client/NhomDich/UpdateNhomDichUser");
         model.addAttribute("title", "Sửa nhóm");

         return "layout/cilent_base";
     }

     nhom.setTenNhom(nhomForm.getTenNhom());
     nhom.setMoTa(nhomForm.getMoTa());

     if (truongMoi != null && !truongMoi.getId().equals(nhom.getTruongNhom().getId())) {

         NguoiDung truongCu = nhom.getTruongNhom();

         ThanhVienNhomDich tvCu =
                 thanhVienNhomDichRepo
                 .findByNhomDichAndNguoiDung(nhom, truongCu)
                 .orElse(null);

         if (tvCu != null) {
             tvCu.setVaiTro("THANH_VIEN");
             thanhVienNhomDichRepo.save(tvCu);
         }

         ThanhVienNhomDich tvMoi =
                 thanhVienNhomDichRepo
                 .findByNhomDichAndNguoiDung(nhom, truongMoi)
                 .orElse(null);

         if (tvMoi != null) {
             tvMoi.setVaiTro("TRUONG_NHOM");
             tvMoi.setTrangThai("DA_DUYET");
             thanhVienNhomDichRepo.save(tvMoi);
         }
     }

     if (truongMoi != null) {
         nhom.setTruongNhom(truongMoi);
     }

     nhomDichRepository.save(nhom);

     redirectAttributes.addFlashAttribute("successMessage", "Cập nhật nhóm thành công!");

     return "redirect:/dbu/nhomDichCuaToi";
 }

    // =============================
    // XÓA NHÓM
    // =============================
    @GetMapping("/nhomDichCuaToi/xoa/{id}")
    public String xoa(@PathVariable Long id, Principal principal) {

        NguoiDung user = nguoiDungRepository
                .findByTenDangNhap(principal.getName())
                .orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        NhomDich nhom = nhomDichRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhóm"));

        if (nhom.getTruongNhom().getId().equals(user.getId())) {
            nhomDichRepository.delete(nhom);
        }

        return "redirect:/dbu/nhomDichCuaToi";
    }
    
    
    
}