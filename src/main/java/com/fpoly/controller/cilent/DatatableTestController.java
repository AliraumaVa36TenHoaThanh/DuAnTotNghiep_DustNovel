package com.fpoly.controller.cilent;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import java.util.List;
import java.io.IOException;
import com.fpoly.model.TheLoai;
import com.fpoly.model.Truyen;
import com.fpoly.model.enums.*;
import com.fpoly.repository.TheLoaiRepository;
import com.fpoly.repository.TruyenRepository;
import com.fpoly.security.CustomUserDetails;
import com.fpoly.service.TruyenService;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import com.fpoly.model.NguoiDung;
import java.io.File;
@Controller
@RequestMapping("/dbu")
public class DatatableTestController {
	@Autowired
	private TruyenRepository truyenRepository;
	
	@Autowired
	private TheLoaiRepository theLoaiRepository;
	
	@Autowired
	private TruyenService truyenService;

	@GetMapping("/")
	public String list(Model model) {
		model.addAttribute("content", "/view/client/truyen/index.html");
		return "/layout/cilent_base";
	}

	@GetMapping("/truyen-sang-tac")
	public String truyenSangTac(Model model, Authentication authentication) {

	    CustomUserDetails userDetails =
	            (CustomUserDetails) authentication.getPrincipal();

	    Long userId = userDetails.getNguoiDung().getId();

	    List<Truyen> listTruyen =
	            truyenRepository.findByUserAndLoai(
	                    userId,
	                    LoaiTruyen.SÁNG_TÁC
	            );

	    model.addAttribute("listTruyen", listTruyen);
	    model.addAttribute("content", "view/client/truyen/sangTac");
	    model.addAttribute("title", "Truyện sáng tác");

	    return "layout/cilent_base";
	}


	@GetMapping("/truyen-dich")
	public String truyenDich(Model model, Authentication authentication) {

	    CustomUserDetails userDetails =
	            (CustomUserDetails) authentication.getPrincipal();

	    Long userId = userDetails.getNguoiDung().getId();

	    List<Truyen> listTruyen =
	            truyenRepository.findByUserAndLoai(
	                    userId,
	                    LoaiTruyen.DỊCH   
	            );

	    model.addAttribute("listTruyenDich", listTruyen);
	    model.addAttribute("content", "view/client/truyen/dich");
	    model.addAttribute("title", "Truyện dịch");    
	    return "layout/cilent_base";
	}
	
	@GetMapping("/truyen-the-loai")
	public String danhSachTheLoai(Model model) {

	    List<TheLoai> listTheLoai = theLoaiRepository.findByStatusTheLoai(StatusTheLoai.ON);

	    model.addAttribute("listTheLoai", listTheLoai);
	    model.addAttribute("content", "view/client/truyen/ListTheLoaiUser");

	    return "layout/cilent_base";
	}
	
	@GetMapping("/truyen-the-loai/chi-tiet-user/{id}")
	public String xemTruyenTheoTheLoai(
	        @PathVariable("id") Long idTheLoai,Model model) {
	    List<Truyen> listTruyen = truyenRepository.findTruyenByTheLoaiId(idTheLoai);
	    TheLoai theLoai =  theLoaiRepository.findById(idTheLoai).orElse(null);
	    model.addAttribute("listTruyenXemUser", listTruyen);
	    model.addAttribute("theLoai", theLoai);
	    model.addAttribute("content", "view/client/truyen/ListXemTheLoaiUser");
	    return "layout/cilent_base";
	}
	
	@GetMapping("/truyen/them")
	public String formThemTruyenUser(Model model) {

	    model.addAttribute("truyenUser", new Truyen());
	    model.addAttribute("dsTheLoai",
	            theLoaiRepository.findByStatusTheLoai(StatusTheLoai.ON));
	    model.addAttribute("content",
	            "view/client/truyen/ThemTruyenUser");

	    return "layout/cilent_base";
	}


	@PostMapping("/truyen/them")
	public String themTruyenUser(
	        @ModelAttribute("truyenUser") Truyen truyen,
	        @RequestParam(value = "theLoaiIds", required = false) List<Long> theLoaiIds,
	        @RequestParam(value = "file", required = false) MultipartFile file,
	        Model model
	) throws IOException {

	    CustomUserDetails cud =
	            (CustomUserDetails) SecurityContextHolder
	                    .getContext()
	                    .getAuthentication()
	                    .getPrincipal();

	    NguoiDung user = cud.getUser();
	    truyen.setNguoiDang(user);

	    if (file != null && !file.isEmpty()) {

	        String fileName =
	                System.currentTimeMillis()
	                        + "_" + file.getOriginalFilename();

	        String uploadDir =
	                System.getProperty("user.dir")
	                        + "/src/main/resources/static/uploads/truyen/";

	        File dir = new File(uploadDir);
	        if (!dir.exists()) dir.mkdirs();

	        file.transferTo(new File(uploadDir + fileName));

	        truyen.setAnhBia("/uploads/truyen/" + fileName);
	    }

	    if (truyen.getAnhBia() == null
	            || truyen.getAnhBia().isBlank()) {

	        truyen.setAnhBia("/images/default.jpg");
	    }
	    if (theLoaiIds == null || theLoaiIds.isEmpty()) {

	        model.addAttribute("error",
	                "Vui lòng chọn ít nhất một thể loại!");
	        model.addAttribute("dsTheLoai",
	        		theLoaiRepository.findByStatusTheLoai(StatusTheLoai.ON));
	        model.addAttribute("truyenUser", truyen);
	        model.addAttribute("content",
	                "view/client/truyen/ThemTruyenUser");

	        return "layout/cilent_base";
	    }

	    truyenService.save(truyen, theLoaiIds);

	    return "redirect:/DustNovel/home";
	}

}
