package com.fpoly.controller;

import com.fpoly.model.Truyen;
import com.fpoly.model.enums.LoaiTruyen;
import com.fpoly.model.enums.TrangThaiTruyen;
import com.fpoly.model.enums.StatusTheLoai;
import com.fpoly.model.Chuong;
import com.fpoly.model.NguoiDung;
import com.fpoly.model.TheLoai;
import com.fpoly.service.TruyenService;

import jakarta.validation.Valid;

import com.fpoly.service.BinhLuanService;
import com.fpoly.service.ChuongService;
import com.fpoly.service.DanhGiaTruyenService;
import com.fpoly.service.TapService;
import com.fpoly.service.TheLoaiService;
import com.fpoly.repository.ChuongRepository;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.repository.NhomDichRepository;
import com.fpoly.repository.TheLoaiRepository;
import com.fpoly.repository.TruyenRepository;
import com.fpoly.security.CustomUserDetails;
import com.fpoly.security.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;

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
	    @Autowired 
	    ChuongRepository chuongRepo;
	    @Autowired
	    NhomDichRepository nhomDichRepo;
	    @Autowired
	    DanhGiaTruyenService danhGiaService;
	    @GetMapping("/truyen/{id:\\d+}")
	    public String detail(@PathVariable Long id, Model model) {
	    	NguoiDung user = securityUtil.getCurrentUserFromDB();
	        Truyen truyen = truyenService.findById(id);
	        LocalDateTime ngayCapNhat = chuongService.layNgayCapNhatTruyen(id);
	        long tongSoTu = chuongService.laySoTuTruyen(id);
	        if (truyen == null) return "redirect:/DustNovel/home";
////	        Chuong chuongDau = chuongService.layChuongDau(id);
////	        Chuong chuongMoi = chuongService.layChuongMoiNhat(id);
//	        model.addAttribute("chuongDau", chuongDau);
//	        model.addAttribute("chuongMoi", chuongMoi);
	        Chuong chuongDau = chuongService.layChuongDauCoTheDoc(id, user);
	        Chuong chuongMoi = chuongService.layChuongMoiCoTheDoc(id, user);
	        List<Chuong> tatCaChuong = chuongRepo.findByTruyenId(id);
	        String tenNhom = null;
	        if (truyen.getNguoiDang() != null) {
	            // Lấy danh sách nhóm mà user này tham gia (hoặc làm trưởng nhóm)
	            List<com.fpoly.model.NhomDich> dsNhom = nhomDichRepo.findNhomByUserId(truyen.getNguoiDang().getId());
	            if (dsNhom != null && !dsNhom.isEmpty()) {
	                tenNhom = dsNhom.get(0).getTenNhom(); // Lấy tên nhóm đầu tiên tìm thấy
	            }
	        }
	        model.addAttribute("tenNhom", tenNhom);
	        model.addAttribute("tatCaChuong", tatCaChuong);
	        model.addAttribute("chuongDau", chuongDau);
	        model.addAttribute("chuongMoi", chuongMoi);
	        model.addAttribute("truyen", truyen);
	        model.addAttribute("dsTap", tapService.findByTruyen(id));
	        model.addAttribute("comments", binhLuanService.getByTruyen(id));
	        model.addAttribute("luotXem", truyen.getLuotXem());
	        model.addAttribute("tongSoTu", tongSoTu);
	        model.addAttribute("ngayCapNhat", ngayCapNhat);
	        model.addAttribute("danhGias", danhGiaService.layDanhGiaTheoTruyen(id));
	        model.addAttribute("trungBinhSao", danhGiaService.layDiemTrungBinh(id));
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
	    		@Valid
	            @ModelAttribute Truyen truyen,
	            BindingResult result,
	            @RequestParam (value = "theLoaiIds", required = false) List<Long> theLoaiIds,
	            @RequestParam(value = "file", required = false) MultipartFile file,
	            Model model,
	            RedirectAttributes redirectAttributes
	    )  throws IOException {
	    	    	
	        CustomUserDetails cud =
	        	    (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

	        Long UserId = cud.getId();          
	        NguoiDung user = cud.getUser();     

	        
	        truyen.setNguoiDang(user);
	        	
	        if (truyenRepo.existsByTenTruyenIgnoreCase(truyen.getTenTruyen().trim())) {
	            result.rejectValue(
	                "tenTruyen",
	                "error.tenTruyen",
	                "Tên truyện đã tồn tại"
	            );

	            model.addAttribute("dsTheLoai", theLoaiRepo.findByStatusTheLoai(StatusTheLoai.ON));
	            model.addAttribute("content", "truyen/add");
	            model.addAttribute("title", "Thêm truyện");
	            return "layout/main";
	        }
	        
	        if (result.hasErrors()) {
	            model.addAttribute("dsTheLoai", theLoaiRepo.findByStatusTheLoai(StatusTheLoai.ON));
	            model.addAttribute("content", "truyen/add");
	            model.addAttribute("title", "Thêm truyện");
	            return "layout/main";
	        }
	        
	     // =========================
	        //  THÊM PHẦN UPLOAD ẢNH
	        // =========================
	        if (file != null && !file.isEmpty()) {

	            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

	            String uploadDir = System.getProperty("user.dir")
	                    + "/src/main/resources/static/uploads/truyen/";

	            File dir = new File(uploadDir);
	            if (!dir.exists()) dir.mkdirs();

	            file.transferTo(new File(uploadDir + fileName));

	            truyen.setAnhBia("/uploads/truyen/" + fileName);
	        }

	        if (truyen.getAnhBia() == null || truyen.getAnhBia().isBlank()) {
	            truyen.setAnhBia("/images/aria.jpg");
	        }
	        
	    //  Nếu chưa chọn thể loại
	    	if (theLoaiIds == null || theLoaiIds.isEmpty()) {

	            model.addAttribute("error", "Vui lòng chọn ít nhất một thể loại!");
	            model.addAttribute("dsTheLoai", theLoaiRepo.findByStatusTheLoai(StatusTheLoai.ON));
	            model.addAttribute("truyen", truyen);
	            model.addAttribute("content", "truyen/add");
	            model.addAttribute("title", "Thêm truyện");

	            return "layout/main"; // không redirect để giữ dữ liệu
	        }

	        truyenService.save(truyen, theLoaiIds);
	        redirectAttributes.addFlashAttribute(
	        	    "successMessage",
	        	    "Thêm truyện \"" + truyen.getTenTruyen() + "\" thành công!"
	        	);
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
	    public String xoaTruyen(@PathVariable Long id, RedirectAttributes redirectAttributes) {
	    	
	    	Truyen truyen = truyenService.findById(id);
	        String tenTruyen = (truyen != null) ? truyen.getTenTruyen() : "";
	        
	        truyenService.xoaTruyen(id);
	        
	        redirectAttributes.addFlashAttribute(
	                "successMessage",
	                "Bạn đã xóa truyện \"" + tenTruyen + "\" thành công!"
	            );
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
	            @Valid @ModelAttribute("truyen") Truyen truyenForm,
	            BindingResult result,
	            @RequestParam(required = false) List<Long> theLoaiIds,
	            Model model,
	            RedirectAttributes redirectAttributes 
	    ) {
	        // 1. Lấy truyện CŨ từ database lên (để giữ nguyên lượt xem, ngày tạo, ds chương...)
	        Truyen truyenDB = truyenService.findById(id);
	        if (truyenDB == null) {
	            return "redirect:/DustNovel/home";
	        }
	        if (truyenRepo.existsByTenTruyenIgnoreCaseAndIdNot(
	                truyenForm.getTenTruyen().trim(), id)) {

	            result.rejectValue(
	                    "tenTruyen",
	                    "error.tenTruyen",
	                    "Tên truyện đã tồn tại"
	            );
	        }
	        if (result.hasErrors() || theLoaiIds == null || theLoaiIds.isEmpty()) {
	        	
	        	if (theLoaiIds == null || theLoaiIds.isEmpty()) {
	                model.addAttribute("error", "Vui lòng chọn ít nhất một thể loại!");
	            }
	        	
	        	if (theLoaiIds != null) {
	                List<TheLoai> selected = theLoaiRepo.findAllById(theLoaiIds);
	                truyenForm.setTheLoais(selected);
	            }
	        	
	            model.addAttribute("truyen", truyenForm);
	            model.addAttribute("dsTheLoai", theLoaiRepo.findAll());
	            model.addAttribute("content", "truyen/edit");
	            model.addAttribute("title", "Sửa truyện");
	            return "layout/main";
	        }
	        
	        // CHECK CÓ THAY ĐỔI HAY KHÔNG
	        boolean isChanged = false;

	        if (!truyenDB.getTenTruyen().equals(truyenForm.getTenTruyen())) {
	            isChanged = true;
	        }

	        if (!java.util.Objects.equals(truyenDB.getMoTa(), truyenForm.getMoTa())) {
	            isChanged = true;
	        }

	        if (!java.util.Objects.equals(truyenDB.getTenTacGia(), truyenForm.getTenTacGia())) {
	            isChanged = true;
	        }

	        if (truyenDB.getLoaiTruyen() != truyenForm.getLoaiTruyen()) {
	            isChanged = true;
	        }
	        
	        if (truyenDB.getTrangThai() != truyenForm.getTrangThai()) {
	            isChanged = true;
	        }

	        if (!java.util.Objects.equals(truyenDB.getTag18(), truyenForm.getTag18())) {
	            isChanged = true;
	        }

	        if (truyenForm.getAnhBia() != null && !truyenForm.getAnhBia().isBlank() &&
	            !truyenForm.getAnhBia().equals(truyenDB.getAnhBia())) {
	            isChanged = true;
	        }

	        if (theLoaiIds != null) {
	            List<TheLoai> newList = theLoaiRepo.findAllById(theLoaiIds);
	            if (!newList.equals(truyenDB.getTheLoais())) {
	                isChanged = true;
	            }
	        }
	    
	        truyenDB.setTenTruyen(truyenForm.getTenTruyen());
	        truyenDB.setMoTa(truyenForm.getMoTa());
	        truyenDB.setTenTacGia(truyenForm.getTenTacGia());
	        truyenDB.setLoaiTruyen(truyenForm.getLoaiTruyen());
	        truyenDB.setTrangThai(truyenForm.getTrangThai());
	        truyenDB.setTag18(truyenForm.getTag18());
	        
	        
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
	        
	        // CHỈ HIỆN THÔNG BÁO KHI CÓ THAY ĐỔI
	        if (isChanged) {
	            redirectAttributes.addFlashAttribute(
	                "successMessage",
	                "Cập nhật truyện \"" + truyenDB.getTenTruyen() + "\" thành công!"
	            );
	        }

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
//	    @GetMapping("/the-loai/{id}")
//	    public String xemTheoTheLoai(@PathVariable Long id, Model model) {
//
//	        TheLoai theLoai = theLoaiRepo.findById(id).orElse(null);
//
//	        if (theLoai == null) {
//	            return "redirect:/DustNovel/home";
//	        }
//
//	        // Nếu OFF → log và chặn
//	        if (theLoai.getStatusTheLoai() == StatusTheLoai.OFF) {
//	            System.out.println("⚠ Thể loại này đang OFF: " + theLoai.getTenTheLoai());
//	            return "redirect:/DustNovel/home";
//	        }
//
//	        List<Truyen> dsTruyen = truyenRepo.findByTheLoai(id);																
//
//	        model.addAttribute("theLoai", theLoai);
//	        model.addAttribute("truyens", dsTruyen);
//	        model.addAttribute("content", "truyen/the-loai");
//	        model.addAttribute("error", "Thể loại này hiện đang tạm khóa");
//
//	        return "layout/main";
//	    }
	    
	    @PostMapping("/api/cap-nhat-ngay-sinh")
	    @ResponseBody
	    public org.springframework.http.ResponseEntity<?> capNhatNgaySinh(@RequestParam("ngaySinh") String ngaySinhStr) {
	        NguoiDung user = securityUtil.getCurrentUserFromDB();
	        
	        if (user != null) {
	            try {
	                LocalDate ngaySinh = LocalDate.parse(ngaySinhStr);
	                
	                // (Tùy chọn) Validation nhẹ ở backend: Không cho phép ngày sinh ở tương lai
	                if (ngaySinh.isAfter(LocalDate.now())) {
	                    return org.springframework.http.ResponseEntity.badRequest().body("Ngày sinh không hợp lệ");
	                }
	                
	                user.setNgaySinh(ngaySinh);
	                nguoiDungRepo.save(user); 
	                return org.springframework.http.ResponseEntity.ok("Cập nhật ngày sinh thành công");
	                
	            } catch (DateTimeParseException e) {
	                return org.springframework.http.ResponseEntity.badRequest().body("Định dạng ngày không hợp lệ");
	            }
	        }
	        
	        return org.springframework.http.ResponseEntity.badRequest().body("Bạn chưa đăng nhập");
	    }
	    @PostMapping("/truyen/{id}/chu-thich")
	    @PreAuthorize("isAuthenticated()")
	    public String capNhatChuThich(@PathVariable Long id, 
	                                  @RequestParam("chuThich") String chuThich) {
	        Truyen truyen = truyenService.findById(id);
	        NguoiDung user = securityUtil.getCurrentUserFromDB();
	        
	        // Kiểm tra xem người đang đăng nhập có đúng là tác giả không
	        if (truyen != null && user != null && truyen.getNguoiDang().getId().equals(user.getId())) {
	            
	            // GỌI QUA TẦNG SERVICE (Chuẩn bài 3 lớp)
	            truyenService.capNhatChuThich(id, chuThich);
	        }
	        
	        return "redirect:/DustNovel/truyen/" + id;
	    }
	    
	    @PostMapping("/truyen/{id}/danh-gia")
	    @PreAuthorize("isAuthenticated()")
	    public String guiDanhGia(@PathVariable Long id, 
	                             @RequestParam("soSao") Integer soSao, 
	                             @RequestParam("noiDung") String noiDung) {
	                             
	        Truyen truyen = truyenService.findById(id);
	        NguoiDung user = securityUtil.getCurrentUserFromDB();
	        
	        // Ràng buộc số sao từ 1 đến 5 để chống hack qua F12 (Inspect Element)
	        if (soSao < 1) soSao = 1;
	        if (soSao > 5) soSao = 5;

	        if (truyen != null && user != null) {
	            danhGiaService.luuDanhGia(user, truyen, soSao, noiDung);
	        }
	        
	        // Gửi xong load lại trang truyện để thấy đánh giá vừa hiện lên
	        return "redirect:/DustNovel/truyen/" + id;
	    }
	    
	    @PostMapping("/truyen/{truyenId}/danh-gia/xoa/{dgId}")
	    @PreAuthorize("isAuthenticated()") // Bắt buộc đăng nhập
	    public String xoaDanhGia(@PathVariable Long truyenId, @PathVariable Long dgId) {
	        
	        NguoiDung currentUser = securityUtil.getCurrentUserFromDB();
	        
	        // Gọi xuống Service để xóa (Nhớ viết thêm logic check xem đánh giá đó có đúng là của currentUser không ở Service nhé để chống hack)
	        danhGiaService.xoaDanhGia(dgId, currentUser.getId());
	        
	        // Xóa xong thì load lại trang truyện đó
	        return "redirect:/DustNovel/truyen/" + truyenId;
	    }
	    @PostMapping("/dbu/truyen/xoa/{id}")
	    @PreAuthorize("@permissionService.canDeleteTruyen(#id)")
	    public String xoaTruyenDBU(
	            @PathVariable Long id,
	            @RequestParam("type") String type,
	            RedirectAttributes redirectAttributes
	    ) {

	        Truyen truyen = truyenService.findById(id);
	        String tenTruyen = (truyen != null) ? truyen.getTenTruyen() : "";

	        truyenService.xoaTruyen(id);

	        redirectAttributes.addFlashAttribute(
	            "successMessage",
	            "Bạn đã xóa truyện \"" + tenTruyen + "\" thành công!"
	        );

	        // 🔥 redirect theo loại
	        if ("dich".equals(type)) {
	            return "redirect:/dbu/truyen-dich";
	        } else {
	            return "redirect:/dbu/truyen-sang-tac";
	        }
	    }
	    
	    
}
