package com.fpoly.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.fpoly.model.BinhLuan;
import com.fpoly.model.Chuong;
import com.fpoly.model.MoKhoaChuong;
import com.fpoly.model.NguoiDung;
import com.fpoly.model.PhieuThuong;
import com.fpoly.model.Tap;
import com.fpoly.repository.ChuongRepository;
import com.fpoly.repository.LichSuDocRepository;
import com.fpoly.repository.MoKhoaChuongRepository;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.repository.TruyenRepository;
import com.fpoly.security.CustomUserDetails;
import com.fpoly.security.SecurityUtil;
import com.fpoly.service.BinhLuanService;
import com.fpoly.service.ChuongService;
import com.fpoly.service.LichSuDocService;
import com.fpoly.service.PermissionService;
import com.fpoly.service.PhieuThuongService;
import com.fpoly.service.TapService;
import com.fpoly.service.TruyenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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
	LichSuDocRepository lichSuDocRepo;
	@Autowired
	SecurityUtil securityUtil;
	@Autowired
	PermissionService permissionService;
	@Autowired
	TapService tapService;
	@Autowired
	PhieuThuongService phieuThuongService;
	@Autowired
	private BinhLuanService binhLuanService;
	@Autowired
	LichSuDocService lichSuDocService;
	
	@GetMapping("/{id}")
	public String read(@PathVariable Long id, Model model) {
		boolean canEditChuong = false;
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
	    List<BinhLuan> comments =
	            binhLuanService.getByChuong(id);

	    model.addAttribute("comments", comments);

	    
	    lichSuDocService.luuLichSuVaTangView(currentUser, chuong);
	    
	    Chuong chuongTruoc = chuongService.chuongTruoc(chuong);
	    Chuong chuongSau = chuongService.chuongSau(chuong);

	    if (chuongSau != null) {
	        boolean canReadNext = permissionService.canReadChuong(chuongSau, currentUser);
	        model.addAttribute("canReadNext", canReadNext);
	    }
	    
	    
	    List<Chuong> danhSachChuong = chuongService.findByTap(chuong.getTap().getId());
	    model.addAttribute("chuong", chuong);
	    model.addAttribute("dsChuong", danhSachChuong);
	    model.addAttribute("chuongTruoc", chuongTruoc);
	    model.addAttribute("chuongSau", chuongSau);
	    model.addAttribute("canRead", canRead);
	    model.addAttribute("currentUser", currentUser);
	    model.addAttribute("content", "truyen/chapter");
	    model.addAttribute("canEditChuong", canEditChuong);
	    return "layout/main";
	}


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
	    int nextSo = chuongService.getNextSoChuongByTap(tapId); //
	    chuong.setSoChuong(nextSo); 

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

	    Tap tap = tapService.findById(chuong.getTap().getId());
	    chuong.setTap(tap);

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
//    @PostMapping("/{id}/mua")
//    @Transactional
//    public String mua(@PathVariable Long id) {
//
//        NguoiDung user = securityUtil.getCurrentUserFromDB();
//        if (user == null) return "redirect:/DustNovel/login";
//
//        Chuong c = chuongRepo.findById(id).orElseThrow();
//
//        if (moKhoaChuongRepo.existsByNguoiDung_IdAndChuong_Id(
//                user.getId(), id)) {
//            return "redirect:/DustNovel/chuong/" + id;
//        }
//
//        long gia = com.fpoly.config.GiaChuongKhoa.gia_chuong;
//        if (user.getToken() < gia)
//            return "redirect:/DustNovel/nap-tien";
//
//        user.setToken(user.getToken() - gia);
//        nguoiDungRepo.save(user);
//        
//        NguoiDung nguoiDang = c.getNguoiDang();
//        if (nguoiDang != null) {
//            long tokenHienTai = (nguoiDang.getToken() != null) ? nguoiDang.getToken() : 0L;
//            nguoiDang.setToken(tokenHienTai + gia);
//            nguoiDungRepo.save(nguoiDang);
//        }
//        
//        MoKhoaChuong mk = new MoKhoaChuong();
//        mk.setNguoiDung(user);
//        mk.setChuong(c);
//        mk.setGiaToken(gia);
//        moKhoaChuongRepo.save(mk);
//
//        return "redirect:/DustNovel/chuong/" + id;
//    }
    
    
    @PreAuthorize("@permissionService.canEditChuong(#id)")
    @PostMapping("/xoa/{id}")
    @Transactional
    public String xoaChuong(@PathVariable Long id) {

        Chuong c = chuongService.findById(id);
        if (c == null) return "redirect:/DustNovel/home";
        
        lichSuDocRepo.deleteByChuongId(id);
        Long truyenId = c.getTruyen().getId();

        chuongRepo.delete(c);

        return "redirect:/DustNovel/truyen/" + truyenId;
    }
    
    @PreAuthorize("@permissionService.canEditChuong(#id)")
    @GetMapping("/sua/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {

        Chuong chuong = chuongService.findById(id);
        if (chuong == null) return "redirect:/DustNovel/home";

        model.addAttribute("chuong", chuong);
        model.addAttribute("tap", chuong.getTap());
        model.addAttribute("truyen", chuong.getTruyen());
        model.addAttribute("content", "chuong/edit");

        return "layout/main";
    }
//    @PreAuthorize("@permissionService.canEditChuong(#chuong.id)")
//    @PostMapping("/sua")
//    public String update(@ModelAttribute Chuong chuong) {
//
//        Chuong old = chuongService.findById(chuong.getId());
//        if (old == null) return "redirect:/DustNovel/home";
//
//        old.setTieuDe(chuong.getTieuDe());
//        old.setNoiDung(chuong.getNoiDung());
//        old.setSoChuong(chuong.getSoChuong());
//
//        chuongService.save(old);
//
//        return "redirect:/DustNovel/truyen/";
//    }
    @PreAuthorize("@permissionService.canEditChuong(#id)")
    @PostMapping("/sua/{id}") 
    public String suaChuong(
         @PathVariable Long id, 
         @ModelAttribute Chuong chuongForm
    ) {
         Chuong chuongDB = chuongService.findById(id);
         if (chuongDB == null) {
             return "redirect:/DustNovel/home";
         }
         chuongDB.setTieuDe(chuongForm.getTieuDe());
         chuongDB.setNoiDung(chuongForm.getNoiDung());
         chuongService.save(chuongDB);
         return "redirect:/DustNovel/truyen/" + chuongDB.getTruyen().getId();
    }
    @PostMapping("/{id}/mua")
    public String muaChuong(@PathVariable Long id, Model model) {

        NguoiDung user = securityUtil.getCurrentUserFromDB();
        if (user == null) return "redirect:/DustNovel/login";

        Chuong chuong = chuongRepo.findById(id).orElseThrow();

        PhieuThuong phieu = user.getPhieuThuong();
        if (phieu != null && phieu.getSoLuong() > 0) {
            model.addAttribute("chuong", chuong);
            model.addAttribute("soPhieu", phieu.getSoLuong());
            model.addAttribute("content", "chuong/hoi-dung-phieu");
            return "layout/main";
        }
        return "redirect:/DustNovel/chuong/" + id + "/mua-token";
    }
    
    @PostMapping("/{id}/mua-phieu")
    @Transactional
    public String muaBangPhieu(@PathVariable Long id) {

        NguoiDung user = securityUtil.getCurrentUserFromDB();
        Chuong chuong = chuongRepo.findById(id).orElseThrow();

        
		phieuThuongService.muaChuongBangPhieu(user, chuong);

        return "redirect:/DustNovel/chuong/" + id;
    }
    @PostMapping("/{id}/comment")
    public String commentChuong(@PathVariable("id") Long chuongId,
                                 @RequestParam String noiDung,
                                 HttpSession session) {

    	NguoiDung user = securityUtil.getCurrentUserFromDB();
    	if (user == null) {
    	    return "redirect:/DustNovel/login";
    	}

        binhLuanService.saveForChuong(chuongId, user.getId(), noiDung);

        return "redirect:/DustNovel/chuong/" + chuongId;
    }
    @PostMapping("/comment/delete")
    public String deleteComment(@RequestParam Long commentId,
                                @RequestParam Long chuongId,
                                HttpSession session) {

    	NguoiDung user = securityUtil.getCurrentUserFromDB();
        if (user == null) {
            return "redirect:/DustNovel/login";
        }

        binhLuanService.deletecmtChuong(commentId, user.getId());

        return "redirect:/DustNovel/chuong/" + chuongId;
    }
    @PostMapping("/comment/update")
    public String updateComment(@RequestParam Long commentId,
                                @RequestParam Long chuongId,
                                @RequestParam String noiDung,
                                HttpSession session) {

    	NguoiDung user = securityUtil.getCurrentUserFromDB();
        if (user == null) {
            return "redirect:/DustNovel/login";
        }

        binhLuanService.updatecmtChuong(commentId, noiDung, user.getId());

        return "redirect:/DustNovel/chuong/" + chuongId;
    }
    @PostMapping("/comment/reply")
    public String replyComment(@RequestParam Long parentId,
                               @RequestParam Long chuongId,
                               @RequestParam String noiDung) {

        NguoiDung user = securityUtil.getCurrentUserFromDB();
        if (user == null) {
            return "redirect:/DustNovel/login";
        }

        binhLuanService.replyForChuong(
                chuongId,
                user.getId(),
                parentId,
                noiDung
        );

        return "redirect:/DustNovel/chuong/" + chuongId;
    }
    // nút sửa phần reply cmt
    @PostMapping("/comment/reply/update")
    public String updateReply(@RequestParam Long replyId,
                              @RequestParam Long chuongId,
                              @RequestParam String noiDung) {

        NguoiDung user = securityUtil.getCurrentUserFromDB();
        if (user == null) {
            return "redirect:/DustNovel/login";
        }

        binhLuanService.updateReply(replyId, noiDung, user.getId());

        return "redirect:/DustNovel/chuong/" + chuongId;
    }
    // nút xóa phần reply cmt
    @PostMapping("/comment/reply/delete")
    public String deleteReply(@RequestParam Long replyId,
                              @RequestParam Long chuongId) {

        NguoiDung user = securityUtil.getCurrentUserFromDB();
        if (user == null) {
            return "redirect:/DustNovel/login";
        }

        binhLuanService.deleteReply(replyId, user.getId());

        return "redirect:/DustNovel/chuong/" + chuongId;
    }
}
