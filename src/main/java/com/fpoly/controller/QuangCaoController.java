package com.fpoly.controller;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.fpoly.model.Banner;
import com.fpoly.model.NguoiDung;
import com.fpoly.model.Truyen;
import com.fpoly.repository.BannerRepository;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.repository.ThueBannerRepository;
import com.fpoly.repository.TruyenRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import org.springframework.ui.Model;
import java.io.File;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/quang-cao")
public class QuangCaoController {	
	@Autowired
    TruyenRepository truyenRepository;
	@Autowired
    NguoiDungRepository nguoiDungRepository;
	@Autowired
	BannerRepository bannerRepository;
	@Autowired
	ThueBannerRepository thueBannerRepository;
	
	
	
	@GetMapping("/")
    public String quangCao(Authentication authentication,Model model) {
		
		String username = authentication.getName();
        
	    Optional<NguoiDung> userOpt = nguoiDungRepository.findByTenDangNhap(username);
	    
	    if(userOpt.isEmpty()){
	        return "redirect:/login";
	    }
	    NguoiDung user = userOpt.get();
	    
	    List<Truyen> listTruyen = truyenRepository.findByNguoiDang_Id(user.getId());
	    List<Banner> listBanner = bannerRepository.findAll();
        
        model.addAttribute("listTruyen", listTruyen);       
        model.addAttribute("listBanner", listBanner);
		model.addAttribute("content", "/QuangCao/QuangCao");
		
        return "/layout/main";
    }
	
	
	
	
	@PostMapping("/TaoQuangCao")
	public String taoQuangCao(
	        Authentication authentication,
	        @RequestParam(required = false) Long truyenId,
	        @RequestParam(required = false) String viTri,
	        @RequestParam("file") MultipartFile file,
	        @RequestParam(required = false) Long tokenMoiNgay,
	        @RequestParam String startDate,
	        @RequestParam(required = false) Integer soNgayChay,
	        Model model, RedirectAttributes redirectAttributes) throws IOException {

	    boolean hasError = false;

	    if(truyenId == null){
	        model.addAttribute("errorTruyen","Vui lòng chọn truyện");
	        hasError = true;
	    }

	    if(viTri == null || viTri.isEmpty()){
	        model.addAttribute("errorViTri","Vui lòng chọn vị trí");
	        hasError = true;
	    }

	    if(file == null || file.isEmpty()){
	        model.addAttribute("errorFile","Vui lòng chọn ảnh banner");
	        hasError = true;
	    }

	    if(tokenMoiNgay == null){
	        model.addAttribute("errorToken","Vui lòng nhập token mỗi ngày");
	        hasError = true;
	    }
	    else if(tokenMoiNgay < 1000){
	        model.addAttribute("errorToken","Token phải lớn hơn hoặc bằng 1000");
	        hasError = true;
	    }
	    else if(tokenMoiNgay > 50000000){
	        model.addAttribute("errorToken","Chỉ được chạy quảng cáo với số token nhỏ hơn hoặc bằng 50000000");
	        hasError = true;
	    }

	    if(startDate == null || startDate.isEmpty()){
	        model.addAttribute("errorStartDate","Vui lòng chọn ngày bắt đầu");
	        hasError = true;
	    }

	    if(startDate != null && !startDate.isEmpty()){

	        LocalDate start = LocalDate.parse(startDate);
	        LocalDate today = LocalDate.now();

	        if(start.isBefore(today)){
	            model.addAttribute("errorStartDate","Ngày bắt đầu phải từ hôm nay trở đi");
	            hasError = true;
	        }

	    }

	    if(soNgayChay == null){
	        model.addAttribute("errorSoNgay","Vui lòng nhập số ngày chạy");
	        hasError = true;
	    }
	    else if(soNgayChay < 1){
	        model.addAttribute("errorSoNgay","Số ngày chạy phải lớn hơn 0");
	        hasError = true;
	    }
	    else if(soNgayChay > 365){
	        model.addAttribute("errorSoNgay","Chỉ được chạy tối đa 365 ngày");
	        hasError = true;
	    }

	    String username = authentication.getName();

	    Optional<NguoiDung> userOpt = nguoiDungRepository.findByTenDangNhap(username);

	    if(userOpt.isEmpty()){
	        return "redirect:/login";
	    }

	    NguoiDung user = userOpt.get();

	    List<Truyen> listTruyen = truyenRepository.findByNguoiDang_Id(user.getId());

	    if(hasError){
	        model.addAttribute("listTruyen", listTruyen);
	        model.addAttribute("listBanner", bannerRepository.findAll());
	        model.addAttribute("content", "/QuangCao/QuangCao");
	        model.addAttribute("truyenId", truyenId);
	        model.addAttribute("viTri", viTri);
	        model.addAttribute("tokenMoiNgay", tokenMoiNgay);
	        model.addAttribute("startDate", startDate);
	        model.addAttribute("soNgayChay", soNgayChay);
	        return "/layout/main";
	    }


	    String fileName = System.currentTimeMillis() + "_banner_" + file.getOriginalFilename();

	    String uploadDir = System.getProperty("user.dir")
	            + "/src/main/resources/static/uploads/banner/";

	    File dir = new File(uploadDir);
	    if (!dir.exists()) {
	        dir.mkdirs(); 
	    }

	    File saveFile = new File(uploadDir + fileName);
	    file.transferTo(saveFile);

	    Banner banner = new Banner();
	    Truyen truyen = truyenRepository.findById(truyenId).get();
	    banner.setTruyen(truyen);
	    banner.setViTri(viTri);

	    banner.setAnhBanner("/uploads/banner/" + fileName);
	    banner.setTokenMoiNgay(tokenMoiNgay);


	    LocalDate startDateParsed = LocalDate.parse(startDate);

	    LocalDateTime start;

	    if (startDateParsed.equals(LocalDate.now())) {
	        start = LocalDateTime.now();
	    } else {
	    	start = startDateParsed.atTime(LocalDateTime.now().toLocalTime());
	    }

	    LocalDateTime end = start.plusDays(soNgayChay);

	    banner.setNgayBatDau(start);
	    banner.setNgayKetThuc(end);
	    banner.setGioTao(start.toLocalTime());

	    LocalDateTime now = LocalDateTime.now();

	    if (now.isBefore(start)) {
	        banner.setTrangThai("CHO_CHAY");
	    } 
	    else if (now.isAfter(end)) {
	        banner.setTrangThai("HET_HAN");
	    } 
	    else {
	        banner.setTrangThai("HOAT_DONG");
	    }
	    
	    bannerRepository.save(banner);
	    redirectAttributes.addFlashAttribute("successMessage", "Bạn đã tạo quảng cáo thành công !");

	    return "redirect:/quang-cao/";
	}
	
	
	
		
	
	
	
	@GetMapping("LichSu/QuangCao")
	public String LichSuQuangCao(Authentication authentication, Model model) {

	    String username = authentication.getName();

	    Optional<NguoiDung> userOpt = nguoiDungRepository.findByTenDangNhap(username);

	    if(userOpt.isEmpty()){
	        return "redirect:/login";
	    }

	    NguoiDung user = userOpt.get();

	    List<Truyen> listTruyen = truyenRepository.findByNguoiDang_Id(user.getId());

	    List<Banner> listBanner = bannerRepository.findByTruyenIdIn(
	            listTruyen.stream().map(Truyen::getId).toList()
	    );

	    LocalDateTime now = LocalDateTime.now();

	    for (Banner b : listBanner) {

	        long phutDaChay = 0;
	        long phutConLai = 0;
	        long tokenDaTru = 0;

	        long tokenDaDung = (b.getTokenDaDung() == null) ? 0 : b.getTokenDaDung();

	        if (b.getNgayBatDau() != null) {

	            /* =========================
	                HOAT_DONG (REALTIME)
	               ========================= */
	        	if ("HOAT_DONG".equals(b.getTrangThai())) {

	        	    LocalDateTime start = b.getNgayBatDau(); 
	        	    LocalDateTime end = b.getNgayKetThuc();
	        	    LocalDateTime current = now.isAfter(end) ? end : now;

	        	    long seconds = Duration.between(start, current).getSeconds();
	        	    if (seconds < 0) seconds = 0;

	        	    phutDaChay = seconds / 60;

	        	    long totalMinutes = (long) Math.ceil(Duration.between(start, end).getSeconds() / 60.0);
	        	    phutConLai = totalMinutes - phutDaChay;
	        	    if (phutConLai < 0) phutConLai = 0;

	        	    /* =========================
	        	       TOKEN REALTIME 
	        	       ========================= */

	        	    LocalDateTime last = b.getLastUpdateTime();

	        	    if(last == null){
	        	        last = b.getNgayBatDau();
	        	    }

	        	    long secondsChuaLuu = Duration.between(last, now).getSeconds();
	        	    if(secondsChuaLuu < 0) secondsChuaLuu = 0;

	        	    double tokenPerSecond = b.getTokenMoiNgay() / 86400.0;

	        	    long tokenRealtime = (long) (secondsChuaLuu * tokenPerSecond);

	        	    // tổng token 
	        	    tokenDaTru = b.getTokenDaDung() + tokenRealtime;
	        	}

	            /* =========================
	                TAM_DUNG
	               ========================= */
	            else if ("TAM_DUNG".equals(b.getTrangThai())) {

	                // giữ token đã dùng trước đó
	                tokenDaTru = tokenDaDung;

	                if (b.getThoiGianConLai() != null) {
	                    phutConLai = b.getThoiGianConLai();
	                }

	                phutDaChay = 0;
	            }

	        	/* =========================
	        	   CHO_CHAY (chưa chạy)
	        	   ========================= */
	        	else if ("CHO_CHAY".equals(b.getTrangThai())) {

	        	    phutDaChay = 0;

	        	    phutConLai = Duration
	        	            .between(b.getNgayBatDau(), b.getNgayKetThuc())
	        	            .toMinutes();

	        	    if (phutConLai < 0) phutConLai = 0;

	        	    tokenDaTru = 0;
	        	}

	        	/* =========================
	        	   HET_HAN (đã chạy xong)
	        	   ========================= */        	
	        	
	        	else if ("HET_HAN".equals(b.getTrangThai())) {

	        	    long seconds = Duration
	        	            .between(b.getNgayBatDau(), b.getNgayKetThuc())
	        	            .getSeconds();

	        	    if (seconds < 0) seconds = 0;

	        	    phutDaChay = seconds / 60;

	        	    //  Ưu tiên lấy token đã lưu trong DB
	        	    tokenDaTru = (b.getTokenDaDung() == null)
	        	            ? b.getTokenMoiNgay() 
	        	            : b.getTokenDaDung();

	        	    phutConLai = 0;
	        	}
	        	
	        }

	        model.addAttribute("tokenDaTru_" + b.getId(), tokenDaTru);
	        model.addAttribute("phutDaChay_" + b.getId(), phutDaChay);
	        model.addAttribute("phutConLai_" + b.getId(), phutConLai);
	    }

	    model.addAttribute("listBanner", listBanner);
	    model.addAttribute("content", "/QuangCao/LichSuQuangCao");

	    return "/layout/main";
	}
	
	

	
	
	
	@GetMapping("/sua/{id}")
	public String suaQuangCao(@PathVariable Long id, Model model){

	    Banner banner = bannerRepository.findById(id).orElse(null);

	    if(banner == null){
	        return "redirect:/quang-cao/LichSu/QuangCao";
	    }

	    model.addAttribute("banner", banner);
	    model.addAttribute("content","QuangCao/UpdateQuangCao");

	    return "layout/main";
	}

	
	
	

	
	@PostMapping("/update")
	public String updateQuangCao(
	        @RequestParam Long id,
	        @RequestParam(required = false) Long tokenMoiNgay,
	        @RequestParam(required = false) String viTri,
	        @RequestParam(required = false) String viTriSelect,
	        @RequestParam(required = false) String ngayBatDau, 
	        @RequestParam(required = false) String ngayKetThuc,
	        @RequestParam(required = false) String trangThai,
	        @RequestParam(required = false) Long truyenId,
	        Authentication authentication,
	        Model model,
	        RedirectAttributes redirectAttributes){

	    Banner banner = bannerRepository.findById(id).orElse(null);

	    if(banner == null){
	        return "redirect:/quang-cao/LichSu/QuangCao";
	    }

	    LocalDateTime now = LocalDateTime.now();

	    /* =========================
	        TẠM DỪNG (GIỮ NGUYÊN)
	       ========================= */
	    if("TAM_DUNG".equals(trangThai) && "HOAT_DONG".equals(banner.getTrangThai())){

	        long secondsConLai = Duration
	                .between(now, banner.getNgayKetThuc())
	                .getSeconds();

	        long phutConLai = (long) Math.ceil(secondsConLai / 60.0);

	        if(phutConLai < 0){
	            phutConLai = 0;
	        }

	        banner.setThoiGianConLai(phutConLai);
	        banner.setNgayKetThuc(now);

	        banner.setTrangThai("TAM_DUNG");
	        banner.setLastUpdateTime(now);

	        bannerRepository.save(banner);

	        return "redirect:/quang-cao/LichSu/QuangCao";
	    }

	    /* =========================
	       CHẠY LẠI
	       ========================= */
	    if("HOAT_DONG".equals(trangThai)
	            && "TAM_DUNG".equals(banner.getTrangThai())
	            && banner.getThoiGianConLai() != null){

	        banner.setNgayBatDau(now);

	        LocalDateTime ketThucMoi =
	                now.plusMinutes(banner.getThoiGianConLai());

	        banner.setNgayKetThuc(ketThucMoi);

	        banner.setThoiGianConLai(null);

	        banner.setTrangThai("HOAT_DONG");
	        banner.setLastUpdateTime(now);

	        bannerRepository.save(banner);

	        return "redirect:/quang-cao/LichSu/QuangCao";
	    }

	    /* =========================
	        UPDATE THÔNG TIN
	       ========================= */


	    
	    boolean hasError = false;

	 // =====  VALIDATE RỖNG =====
	 if(tokenMoiNgay == null){
	     model.addAttribute("errorToken","Vui lòng nhập token");
	     hasError = true;
	 }

	 if(ngayBatDau == null || ngayBatDau.isEmpty()){
	     model.addAttribute("errorStart","Vui lòng chọn ngày bắt đầu");
	     hasError = true;
	 }

	 if(ngayKetThuc == null || ngayKetThuc.isEmpty()){
	     model.addAttribute("errorNgay","Vui lòng chọn ngày kết thúc");
	     hasError = true;
	 }
     
	 // =====  PARSE NGÀY (KHÔNG phụ thuộc hasError) =====
	 LocalDateTime startDate = null;
	 LocalDateTime endDate = null;

	// ===== START DATE =====
	 if (ngayBatDau != null && !ngayBatDau.isEmpty()) {
	     LocalDate startDateParsed = LocalDate.parse(ngayBatDau);

	     if (startDateParsed.isEqual(LocalDate.now())) {
	         //  chạy ngay
	         startDate = LocalDateTime.now();
	     } else {
	         //  giữ giờ cũ của banner (không dùng thoiGianCapNhat)
	         LocalTime time = banner.getNgayBatDau() != null
	                 ? banner.getNgayBatDau().toLocalTime()
	                 : LocalTime.now();

	         startDate = startDateParsed.atTime(time);
	     }
	 }


	// ===== END DATE =====
	 if (ngayKetThuc != null && !ngayKetThuc.isEmpty()) {

		    LocalDate endDateParsed = LocalDate.parse(ngayKetThuc);

		    LocalDate oldEndDate = banner.getNgayKetThuc() != null
		            ? banner.getNgayKetThuc().toLocalDate()
		            : null;

		    boolean userChangedEndDate = (oldEndDate == null)
		            || !endDateParsed.isEqual(oldEndDate);

		    if (!userChangedEndDate) {
		        // người dùng không sửa ngày kết thúc → giữ duration theo số ngày cũ
		        long soNgay = ChronoUnit.DAYS.between(
		                banner.getNgayBatDau().toLocalDate(),
		                banner.getNgayKetThuc().toLocalDate()
		        ) + 1;

		        // giữ giờ phút giây của startDate
		        endDate = startDate.plusDays(soNgay - 1);

		    } else {
		        // người dùng sửa ngày kết thúc → lấy giờ phút giây hiện tại hoặc giờ cũ
		        LocalTime time;
		        if (banner.getNgayKetThuc() != null && "HOAT_DONG".equals(banner.getTrangThai())) {
		            // đã chạy → giữ giờ cũ
		            time = banner.getNgayKetThuc().toLocalTime();
		        } else {
		            // chưa chạy hoặc tạo mới → lấy giờ phút giây hiện tại
		            time = LocalTime.now();
		        }

		        endDate = endDateParsed.atTime(time);
		    }
		}
		

	 // ===== VALIDATE LOGIC =====
	 LocalDate today = LocalDate.now();

	 if(startDate != null && startDate.toLocalDate().isBefore(today)){
	     model.addAttribute("errorStart","Ngày bắt đầu phải từ hôm nay trở đi");
	     hasError = true;
	 }

	 if(tokenMoiNgay != null && tokenMoiNgay < 1000){
	     model.addAttribute("errorToken","Token phải >= 1000");
	     hasError = true;
	 }

	 if(tokenMoiNgay != null && tokenMoiNgay > 50000000){
	     model.addAttribute("errorToken","Token không được vượt quá 50,000,000");
	     hasError = true;
	 }

	 if(startDate != null && endDate != null && !endDate.isAfter(startDate)){
	     model.addAttribute("errorNgay","Ngày kết thúc phải sau ngày bắt đầu");
	     hasError = true;
	 }

	    String username = authentication.getName();
	    Optional<NguoiDung> userOpt =
	            nguoiDungRepository.findByTenDangNhap(username);

	    if(userOpt.isEmpty()){
	        return "redirect:/login";
	    }

	    NguoiDung user = userOpt.get();

	    long soNgay = 0;
	    long tokenThem = 0;
	    long tongToken = 0;

	    if(!hasError){

	        soNgay = ChronoUnit.DAYS.between(
	                startDate.toLocalDate(),
	                endDate.toLocalDate()) + 1;

	        tokenThem = tokenMoiNgay - banner.getTokenMoiNgay();
	        tongToken = tokenThem * soNgay;
	    }

	    if(hasError){

	        String finalViTri = (viTriSelect != null && !viTriSelect.isEmpty())
	                ? viTriSelect
	                : viTri;

	        banner.setViTri(finalViTri);
	        banner.setTokenMoiNgay(tokenMoiNgay);
	        banner.setNgayBatDau(startDate);
	        banner.setNgayKetThuc(endDate);

           
	        model.addAttribute("ngayBatDau", ngayBatDau);
	        model.addAttribute("ngayKetThuc", ngayKetThuc);
	        model.addAttribute("banner", banner);
	        model.addAttribute("content","QuangCao/UpdateQuangCao");

	        return "layout/main";
	    }

	    if(tokenThem > 0){
	        user.setToken(user.getToken() - tongToken);
	        nguoiDungRepository.save(user);
	    }

	    banner.setTokenMoiNgay(tokenMoiNgay);
	    banner.setNgayBatDau(startDate); 
	    banner.setNgayKetThuc(endDate);

	    // vị trí
	    String finalViTri = (viTriSelect != null && !viTriSelect.isEmpty())
	            ? viTriSelect
	            : viTri;

	    if(finalViTri != null && finalViTri.contains(",")){
	        finalViTri = finalViTri.split(",")[0];
	    }

	    banner.setViTri(finalViTri);

	    // update truyện
	    if(truyenId != null){
	        Truyen truyen = truyenRepository.findById(truyenId).orElse(null);
	        if(truyen != null){
	            banner.setTruyen(truyen);
	        }
	    }

	    banner.setLastUpdateTime(now);

	    bannerRepository.save(banner);

	    redirectAttributes.addFlashAttribute(
	            "successMessage",
	            "Cập nhật thành công"
	    );

	    return "redirect:/quang-cao/LichSu/QuangCao";
	}
	
	
	
	
	@GetMapping("/xoa/{id}")
	public String xoaQuangCao(@PathVariable Long id,
	                          RedirectAttributes redirectAttributes){

	    bannerRepository.deleteById(id);

	    redirectAttributes.addFlashAttribute("successMessage","Đã xóa quảng cáo");

	    return "redirect:/quang-cao/LichSu/QuangCao";
	}
	
	
	
	
	
	@GetMapping("/thueQuangCao")
	public String ThueQuangCao(Authentication authentication, Model model) {
		String username = authentication.getName();

		Optional<NguoiDung> userOpt = nguoiDungRepository.findByTenDangNhap(username);

		if (userOpt.isEmpty()) {
			return "redirect:/login";
		}

		NguoiDung user = userOpt.get();

		List<Truyen> listTruyen = truyenRepository.findByNguoiDang_Id(user.getId());

		model.addAttribute("listTruyen", listTruyen);
		model.addAttribute("content", "quangcao/ThueQuangCao");
		return "/layout/main";
	}

	
	@GetMapping("LichSu/ThueQuangCao")
	public String LichSuThueQuangCao(Authentication authentication, Model model) {
		model.addAttribute("content", "/QuangCao/LichSuThueQuangCao");
		return "/layout/main";
	}

}
