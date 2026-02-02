package com.fpoly.controller;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fpoly.model.NguoiDung;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.security.CustomUserDetails;
import com.fpoly.service.NguoiDungService;

@Controller
@RequestMapping("/DustNovel/user")
public class UserController {

	@Autowired
	private NguoiDungService nguoiDungService;
	
	@Autowired
	private NguoiDungRepository nguoiDungRepository;

	private final NguoiDungRepository nguoiDungRepo;
	private final PasswordEncoder passwordEncoder;

	public UserController(NguoiDungRepository nguoiDungRepo, PasswordEncoder passwordEncoder) {
		this.nguoiDungRepo = nguoiDungRepo;
		this.passwordEncoder = passwordEncoder;
	}

	// ================= GLOBAL USER =================
	@ModelAttribute("user")
	public NguoiDung getUser(Authentication auth) {
		if (auth == null)
			return null;
		return nguoiDungRepo.findByTenDangNhap(auth.getName()).orElse(null);
	}

	// ================= PROFILE PAGE =================
	@GetMapping("/profile")
	public String profile() {
		return "user/profile";
	}

	// ================= Cập nhật PROFILE =================
	@PostMapping("/profile/update")
	public String updateProfile(Authentication authentication, @RequestParam String tenDangNhap,
			@RequestParam String email, @RequestParam(required = false) String matKhauMoi,
			@RequestParam(required = false) String xacNhanMatKhau, Model model, RedirectAttributes redirectAttributes) {

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		NguoiDung user = userDetails.getUser();

		boolean hasError = false;
		boolean updated = false;
		
		if (email == null || email.isBlank()) {
            model.addAttribute("emailError", "Email không được để trống");
            hasError = true;
        } else if (!email.matches("^[A-Za-z0-9._%+-]+@gmail\\.com$")) {
            model.addAttribute("emailError", "Email phải đúng định dạng @gmail.com");
            hasError = true;
        } else if (!email.equals(user.getEmail())) {
            if (nguoiDungRepo.existsByEmail(email)) {
                model.addAttribute("emailError", "Email này đã có người sử dụng");
                hasError = true;
            }
        }
		

		
		if (matKhauMoi != null && !matKhauMoi.isBlank()) {

			if (!matKhauMoi.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
				model.addAttribute("matKhauMoiError", "Mật khẩu phải ≥ 8 ký tự, gồm chữ hoa, chữ thường và số");
				hasError = true;

			} else if (passwordEncoder.matches(matKhauMoi, user.getMatKhau())) {
				model.addAttribute("matKhauMoiError", "Mật khẩu mới không được trùng mật khẩu cũ");
				hasError = true;
			}

			if (xacNhanMatKhau == null || xacNhanMatKhau.isBlank()) {
				model.addAttribute("xacNhanMatKhauError", "Vui lòng xác nhận mật khẩu");
				hasError = true;

			} else if (!matKhauMoi.equals(xacNhanMatKhau)) {
				model.addAttribute("xacNhanMatKhauError", "Xác nhận mật khẩu không khớp");
				hasError = true;
			}
		}

		if (hasError) {
			model.addAttribute("emailValue", email);
			model.addAttribute("matKhauMoi", matKhauMoi);
			model.addAttribute("xacNhanMatKhau", xacNhanMatKhau);
			return "user/profile";
		}

		if (!user.getEmail().equals(email)) {
			user.setEmail(email);
			updated = true;
		}

		if (matKhauMoi != null && !matKhauMoi.isBlank()) {
			user.setMatKhau(passwordEncoder.encode(matKhauMoi));
			updated = true;
		}

		if (updated) {
			nguoiDungRepo.save(user);
			redirectAttributes.addFlashAttribute("success", true);
		}

		return "redirect:/DustNovel/user/profile";
	}

	// ================= AVATAR =================
	@PostMapping("/avatar")
	public String uploadAvatar(Authentication authentication, @RequestParam("file") MultipartFile file)
			throws IOException {

		if (file.isEmpty())
			return "redirect:/DustNovel/user/profile";

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

		String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/uploads/avatar/";

		File dir = new File(uploadDir);
		if (!dir.exists())
			dir.mkdirs();

		file.transferTo(new File(uploadDir + fileName));

		nguoiDungService.luuAvatar(userDetails.getUser().getId(), "/uploads/avatar/" + fileName);

		return "redirect:/DustNovel/user/profile";
	}

	@PostMapping("/avatar/delete")
	public String deleteAvatar(Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		nguoiDungService.xoaAvatar(userDetails.getUser().getId());
		return "redirect:/DustNovel/user/profile";
	}

	// ================= Thêm và đổi banner =================
	@PostMapping("/banner")
	public String uploadBanner(Authentication authentication, @RequestParam("file") MultipartFile file)
			throws IOException {
		if (file.isEmpty()) {
			return "redirect:/DustNovel/user/profile";
		}
		Long userId = ((CustomUserDetails) authentication.getPrincipal()).getUser().getId();
		// ===== Làm sạch tên file
		String originalName = file.getOriginalFilename();
		
		String safeName = originalName.replaceAll("\\s+", "_") // bỏ dấu cách
				.replaceAll("[^a-zA-Z0-9._-]", ""); // bỏ ký tự lạ
		
		String fileName = System.currentTimeMillis() + "_" + safeName;
		
		// ===== Lưu File
		String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/uploads/banner/";
		File dir = new File(uploadDir);
		if (!dir.exists())
			dir.mkdirs();
		file.transferTo(new File(uploadDir + fileName));
		
		// ===== Lưu DB
		nguoiDungService.luuBanner(userId, "/uploads/banner/" + fileName);
		return "redirect:/DustNovel/user/profile";
	}

	// ================= Xóa banner =================
	@PostMapping("/banner/delete")
	public String deleteBanner(Authentication authentication) {
		Long userId = ((CustomUserDetails) authentication.getPrincipal()).getUser().getId();
		nguoiDungService.xoaBanner(userId);
		return "redirect:/DustNovel/user/profile";
	}

	// ================= Xóa tài khoản =================
	@PostMapping("/delete")
	public String deleteAccount(Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		nguoiDungRepo.deleteById(userDetails.getUser().getId());
		return "redirect:/DustNovel/logout";
	}
}
