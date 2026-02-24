package com.fpoly.controller;

import com.fpoly.model.Truyen;
import com.fpoly.model.enums.LoaiTruyen;
import com.fpoly.model.enums.TrangThaiTruyen;
import com.fpoly.model.enums.StatusTheLoai;
import com.fpoly.model.Chuong;
import com.fpoly.model.NguoiDung;
import com.fpoly.model.TheLoai;
import com.fpoly.service.TruyenService;
import com.fpoly.service.BinhLuanService;
import com.fpoly.service.ChuongService;
import com.fpoly.service.TapService;
import com.fpoly.service.TheLoaiService;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.repository.TheLoaiRepository;
import com.fpoly.repository.TruyenRepository;
import com.fpoly.security.CustomUserDetails;
import com.fpoly.security.SecurityUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/DustNovel")
public class TruyenController {
		@Autowired
	     TruyenService truyenService;
	    @Autowired
	     ChuongService chuongService;
	    @Autowired
	     NguoiDungRepository nguoiDungRepo;
	    @Autowired
	    TheLoaiRepository theLoaiRepo;	
	    @Autowired
	    TruyenRepository truyenRepo;
	    @Autowired
	    SecurityUtil securityUtil;
	    @Autowired
	    TheLoaiService tlSer;
	    @Autowired
	    TapService tapService;
	    @Autowired
	    BinhLuanService binhLuanService;
	    
//	    @GetMapping("/truyen/{id:\\d+}")
//	    public String detail(@PathVariable Long id, Model model) {
//	    	
//	    	Truyen truyen = truyenService.findById(id);
//	        NguoiDung user = securityUtil.getCurrentUserFromDB(); 
//	        model.addAttribute("currentUser", user); 
//
//	        model.addAttribute("truyen", truyen);
//	        model.addAttribute("dsTap", tapService.findByTruyen(id));
//	        model.addAttribute("content", "truyen/detail");
//	        return "layout/main";
//	    }
	    @GetMapping("/truyen/{id:\\d+}")
	    public String detail(@PathVariable Long id, Model model) {
	    	
	        Truyen truyen = truyenService.findById(id);
	        LocalDateTime ngayCapNhat = chuongService.layNgayCapNhatTruyen(id);
	        long tongSoTu = chuongService.laySoTuTruyen(id);
	        if (truyen == null) return "redirect:/DustNovel/home";
	        Chuong chuongDau = chuongService.layChuongDau(id);
	        Chuong chuongMoi = chuongService.layChuongMoiNhat(id);
	        model.addAttribute("chuongDau", chuongDau);
	        model.addAttribute("chuongMoi", chuongMoi);
	        model.addAttribute("truyen", truyen);
	        model.addAttribute("dsTap", tapService.findByTruyen(id));
	        model.addAttribute("comments", binhLuanService.getByTruyen(id));
	        model.addAttribute("luotXem", truyen.getLuotXem());
	        model.addAttribute("tongSoTu", tongSoTu);
	        model.addAttribute("ngayCapNhat", ngayCapNhat);
	        
	        model.addAttribute("content", "truyen/detail");
	        return "layout/main";
	    }
	    @GetMapping("/themtruyen")
	    public String showAddForm(Model model) {

	        model.addAttribute("truyen", new Truyen());
	        model.addAttribute("dsTheLoai", theLoaiRepo.findByStatusTheLoai(StatusTheLoai.ON));
	        model.addAttribute("content", "truyen/add");
	        model.addAttribute("title", "Thêm truyện");

	        return "layout/main";
	    }
	    @PostMapping("/themtruyen")
	    public String addTruyen(
	            @ModelAttribute Truyen truyen,
	            @RequestParam List<Long> theLoaiIds
	    ) {
	        CustomUserDetails cud =
	        	    (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

	        Long UserId = cud.getId();          
	        NguoiDung user = cud.getUser();     

	        
	        truyen.setNguoiDang(user);

	        if (truyen.getAnhBia() == null || truyen.getAnhBia().isBlank()) {
	            truyen.setAnhBia("/images/aria.jpg");
	        }

	        truyenService.save(truyen, theLoaiIds);
	        return "redirect:/DustNovel/home";
	    }

	    @GetMapping("/truyen/tim-kiem")
	    public String timKiemTruyen(
	            @RequestParam("keyword") String keyword,
	            Model model
	    ) {

	        List<Truyen> truyens = truyenRepo
	                .findByTenTruyenContainingIgnoreCase(keyword);

	        model.addAttribute("keyword", keyword);
	        model.addAttribute("truyens", truyens);
	        model.addAttribute(
	                "title",
	                "DustNovel | Tìm kiếm truyện: " + keyword
	            );
	        model.addAttribute("content", "truyen/tim-kiem");

	        return "layout/main";
	    }
	    
	    @PreAuthorize("@permissionService.canDeleteTruyen(#id)")
	    @PostMapping("/truyen/{ten_truyen}/xoa/{id}")
	    public String xoaTruyen(@PathVariable Long id) {
	        truyenService.xoaTruyen(id);
	        return "redirect:/DustNovel/home";
	    }
	    
	    @PreAuthorize("@permissionService.canEditTruyen(#id)")
	    @GetMapping("/truyen/sua/{id}")
	    public String formSua(@PathVariable Long id, Model model) {

	        Truyen truyen = truyenService.findById(id);
	        if (truyen == null) {
	            return "redirect:/DustNovel/home";
	        }

	        model.addAttribute("truyen", truyen);
	        model.addAttribute("dsTheLoai", theLoaiRepo.findAll());
	        model.addAttribute("content", "truyen/edit");
	        model.addAttribute("title", "Sửa truyện");

	        return "layout/main";
	    }
	    
//	    @PreAuthorize("@permissionService.canEditTruyen(#id)")
//	    @PostMapping("/truyen/{tenTruyen}/sua/{id}")
//	    public String sua(
//	            @PathVariable String tenTruyen,
//	            @PathVariable Long id,
//	            @ModelAttribute Truyen truyen) {
//
//	        truyen.setId(id);
//	        truyenService.suaTruyen(id, truyen);
//
//	        return "redirect:/DustNovel/truyen/" + tenTruyen + "/" + id;
//	    }
	    
	    
//	    @PreAuthorize("@permissionService.canEditTruyen(#id)")
//	    @PostMapping("/truyen/sua/{id}")
//	    public String sua(
//	            @PathVariable Long id,
//	            @ModelAttribute Truyen truyen,
//	            @RequestParam(required = false) List<Long> theLoaiIds
//	    ) {
//	        truyen.setId(id);
//
//	        Truyen old = truyenService.findById(id);
//	        truyen.setNguoiDang(old.getNguoiDang());
//
//	        if (theLoaiIds != null) {
//	            truyenService.save(truyen, theLoaiIds);
//	        } else {
//	            truyenRepo.save(truyen);
//	        }
//
//	        return "redirect:/DustNovel/truyen/" + id;
//	    }
	    @PreAuthorize("@permissionService.canEditTruyen(#id)")
	    @PostMapping("/truyen/sua/{id}")
	    public String sua(
	            @PathVariable Long id,
	            @ModelAttribute Truyen truyenForm, // Đây là data từ form gửi lên
	            @RequestParam(required = false) List<Long> theLoaiIds
	    ) {
	        // 1. Lấy truyện CŨ từ database lên (để giữ nguyên lượt xem, ngày tạo, ds chương...)
	        Truyen truyenDB = truyenService.findById(id);
	        if (truyenDB == null) {
	            return "redirect:/DustNovel/home";
	        }

	        // 2. Chỉ cập nhật những trường được phép sửa từ form
	        truyenDB.setTenTruyen(truyenForm.getTenTruyen());
	        truyenDB.setMoTa(truyenForm.getMoTa());
	        truyenDB.setTenTacGia(truyenForm.getTenTacGia());
	        truyenDB.setLoaiTruyen(truyenForm.getLoaiTruyen());
	        truyenDB.setTag18(truyenForm.getTag18());
	        
	        // Nếu có upload ảnh bìa mới thì mới cập nhật, không thì giữ ảnh cũ
	        if (truyenForm.getAnhBia() != null && !truyenForm.getAnhBia().isBlank()) {
	            truyenDB.setAnhBia(truyenForm.getAnhBia());
	        }

	        // 3. Cập nhật danh sách thể loại
	        if (theLoaiIds != null) {
	            List<TheLoai> dsTheLoai = theLoaiRepo.findAllById(theLoaiIds);
	            truyenDB.setTheLoais(dsTheLoai);
	        }

	        // 4. Lưu lại vào DB
	        truyenRepo.save(truyenDB);

	        return "redirect:/DustNovel/truyen/" + id;
	    }
	    
	    @GetMapping("/truyen/tim-kiem-nang-cao")
	    
	    public String timKiemNangCao(
	            @RequestParam(required = false) String tenTruyen,
	            @RequestParam(required = false) String tenTacGia,
	            @RequestParam(required = false) Boolean showTag18,
	            @RequestParam(required = false) LoaiTruyen loaiTruyen,
	            @RequestParam Map<String, String> params,
	            Model model
	    ) {

	        boolean isSearch =
	                tenTruyen != null ||
	                tenTacGia != null ||
	                loaiTruyen != null ||
	                showTag18 != null ||
	                params.keySet().stream().anyMatch(k -> k.startsWith("theLoai["));

	        if (isSearch) {
	            List<Truyen> ketQua = truyenService.timKiemNangCao(
	                    tenTruyen,
	                    tenTacGia,
	                    loaiTruyen,
	                    params,
	                    showTag18
	            );
	            model.addAttribute("dsTruyen", ketQua);
	        }

	        model.addAttribute("searched", isSearch);
//	        model.addAttribute("theLoais", tlSer.getAllTheLoai());
	        model.addAttribute("theLoais", theLoaiRepo.findByStatusTheLoai(StatusTheLoai.ON));
	        model.addAttribute("title", "DustNovel | Tìm kiếm nâng cao");
	        model.addAttribute("content", "truyen/tim-kiem-nang-cao");

	        return "layout/main";
	    }
	    @PostMapping("/truyen/{id}/doi-trang-thai")
	    @PreAuthorize("@permissionService.canEditTruyen(#id)")
	    public String doiTrangThai(@PathVariable Long id) {
	        Truyen truyen = truyenService.findById(id);

	        if (truyen.getTrangThai() == TrangThaiTruyen.ĐANG_RA) {
	            truyen.setTrangThai(TrangThaiTruyen.HOÀN_THÀNH);
	        } else {
	            truyen.setTrangThai(TrangThaiTruyen.ĐANG_RA);
	        }

	        truyenService.save2(truyen);
	        return "redirect:/DustNovel/truyen/" + id;
	    }  
	    @GetMapping("/the-loai/{id}")
	    public String xemTheoTheLoai(@PathVariable Long id, Model model) {

	        TheLoai theLoai = theLoaiRepo.findById(id).orElse(null);

	        if (theLoai == null) {
	            return "redirect:/DustNovel/home";
	        }

	        // Nếu OFF → log và chặn
	        if (theLoai.getStatusTheLoai() == StatusTheLoai.OFF) {
	            System.out.println("⚠ Thể loại này đang OFF: " + theLoai.getTenTheLoai());
	            return "redirect:/DustNovel/home";
	        }

	        List<Truyen> dsTruyen = truyenRepo.findByTheLoai(id);																

	        model.addAttribute("theLoai", theLoai);
	        model.addAttribute("truyens", dsTruyen);
	        model.addAttribute("content", "truyen/the-loai");
	        model.addAttribute("error", "Thể loại này hiện đang tạm khóa");

	        return "layout/main";
	    }
}
