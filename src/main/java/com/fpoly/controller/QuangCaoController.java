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
import com.fpoly.repository.TruyenRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.springframework.ui.Model;
import java.io.File;
import java.io.IOException;
import java.text.Normalizer;

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
	
	
	
	
	public static String removeAccent(String input) {
	    if (input == null) return "";
	    String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
	    return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
	                     .replace("đ", "d")
	                     .replace("Đ", "D");
	}
	
	
	
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
	    
	    if(truyenId != null && viTri != null){

	        boolean daTonTai = bannerRepository
	            .existsByTruyen_IdAndViTriAndTrangThaiIn(
	                truyenId,
	                viTri,
	                List.of("CHO_CHAY", "HOAT_DONG", "TAM_DUNG")
	            );

	        if(daTonTai){
	            model.addAttribute("errorViTri","Truyện bạn đang chọn đã có quảng cáo ở vị trí này");
	            hasError = true;
	        }
	    }

	    if(viTri == null || viTri.isEmpty()){
	        model.addAttribute("errorViTri","Vui lòng chọn vị trí");
	        hasError = true;
	    }

	    if(file == null || file.isEmpty()){
	        model.addAttribute("errorFile","Vui lòng chọn ảnh banner");
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

	    long tongToken = soNgayChay * tokenMoiNgay;

	    if(user.getToken() < tongToken){
	        model.addAttribute("errorToken","Bạn không đủ token");

	        model.addAttribute("listTruyen", listTruyen);
	        model.addAttribute("listBanner", bannerRepository.findAll());
	        model.addAttribute("content", "/QuangCao/QuangCao");

	        return "/layout/main";
	    }

	    user.setToken(user.getToken() - tongToken);
	    nguoiDungRepository.save(user);

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
	    banner.setTokenDaDung(tongToken);
	    banner.setSoNgayChay(soNgayChay.longValue());

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
	public String LichSuQuangCao(@RequestParam(required = false) String keyword,Authentication authentication, Model model) {

	    String username = authentication.getName();

	    Optional<NguoiDung> userOpt = nguoiDungRepository.findByTenDangNhap(username);

	    if(userOpt.isEmpty()){
	        return "redirect:/login";
	    }

	    NguoiDung user = userOpt.get();
	    
	    
	    // check ADMIN / USER
	    boolean isAdmin = authentication.getAuthorities().stream()
	            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

	    model.addAttribute("isAdmin", isAdmin);
	    model.addAttribute("currentUser", user);


	    // PHÂN QUYỀN LIST TRUYỆN
	    // =========================
	    List<Truyen> listTruyen;

	    if (isAdmin) {
	        listTruyen = truyenRepository.findAll(); // ADMIN xem tất cả truyện
	    } else {
	        listTruyen = truyenRepository.findByNguoiDang_Id(user.getId()); // USER chỉ xem của mình
	    }

		/*
		 * List<Truyen> listTruyen = truyenRepository.findByNguoiDang_Id(user.getId());
		 */

	    List<Banner> listBanner = bannerRepository.findByTruyenIdIn(
	            listTruyen.stream().map(Truyen::getId).toList()
	    );
	    
	    
	    List<Long> truyenIds = listTruyen.stream()
	            .map(Truyen::getId)
	            .toList();


	    if(keyword != null && !keyword.trim().isEmpty()){

	        String keywordNoAccent = removeAccent(keyword).toLowerCase();

	        listBanner = bannerRepository.findByTruyenIdIn(truyenIds)
	                .stream()
	                .filter(b -> {
	                    String ten = removeAccent(b.getTruyen().getTenTruyen()).toLowerCase();
	                    return ten.contains(keywordNoAccent);
	                })
	                .toList();

	    } else {	    
	        listBanner = bannerRepository.findByTruyenIdIn(truyenIds);
	    }

	    LocalDateTime now = LocalDateTime.now();

	    for (Banner b : listBanner) {

	        long phutDaChay = 0;
	        long phutConLai = 0;
	        long tokenDaTru = 0;

	        long tokenDaDung = (b.getTokenDaDung() == null) ? 0 : b.getTokenDaDung();

	        long tongToken = 0;
	        if(b.getSoNgayChay() != null){
	            tongToken = b.getSoNgayChay() * b.getTokenMoiNgay();
	        }

	        if (b.getNgayBatDau() != null) {

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

	                tokenDaTru = tokenDaDung;
	            }

	            else if ("TAM_DUNG".equals(b.getTrangThai())) {

	                tokenDaTru = tokenDaDung;

	                if (b.getThoiGianConLai() != null) {
	                    phutConLai = b.getThoiGianConLai();
	                }

	                phutDaChay = 0;
	            }

	            else if ("CHO_CHAY".equals(b.getTrangThai())) {

	                phutDaChay = 0;

	                phutConLai = Duration
	                        .between(b.getNgayBatDau(), b.getNgayKetThuc())
	                        .toMinutes();

	                if (phutConLai < 0) phutConLai = 0;

//	                tokenDaTru = 0;
	                tokenDaTru = b.getTokenDaDung() == null ? 0 : b.getTokenDaDung();
	            }

	            else if ("HET_HAN".equals(b.getTrangThai())) {

	                long seconds = Duration
	                        .between(b.getNgayBatDau(), b.getNgayKetThuc())
	                        .getSeconds();

	                if (seconds < 0) seconds = 0;

	                phutDaChay = seconds / 60;

	                tokenDaTru = (b.getTokenDaDung() == null)
	                        ? tongToken
	                        : b.getTokenDaDung();

	                phutConLai = 0;
	            }
	        }

	        model.addAttribute("tokenDaTru_" + b.getId(), tokenDaTru);
	        model.addAttribute("phutDaChay_" + b.getId(), phutDaChay);
	        model.addAttribute("phutConLai_" + b.getId(), phutConLai);
	        model.addAttribute("tongToken_" + b.getId(), tongToken);
	    }

	    model.addAttribute("listBanner", listBanner);
	    model.addAttribute("content", "/QuangCao/LichSuQuangCao");

	    return "/layout/main";
	}
	
	

	
	
	
	@GetMapping("/sua/{id}")
	public String suaQuangCao(@PathVariable Long id, Model model, Authentication authentication){

	    Banner banner = bannerRepository.findById(id).orElse(null);

	    if(banner == null){
	        return "redirect:/quang-cao/LichSu/QuangCao";
	    }
	    
	    String username = authentication.getName();
	    Optional<NguoiDung> userOpt = nguoiDungRepository.findByTenDangNhap(username);
	    
	    if(userOpt.isEmpty()){
	        return "redirect:/login";
	    }

	    NguoiDung user = userOpt.get();

	    //  LẤY DANH SÁCH TRUYỆN CỦA USER
	    List<Truyen> listTruyen = truyenRepository.findByNguoiDang_Id(user.getId());

	    model.addAttribute("banner", banner);
	    model.addAttribute("listTruyen", listTruyen);
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
	        @RequestParam(required = false) Long soNgayChay, 
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
//	        banner.setNgayKetThuc(now);

	        banner.setTrangThai("TAM_DUNG");
	        banner.setLastUpdateTime(now);

	        bannerRepository.save(banner);
	        
	        redirectAttributes.addFlashAttribute(
	                "successMessage",
	                "Đã tạm dừng quảng cáo"
	            );

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
	        
	        redirectAttributes.addFlashAttribute(
	                "successMessage",
	                "Đã chạy lại quảng cáo"
	            );

	        return "redirect:/quang-cao/LichSu/QuangCao";
	    }

	    /* =========================
	        UPDATE THÔNG TIN
	       ========================= */

	    boolean hasError = false;

	    if(tokenMoiNgay == null){
	        model.addAttribute("errorToken","Vui lòng nhập token");
	        hasError = true;
	    }

	    if(ngayBatDau == null || ngayBatDau.isEmpty()){
	        model.addAttribute("errorMessage","Vui lòng chọn ngày bắt đầu");
	        hasError = true;
	    }

	    if(soNgayChay == null || soNgayChay <= 0){
	        model.addAttribute("errorMessage","Số ngày chạy phải >= 1");
	        hasError = true;
	    }

	    // ===== PARSE START DATE =====
	    LocalDateTime startDate = banner.getNgayBatDau();

	    if (ngayBatDau != null && !ngayBatDau.isEmpty()) {
	        LocalDate d = LocalDate.parse(ngayBatDau);
	        startDate = d.atTime(startDate.toLocalTime());
	    }

	    // ===== TÍNH END DATE TỪ SỐ NGÀY =====
	    LocalDateTime endDate = startDate.plusDays(soNgayChay);

	    // ===== VALIDATE =====
	    if(startDate != null && startDate.toLocalDate().isBefore(LocalDate.now())){
	        model.addAttribute("errorMessage","Ngày bắt đầu phải >= hôm nay");
	        hasError = true;
	    }
	    
	    String viTriCheck = (viTriSelect != null && !viTriSelect.isEmpty())
	            ? viTriSelect
	            : viTri;

	    Long finalTruyenId = (truyenId != null) ? truyenId : banner.getTruyen().getId();

	    if(finalTruyenId != null && viTriCheck != null){

	        boolean daTonTai = bannerRepository
	            .findAll()
	            .stream()
	            .anyMatch(b -> 
	                !b.getId().equals(banner.getId()) &&
	                b.getTruyen().getId().equals(finalTruyenId) &&
	                b.getViTri().equals(viTriCheck) &&
	                !b.getTrangThai().equals("HET_HAN")
	            );

	        if(daTonTai){
	            redirectAttributes.addFlashAttribute(
	                "errorMessage",
	                "Truyện bạn đang chọn đã có quảng cáo ở vị trí này"
	            );
	            return "redirect:/quang-cao/sua/" + id;
	        }
	    }

	    String username = authentication.getName();
	    Optional<NguoiDung> userOpt =
	            nguoiDungRepository.findByTenDangNhap(username);

	    if(userOpt.isEmpty()){
	        return "redirect:/login";
	    }

	    NguoiDung user = userOpt.get();

	    // ===== TÍNH NGÀY =====
	    long soNgayCu = ChronoUnit.DAYS.between(
	            banner.getNgayBatDau().toLocalDate(),
	            banner.getNgayKetThuc().toLocalDate());

	    long soNgayMoi = soNgayChay;

	    long tokenCu = banner.getTokenMoiNgay();

	    if(hasError){
	    	
	    	List<Truyen> listTruyen =
	                truyenRepository.findByNguoiDang_Id(user.getId());
	    	
	        model.addAttribute("banner", banner);
	        model.addAttribute("listTruyen", listTruyen);
	        model.addAttribute("tokenMoiNgay", tokenMoiNgay);

	        model.addAttribute("content","QuangCao/UpdateQuangCao");
	        return "layout/main";
	    }
	    
	 // giá trị cũ
	    Long tokenCuCheck = banner.getTokenMoiNgay();

	    long soNgayCuCheck = ChronoUnit.DAYS.between(
	            banner.getNgayBatDau().toLocalDate(),
	            banner.getNgayKetThuc().toLocalDate()
	    );

	    // parse lại start date mới để so sánh
	    LocalDateTime startDateCheck = banner.getNgayBatDau();
	    if (ngayBatDau != null && !ngayBatDau.isEmpty()) {
	        LocalDate d = LocalDate.parse(ngayBatDau);
	        startDateCheck = d.atTime(startDateCheck.toLocalTime());
	    }

	    // vị trí cuối
	    String finalViTriCheck = (viTriSelect != null && !viTriSelect.isEmpty())
	            ? viTriSelect
	            : viTri;

	    // truyện cuối
	    Long finalTruyenIdCheck = (truyenId != null)
	            ? truyenId
	            : banner.getTruyen().getId();

	    // CHECK tất cả nếu không đổi
	    boolean isSame =
	            tokenMoiNgay.equals(tokenCuCheck)
	            && soNgayChay == soNgayCuCheck
	            && startDateCheck.toLocalDate().equals(banner.getNgayBatDau().toLocalDate())
	            && finalViTriCheck.equals(banner.getViTri())
	            && finalTruyenIdCheck.equals(banner.getTruyen().getId());

	    if(isSame){
	    	return "redirect:/quang-cao/LichSu/QuangCao";     
	    }

	    // =========================
	    // LOGIC TIỀN
	    // =========================

	    // TĂNG NGÀY
	    if (soNgayMoi > soNgayCu) {
	        long tienThem = (soNgayMoi - soNgayCu) * tokenMoiNgay;

	        if (user.getToken() < tienThem) {
	            redirectAttributes.addFlashAttribute(
	                    "errorMessage",
	                    "Không đủ token! Cần thêm " + (tienThem - user.getToken())
	            );
	            return "redirect:/quang-cao/sua/" + id;
	        }

	        user.setToken(user.getToken() - tienThem);
	    }

	    // GIẢM NGÀY → HOÀN NGAY
	    if (soNgayMoi < soNgayCu) {
	        long tienHoan = (soNgayCu - soNgayMoi) * tokenCu;
	        user.setToken(user.getToken() + tienHoan);
	    }

	    //  TĂNG TOKEN/NGÀY
	    if (tokenMoiNgay > tokenCu) {
	        long tienThem = (tokenMoiNgay - tokenCu) * soNgayMoi;

	        if (user.getToken() < tienThem) {
	            redirectAttributes.addFlashAttribute(
	                    "errorMessage",
	                    "Không đủ token! Cần thêm " + (tienThem - user.getToken())
	            );
	            return "redirect:/quang-cao/sua/" + id;
	        }

	        user.setToken(user.getToken() - tienThem);
	    }

	    // =========================
	    // UPDATE DATA
	    // =========================
	    banner.setNgayBatDau(startDate);
	    banner.setNgayKetThuc(endDate);
	    banner.setTokenMoiNgay(tokenMoiNgay);
	    banner.setSoNgayChay(soNgayChay);

	    String finalViTri = (viTriSelect != null && !viTriSelect.isEmpty())
	            ? viTriSelect
	            : viTri;

	    banner.setViTri(finalViTri);

	    if(truyenId != null){
	        Truyen truyen = truyenRepository.findById(truyenId).orElse(null);
	        if(truyen != null){
	            banner.setTruyen(truyen);
	        }
	    }

	    banner.setLastUpdateTime(now);
	    
	    long tongTokenMoi = soNgayChay * tokenMoiNgay;
	    banner.setTokenDaDung(tongTokenMoi);

	    bannerRepository.save(banner);
	    nguoiDungRepository.save(user);

	    redirectAttributes.addFlashAttribute(
	            "successMessage",
	            "Cập nhật thành công"
	    );

	    return "redirect:/quang-cao/LichSu/QuangCao";
	}
	
	
	
	
	@GetMapping("/xoa/{id}")
	public String xoaQuangCao(@PathVariable Long id,
	                          RedirectAttributes redirectAttributes){
		
		Banner banner = bannerRepository.findById(id).orElse(null);

	    if (banner == null) {
	        redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy quảng cáo");
	        return "redirect:/quang-cao/LichSu/QuangCao";
	    }
	    
	    String tenTruyen = banner.getTruyen().getTenTruyen();

	    bannerRepository.deleteById(id);

	    redirectAttributes.addFlashAttribute("successMessage","Đã xóa quảng cáo truyện: " + tenTruyen);

	    return "redirect:/quang-cao/LichSu/QuangCao";
	}
}
