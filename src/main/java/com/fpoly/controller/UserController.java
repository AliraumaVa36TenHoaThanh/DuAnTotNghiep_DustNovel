package com.fpoly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fpoly.model.NguoiDung;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.security.CustomUserDetails;
import com.fpoly.service.NguoiDungService;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/DustNovel/user")
public class UserController {

	@Autowired
	private NguoiDungService nguoiDungService;

	private final NguoiDungRepository nguoiDungRepo;
	private final PasswordEncoder passwordEncoder;

	public UserController(NguoiDungRepository nguoiDungRepo, PasswordEncoder passwordEncoder) {
		this.nguoiDungRepo = nguoiDungRepo;
		this.passwordEncoder = passwordEncoder;
	}

	// =====================================================
	// [THÊM]
	// ModelAttribute dùng chung cho toàn bộ controller
	// → Header / Main (avatar)
	// → Profile (avatar + banner)
	// FIX lỗi Optional
	// =====================================================
	@ModelAttribute("user")
	public NguoiDung getUser(Authentication auth) {
		if (auth == null)
			return null;

		return nguoiDungRepo.findByTenDangNhap(auth.getName()).orElse(null);
	}

	// =====================================================
	// HIỂN THỊ PROFILE
	// =====================================================
	@GetMapping("/profile")
	public String profile() {
		return "user/profile";
	}

        // VALIDATE TÊN ĐĂNG NHẬP
        if (tenDangNhap == null || tenDangNhap.isBlank()) {
            model.addAttribute("tenDangNhapError", "Tên đăng nhập không được để trống");
            hasError = true;
        } else if (tenDangNhap.contains(" ")) {
            model.addAttribute("tenDangNhapError", "Tên đăng nhập không được chứa khoảng trắng");
            hasError = true;
        } else if (tenDangNhap.length() < 5) {
            model.addAttribute("tenDangNhapError", "Tên đăng nhập phải từ 5 ký tự trở lên");
            hasError = true;
        } else if (!tenDangNhap.matches("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z0-9]+$")) {
            model.addAttribute(
                    "tenDangNhapError",
                    "Tên đăng nhập phải chứa cả chữ và số, không chứa ký tự đặc biệt"
            );
            hasError = true;
        }

        // VALIDATE EMAIL
        if (email == null || email.isBlank()) {
            model.addAttribute("emailError", "Email không được để trống");
            hasError = true;
        } else if (!email.matches("^[A-Za-z0-9._%+-]+@gmail\\.com$")) {
            model.addAttribute("emailError", "Email phải đúng định dạng @gmail.com");
            hasError = true;
        }

        // VALIDATE MẬT KHẨU
        if (matKhauMoi != null && !matKhauMoi.isBlank()) {
            if (!matKhauMoi.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
                model.addAttribute(
                        "matKhauMoiError",
                        "Mật khẩu phải ≥ 8 ký tự, gồm chữ hoa, chữ thường và số"
                );
                hasError = true;
            }
	// =====================================================
	// CẬP NHẬT THÔNG TIN PROFILE
	// =====================================================
	@PostMapping("/profile/update")
	public String updateProfile(Authentication authentication, @RequestParam String tenDangNhap,
			@RequestParam String email, @RequestParam(required = false) String matKhauMoi,
			@RequestParam(required = false) String xacNhanMatKhau, Model model, RedirectAttributes redirectAttributes) {

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		NguoiDung user = userDetails.getUser();

		boolean hasError = false;
		boolean updated = false;

		// ===== VALIDATE MẬT KHẨU =====
		if (matKhauMoi != null && !matKhauMoi.isBlank()) {

			boolean passwordValid = true;

			if (!matKhauMoi.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
				model.addAttribute("matKhauMoiError", "Mật khẩu phải ≥ 8 ký tự, gồm chữ hoa, chữ thường và số");
				hasError = true;
				passwordValid = false;
			} else if (passwordEncoder.matches(matKhauMoi, user.getMatKhau())) {
				model.addAttribute("matKhauMoiError", "Mật khẩu mới không được trùng mật khẩu cũ");
				hasError = true;
				passwordValid = false;
			}

			if (passwordValid) {
				if (xacNhanMatKhau == null || xacNhanMatKhau.isBlank()) {
					model.addAttribute("xacNhanMatKhauError", "Vui lòng xác nhận mật khẩu");
					hasError = true;
				} else if (!matKhauMoi.equals(xacNhanMatKhau)) {
					model.addAttribute("xacNhanMatKhauError", "Mật khẩu và xác nhận mật khẩu không khớp");
					hasError = true;
				}
			}
		}

		// ===== CÓ LỖI → GIỮ FORM =====
		if (hasError) {
			model.addAttribute("user", user);
			model.addAttribute("tenDangNhapValue", tenDangNhap);
			model.addAttribute("emailValue", email);
			model.addAttribute("matKhauMoi", matKhauMoi);
			model.addAttribute("xacNhanMatKhau", xacNhanMatKhau);
			return "user/profile";
		}

		// ===== UPDATE USERNAME =====
		if (!user.getTenDangNhap().equals(tenDangNhap)) {
			user.setTenDangNhap(tenDangNhap);
			updated = true;
		}

		// ===== UPDATE EMAIL =====
		if (!user.getEmail().equals(email)) {
			user.setEmail(email);
			updated = true;
		}

		// ===== UPDATE PASSWORD =====
		if (matKhauMoi != null && !matKhauMoi.isBlank()) {
			user.setMatKhau(passwordEncoder.encode(matKhauMoi));
			updated = true;
		}

		// ===== SAVE =====
		if (updated) {
			nguoiDungRepo.save(user);
			redirectAttributes.addFlashAttribute("success", true);
		}

		return "redirect:/DustNovel/user/profile";
	}

	// =====================================================
	// UPLOAD / ĐỔI AVATAR
	// =====================================================
	@PostMapping("/avatar")
	public String uploadAvatar(Authentication authentication, @RequestParam("file") MultipartFile file)
			throws IOException {

		if (file.isEmpty()) {
			return "redirect:/DustNovel/user/profile";
		}

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		Long userId = userDetails.getUser().getId();

		// Tạo tên file
		String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

		// Đường dẫn lưu file (STATIC – KHÔNG LỖI TOMCAT)
		String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/uploads/avatar/";

		File dir = new File(uploadDir);
		if (!dir.exists())
			dir.mkdirs();

		file.transferTo(new File(uploadDir + fileName));

		// 👉 GỌI SERVICE LƯU DB
		nguoiDungService.luuAvatar(userId, "/uploads/avatar/" + fileName);

		return "redirect:/DustNovel/user/profile";
	}

	// =====================================================
	// XÓA AVATAR
	// =====================================================
	@PostMapping("/avatar/delete")
	public String deleteAvatar(Authentication authentication) {

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		Long userId = userDetails.getUser().getId();

		// 👉 GỌI SERVICE
		nguoiDungService.xoaAvatar(userId);

		return "redirect:/DustNovel/user/profile";
	}

	// =====================================================
	// THÊM VÀ ĐỔI BANNER
	// =====================================================
	@PostMapping("/banner")
	public String uploadBanner(Authentication authentication, @RequestParam("file") MultipartFile file)
			throws IOException {

		if (file.isEmpty()) {
			return "redirect:/DustNovel/user/profile";
		}

		Long userId = ((CustomUserDetails) authentication.getPrincipal()).getUser().getId();

		// ===== 1. LÀM SẠCH TÊN FILE (CỰC KỲ QUAN TRỌNG)
		String originalName = file.getOriginalFilename();

		String safeName = originalName.replaceAll("\\s+", "_") // bỏ dấu cách
				.replaceAll("[^a-zA-Z0-9._-]", ""); // bỏ ký tự lạ

		String fileName = System.currentTimeMillis() + "_" + safeName;

		// ===== 2. LƯU FILE
		String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/uploads/banner/";

		File dir = new File(uploadDir);
		if (!dir.exists())
			dir.mkdirs();

		file.transferTo(new File(uploadDir + fileName));

		// ===== 3. LƯU DB
		nguoiDungService.luuBanner(userId, "/uploads/banner/" + fileName);

		return "redirect:/DustNovel/user/profile";
	}

	// =====================================================
	// XÓA BANNER
	// =====================================================

	@PostMapping("/banner/delete")
	public String deleteBanner(Authentication authentication) {
		Long userId = ((CustomUserDetails) authentication.getPrincipal()).getUser().getId();
		nguoiDungService.xoaBanner(userId);
		return "redirect:/DustNovel/user/profile";
	}

	// =====================================================
	// XÓA TÀI KHOẢN
	// =====================================================
	@PostMapping("/delete")
	public String deleteAccount(Authentication authentication) {

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		nguoiDungRepo.deleteById(userDetails.getUser().getId());

		return "redirect:/DustNovel/logout";
	}
}
