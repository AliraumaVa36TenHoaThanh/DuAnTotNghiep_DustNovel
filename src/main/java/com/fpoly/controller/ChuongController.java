package com.fpoly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.fpoly.model.Chuong;
import com.fpoly.model.MoKhoaChuong;
import com.fpoly.model.NguoiDung;
import com.fpoly.model.Tap;
import com.fpoly.repository.ChuongRepository;
import com.fpoly.repository.MoKhoaChuongRepository;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.repository.TruyenRepository;
import com.fpoly.security.CustomUserDetails;
import com.fpoly.security.SecurityUtil;
import com.fpoly.service.ChuongService;
import com.fpoly.service.PermissionService;
import com.fpoly.service.TapService;
import com.fpoly.service.TruyenService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/DustNovel/chuong")
public class ChuongController {

	@Autowired
	ChuongService chuongService;
	@Autowired
	 TruyenService truyenService;
	@Autowired
	 NguoiDungRepository nguoiDungRepo;
	@Autowired
	ChuongRepository chuongRepo;
	@Autowired
	TruyenRepository truyenRepo;
	@Autowired
	MoKhoaChuongRepository moKhoaChuongRepo;
	@Autowired
	SecurityUtil securityUtil;
	@Autowired
	PermissionService permissionService;
	@Autowired
	TapService tapService;
	
//	@GetMapping("/{id}")
//	public String read(@PathVariable Long id, Model model) {
//
//	    Chuong chuong = chuongService.findById(id);
//	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//	    NguoiDung currentUser = null;
//	    if (auth != null && auth.isAuthenticated()
//	        && !(auth instanceof AnonymousAuthenticationToken)) {
//
//	        CustomUserDetails cud = (CustomUserDetails) auth.getPrincipal();
//	        currentUser = nguoiDungRepo.findById(cud.getId()).orElse(null);
//	    }
//	    boolean canRead = permissionService.canReadChuong(chuong, currentUser);
//	    
//	    model.addAttribute("chuong", chuong);
//	    model.addAttribute("chuongTruoc", chuongService.chuongTruoc(chuong));
//	    model.addAttribute("chuongSau", chuongService.chuongSau(chuong));
//	    model.addAttribute("canRead", canRead);
//	    model.addAttribute("currentUser", currentUser);
//	    model.addAttribute("content", "truyen/chapter");
//	    model.addAttribute("title", chuong.getTieuDe());
//
//	    return "layout/main";
	// }
	
//	@GetMapping("/{id}")
//	public String read(@PathVariable Long id, Model model) {
//	    Chuong chuong = chuongService.findById(id);
//	    if (chuong == null) return "redirect:/DustNovel/home";
//	    NguoiDung currentUser = securityUtil.getCurrentUserFromDB();	  
//	    boolean canRead = permissionService.canReadChuong(chuong, currentUser);
//	    if (!canRead) {
//	        return "redirect:/DustNovel/truyen/" + chuong.getTruyen().getId() + "?error=not_purchased&chapId=" + id;
//	    }
//
//
//	    model.addAttribute("chuong", chuong);
//	    model.addAttribute("chuongTruoc", chuongService.chuongTruoc(chuong));
//	    model.addAttribute("chuongSau", chuongService.chuongSau(chuong));
//	    model.addAttribute("canRead", canRead);
//	    model.addAttribute("currentUser", currentUser);
//	    model.addAttribute("content", "truyen/chapter");
//	    model.addAttribute("title", chuong.getTieuDe());
//
//	    return "layout/main";
//	}
	
//	@GetMapping("/{id}")
//	public String read(@PathVariable Long id, Model model) {
//	    Chuong chuong = chuongService.findById(id);
//	    if (chuong == null) return "redirect:/DustNovel/home";
//
//	    NguoiDung currentUser = securityUtil.getCurrentUserFromDB();
//	    boolean canRead = permissionService.canReadChuong(chuong, currentUser);
//
//	    if (!canRead) {
//            return "redirect:/DustNovel/truyen/"
//                    + chuong.getTap().getTruyen().getId()
//                    + "?error=not_purchased&chapId=" + id;
//        }
//
//	    Chuong chuongSau = chuongService.chuongSau(chuong);
//	    if (chuongSau != null) {
//	        boolean canReadNext = permissionService.canReadChuong(chuongSau, currentUser);
//	        model.addAttribute("chuongSau", chuongSau);
//	        model.addAttribute("canReadNext", canReadNext);
//	    }
//
//	    model.addAttribute("chuong", chuong);
//	    model.addAttribute("chuongTruoc", chuongService.chuongTruoc(chuong));
//	    model.addAttribute("canRead", canRead);
//	    model.addAttribute("currentUser", currentUser);
//	    model.addAttribute("content", "truyen/chapter");
//	    return "layout/main";
//	}
	
	@GetMapping("/{id}")
	public String read(@PathVariable Long id, Model model) {

	    Chuong chuong = chuongService.findById(id);
	    if (chuong == null) {
	        return "redirect:/DustNovel/home";
	    }

	    NguoiDung currentUser = securityUtil.getCurrentUserFromDB();
	    boolean canRead = permissionService.canReadChuong(chuong, currentUser);

	    if (!canRead) {
	        return "redirect:/DustNovel/truyen/"
	                + chuong.getTap().getTruyen().getId()
	                + "/tap/" + chuong.getTap().getId()
	                + "?error=not_purchased&chapId=" + id;
	    }

	    Chuong chuongTruoc = chuongService.chuongTruoc(chuong);
	    Chuong chuongSau = chuongService.chuongSau(chuong);

	    if (chuongSau != null) {
	        boolean canReadNext = permissionService.canReadChuong(chuongSau, currentUser);
	        model.addAttribute("canReadNext", canReadNext);
	    }

	    model.addAttribute("chuong", chuong);
	    model.addAttribute("chuongTruoc", chuongTruoc);
	    model.addAttribute("chuongSau", chuongSau);
	    model.addAttribute("canRead", canRead);
	    model.addAttribute("currentUser", currentUser);
	    model.addAttribute("content", "truyen/chapter");

	    return "layout/main";
	}



//	    if (!permissionService.canReadChuong(chuong, currentUser)) {
//        model.addAttribute("chuong", chuong);
//        model.addAttribute("currentUser", currentUser);
//        model.addAttribute("content", "chuong/bi-khoa");
//        model.addAttribute("title", "Chương bị khóa");
//        return "layout/main";
//    }
//
//    
//
//    model.addAttribute("chuong", chuong);
//    model.addAttribute("chuongTruoc", chuongService.chuongTruoc(chuong));
//    model.addAttribute("chuongSau", chuongService.chuongSau(chuong));
//    model.addAttribute("content", "truyen/chapter");
//    model.addAttribute("title", chuong.getTieuDe());
//
//    return "layout/main";
	    
	

	
//	@PreAuthorize("@permissionService.canAddChuongByTap(#tapId)")
//	@GetMapping("/them/{truyenId}/{tapId}")
//	public String showAddForm(
//	        @PathVariable Long truyenId,
//	        @PathVariable Long tapId,
//	        Model model
//	) {
//	    Tap tap = tapService.findById(tapId);
//	    if (tap == null) return "redirect:/DustNovel/home";
//	    if (!tap.getTruyen().getId().equals(truyenId)) {
//            return "redirect:/DustNovel/home";
//        }
//
//	    Chuong chuong = new Chuong();
//	    chuong.setTap(tap);
//
//	    model.addAttribute("chuong", chuong);
//	    model.addAttribute("tap", tap);
//	    model.addAttribute("truyen", tap.getTruyen());
//
//	    model.addAttribute("content", "chuong/add");
//	    model.addAttribute("title", "Thêm chương");
//
//	    return "layout/main";
//	}

	@PreAuthorize("@permissionService.canAddChuongByTap(#tapId)")
	@GetMapping("/them/{truyenId}/{tapId}")
	public String showAddForm(
	        @PathVariable Long truyenId,
	        @PathVariable Long tapId,
	        Model model
	) {
	    Tap tap = tapService.findById(tapId);
	    if (tap == null) return "redirect:/DustNovel/home";
	    
	    Chuong chuong = new Chuong();
	    chuong.setTap(tap);

	    // Tự động tính số chương tiếp theo dựa trên tập ID
	    int nextSo = chuongService.getNextSoChuongByTap(tapId); //
	    chuong.setSoChuong(nextSo); // Gán vào model để Thymeleaf tự điền vào input

	    model.addAttribute("chuong", chuong);
	    model.addAttribute("tap", tap);
	    model.addAttribute("truyen", tap.getTruyen());
	    model.addAttribute("content", "chuong/add");
	    
	    return "layout/main";
	}
	
	
//	@PreAuthorize("@permissionService.canAddChuongByTap(#chuong.tap.id)")
//    @PostMapping("/them")
//    public String add(@ModelAttribute Chuong chuong) {
//
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        CustomUserDetails cud = (CustomUserDetails) auth.getPrincipal();
//        NguoiDung user = cud.getUser();
//
//        chuong.setNguoiDang(user);
//
//        int nextSo = chuongService.getNextSoChuongByTap(
//                chuong.getTap().getId()
//        );
//        chuong.setSoChuong(nextSo);
//        chuongService.save(chuong);
//
//        return "redirect:/DustNovel/truyen/"
//                + chuong.getTap().getTruyen().getId();
//    }
	@PostMapping("/them")
	@PreAuthorize("@permissionService.canAddChuongByTap(#chuong.tap.id)")
	public String add(@ModelAttribute Chuong chuong) {

	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    CustomUserDetails cud = (CustomUserDetails) auth.getPrincipal();
	    NguoiDung user = cud.getUser();

	    chuong.setNguoiDang(user);

	    // ✅ SET TAP (đã có từ form)
	    Tap tap = tapService.findById(chuong.getTap().getId());
	    chuong.setTap(tap);

	    // ✅ BẮT BUỘC: SET TRUYỆN
	    chuong.setTruyen(tap.getTruyen());

	    int nextSo = chuongService.getNextSoChuongByTap(tap.getId());
	    chuong.setSoChuong(nextSo);

	    chuongService.save(chuong);

	    return "redirect:/DustNovel/truyen/" + tap.getTruyen().getId();
	}


    @PostMapping("/{id}/toggle-khoa")
    public String toggleKhoa(@PathVariable Long id,
                             HttpServletRequest request) {

        Chuong c = chuongRepo.findById(id).orElseThrow();

        c.setKhoa(!c.isKhoa());
        chuongRepo.save(c);

        return "redirect:" + request.getHeader("Referer");
    }

    
//    @PostMapping("/{id}/mua")
//    @Transactional
//    public String muaChuong(@PathVariable Long id) {
//    	
//    	NguoiDung nguoiMua = securityUtil.getCurrentUserFromDB();
//    	if (nguoiMua == null) {
//    	    return "redirect:/DustNovel/login";
//    	}
//
//        Chuong chuong = chuongRepo.findById(id).orElseThrow();
//        NguoiDung nguoiDang = chuong.getNguoiDang();
//     
//        if (moKhoaChuongRepo.existsByNguoiDung_IdAndChuong_Id(
//                nguoiMua.getId(), chuong.getId())) {
//            return "redirect:/DustNovel/chuong/" + id;
//        }
// 
//        if (nguoiMua.getToken() < 100) {
//            return "redirect:/DustNovel/nap-tien";
//        }
//      
//        nguoiMua.setToken(nguoiMua.getToken() - 100);
//     
//        nguoiDang.setToken(nguoiDang.getToken() + 100);
//
//        MoKhoaChuong mk = new MoKhoaChuong();
//        mk.setNguoiDung(nguoiMua);
//        mk.setChuong(chuong);
//        mk.setGiaToken(100L);
//
//        moKhoaChuongRepo.save(mk);
//        nguoiDungRepo.save(nguoiMua);
//        nguoiDungRepo.save(nguoiDang);
//
//        return "redirect:/DustNovel/chuong/" + id;
//    }
    
//    @PostMapping("/{id}/mua")
//    @Transactional
//    public String muaChuong(@PathVariable Long id) {
//
//        NguoiDung nguoiMua = securityUtil.getCurrentUserFromDB();
//        if (nguoiMua == null) {
//            return "redirect:/DustNovel/login";
//        }
//
//        Chuong chuong = chuongRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy chương"));
//
//        if (moKhoaChuongRepo.existsByNguoiDung_IdAndChuong_Id(
//                nguoiMua.getId(), chuong.getId())) {
//            return "redirect:/DustNovel/chuong/" + id;
//        }
//
//
//        long gia = chuong.getGiaToken(); 
//        if (gia <= 0) {
//            return "redirect:/DustNovel/chuong/" + id;
//        }
//
//        if (nguoiMua.getToken() < gia) {
//            return "redirect:/DustNovel/nap-tien";
//        }
//        
//        nguoiMua.setToken(nguoiMua.getToken() - gia);
//        NguoiDung nguoiDang = chuong.getNguoiDang();
//        if (nguoiDang != null) {
//            nguoiDang.setToken(nguoiDang.getToken() + gia);
//            nguoiDungRepo.save(nguoiDang);
//        }
//        MoKhoaChuong mk = new MoKhoaChuong();
//        mk.setNguoiDung(nguoiMua);
//        mk.setChuong(chuong);
//        mk.setGiaToken(gia);
//        moKhoaChuongRepo.save(mk);
//
//        nguoiDungRepo.save(nguoiMua);
//
//        return "redirect:/DustNovel/truyen/"
//        + chuong.getTruyen().getId()
//        + "?result=success&chapId=" + id;
//    }
    
    // ĐỊT MẸ THẰNG NÀO MÀ XÓA CÁI NÀY CỦA T LÀ T GIẾT SẠCH !
    @PostMapping("/{id}/mua")
    @Transactional
    public String mua(@PathVariable Long id) {

        NguoiDung user = securityUtil.getCurrentUserFromDB();
        if (user == null) return "redirect:/DustNovel/login";

        Chuong c = chuongRepo.findById(id).orElseThrow();

        if (moKhoaChuongRepo.existsByNguoiDung_IdAndChuong_Id(
                user.getId(), id)) {
            return "redirect:/DustNovel/chuong/" + id;
        }

        long gia = com.fpoly.config.GiaChuongKhoa.gia_chuong;
        if (user.getToken() < gia)
            return "redirect:/DustNovel/nap-tien";

        user.setToken(user.getToken() - gia);
        nguoiDungRepo.save(user);
        
        NguoiDung nguoiDang = c.getNguoiDang();
        if (nguoiDang != null) {
            long tokenHienTai = (nguoiDang.getToken() != null) ? nguoiDang.getToken() : 0L;
            nguoiDang.setToken(tokenHienTai + gia);
            nguoiDungRepo.save(nguoiDang);
        }
        
        MoKhoaChuong mk = new MoKhoaChuong();
        mk.setNguoiDung(user);
        mk.setChuong(c);
        mk.setGiaToken(gia);
        moKhoaChuongRepo.save(mk);

        return "redirect:/DustNovel/chuong/" + id;
    }
    
    @GetMapping("/xoa/{id}")
    public String xoaChuong(@PathVariable Long id) {
        truyenRepo.deleteById(id);
        return "redirect:/dbu/truyen-sang-tac";
    }
    

    
}
