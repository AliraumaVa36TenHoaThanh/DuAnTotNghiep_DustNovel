package com.fpoly.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.fpoly.config.GiaChuongKhoa;
import com.fpoly.model.Chuong;
import com.fpoly.model.LichSuDoc;
import com.fpoly.model.NguoiDung;
import com.fpoly.model.Tap;
import com.fpoly.model.enums.VaiTro;
import com.fpoly.repository.ChuongRepository;
import com.fpoly.repository.LichSuDocRepository;
import com.fpoly.repository.MoKhoaChuongRepository;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.repository.TapRepository;
import com.fpoly.repository.TruyenRepository;
import com.fpoly.security.SecurityUtil;
import com.fpoly.dto.ChuongView;
import jakarta.transaction.Transactional;

@Service
public class ChuongService {
	@Autowired
	private SecurityUtil securityUtil;
	@Autowired
	private LichSuDocRepository lichSuDocRepo;
	@Autowired
	private TruyenRepository truyenRepo;
   @Autowired
   ChuongRepository chuongRepo;
   @Autowired
   PermissionService permissionService;
   @Autowired
   NguoiDungRepository nguoiDungRepository;
   @Autowired
   MoKhoaChuongRepository moKhoaChuongRepo;
    ChuongService(NguoiDungRepository nguoiDungRepository) {
        this.nguoiDungRepository = nguoiDungRepository;
    }
   
    public Chuong findById(Long id) {
        return chuongRepo.findById(id).orElse(null);
    }
    
//   public List<Chuong> findByTruyen(Long truyenId) {
//        return chuongRepo.findByTapIdOrderBySoChuongAsc(truyenId);
//   }
   public List<Chuong> findByTap(Long tapId) {
	    return chuongRepo.findByTapIdOrderBySoChuongAsc(tapId);
	}



//    public Chuong chuongTruoc(Chuong c) {
//        return chuongRepo.findByTapIdAndSoChuong(
//                        c.getTap().getId(),
//                        c.getSoChuong() - 1
//                )
//                .orElse(null);
//    }
//
//    public Chuong chuongSau(Chuong c) {
//        return chuongRepo.findByTapIdAndSoChuong(
//                        c.getTap().getId(),
//                        c.getSoChuong() + 1
//                )
//                .orElse(null);
//    }
   
   @Autowired
   private TapRepository tapRepo;

   public Chuong chuongSau(Chuong c) {
       Optional<Chuong> nextInTap = chuongRepo.findByTapIdAndSoChuong(c.getTap().getId(), c.getSoChuong() + 1);
       if (nextInTap.isPresent()) {
           return nextInTap.get();
       }
       Tap currentTap = c.getTap();
       Tap nextTap = tapRepo.findFirstByTruyenIdAndSoTapGreaterThanOrderBySoTapAsc(
               currentTap.getTruyen().getId(), 
               currentTap.getSoTap()
       ).orElse(null);
       if (nextTap != null) {
           return chuongRepo.findFirstByTapIdOrderBySoChuongAsc(nextTap.getId()).orElse(null);
       }

       return null; 
   }

   public Chuong chuongTruoc(Chuong c) {
       Optional<Chuong> prevInTap = chuongRepo.findByTapIdAndSoChuong(c.getTap().getId(), c.getSoChuong() - 1);
       if (prevInTap.isPresent()) {
           return prevInTap.get();
       }
       Tap currentTap = c.getTap();
       Tap prevTap = tapRepo.findFirstByTruyenIdAndSoTapLessThanOrderBySoTapDesc(
               currentTap.getTruyen().getId(), 
               currentTap.getSoTap()
       ).orElse(null);
       if (prevTap != null) {
           return chuongRepo.findFirstByTapIdOrderBySoChuongDesc(prevTap.getId()).orElse(null);
       }

       return null;
   }

    public int getNextSoChuong(Long truyenId) {
        Integer max = chuongRepo.findMaxSoChuongByTapId(truyenId);
        return (max == null) ? 1 : max + 1;
    }
    
    public int getNextSoChuongByTap(Long tapId) {
        Integer max = chuongRepo.findMaxSoChuongByTapId(tapId); //
        return (max == null) ? 1 : max + 1;
    }
    

    public Chuong save(Chuong chuong) {
        return chuongRepo.save(chuong);
    }
    
    public void khoaChuong(Long chuongId) {
        if (!permissionService.canEditChuong(chuongId)) {
            throw new AccessDeniedException("Không có quyền");
        }

        Chuong c = chuongRepo.findById(chuongId)
                .orElseThrow();

        c.setKhoa(true);
        c.setGiaToken(GiaChuongKhoa.gia_chuong);

        chuongRepo.save(c);
    } 	
    
    public void moKhoaChuong(Long chuongId) {
        if (!permissionService.canEditChuong(chuongId)) {
            throw new AccessDeniedException("Không có quyền");
        }

        Chuong c = chuongRepo.findById(chuongId)
                .orElseThrow();

        c.setKhoa(false);
        c.setGiaToken(0L);

        chuongRepo.save(c);
    }
    
    @Transactional
    public void muaChuong(Long chuongId) {

        NguoiDung user = securityUtil.getCurrentUserFromDB();
        if (user == null) throw new RuntimeException("Chưa đăng nhập");

        Chuong chuong = chuongRepo.findById(chuongId).orElseThrow();

        if (user.getToken() < GiaChuongKhoa.gia_chuong) {
            throw new RuntimeException("Không đủ token");
        }

        user.setToken(user.getToken() - GiaChuongKhoa.gia_chuong);

        nguoiDungRepository.save(user);
    }

    public List<ChuongView> getDanhSachChuongView(
            Long tapId, NguoiDung user) {

        List<Chuong> ds = chuongRepo.findByTapIdOrderBySoChuongAsc(tapId);


        return ds.stream().map(chap -> {

            ChuongView v = new ChuongView();
            v.setId(chap.getId());
            v.setTieuDe(chap.getTieuDe());
            v.setKhoa(chap.isKhoa());
            v.setGiaToken(chap.getGiaToken());

            boolean daMua = false;
            if (user != null) {
                daMua = moKhoaChuongRepo.existsByNguoiDung_IdAndChuong_Id(user.getId(), chap.getId());
            }

            boolean free = !chap.isKhoa() || chap.getGiaToken() == 0;

            v.setCoTheDoc(free || daMua);
            v.setCanMua(!v.isCoTheDoc() && chap.getGiaToken() > 0);

            return v;
        }).toList();
    }
    
    @Transactional
    public Chuong docChuong(Long chuongId) {

        Chuong chuong = chuongRepo.findById(chuongId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương"));

        // 1️⃣ tăng view truyện (atomic)
        truyenRepo.tangLuotXem(chuong.getTruyen().getId());

        // 2️⃣ lấy user hiện tại (có thể null nếu guest)
        NguoiDung user = securityUtil.getCurrentUserFromDB();

        if (user != null) {
            LichSuDoc lsd = lichSuDocRepo
                    .findByNguoiDungAndChuong(user, chuong)
                    .orElseGet(() -> {
                        LichSuDoc x = new LichSuDoc();
                        x.setNguoiDung(user);
                        x.setTruyen(chuong.getTruyen());
                        x.setChuong(chuong);
                        return x;
                    });

            lsd.setLanDocCuoi(java.time.LocalDateTime.now());
            lichSuDocRepo.save(lsd);
        }

        return chuong;
    }
    public Long laySoTuTruyen(Long truyenId) {
        Long kyTu = chuongRepo.tinhTongSoTu(truyenId);
        return kyTu == null ? 0 : kyTu / 5;
    }
    public LocalDateTime layNgayCapNhatTruyen(Long truyenId) {
        return chuongRepo.layNgayCapNhatTruyen(truyenId);
    }
    
    public Chuong layChuongDau(Long truyenId) {
        return chuongRepo.findFirstByTapTruyenIdOrderByTapSoTapAscSoChuongAsc(truyenId).orElse(null);
    }

    public Chuong layChuongMoiNhat(Long truyenId) {
        return chuongRepo.findFirstByTapTruyenIdOrderByTapSoTapDescSoChuongDesc(truyenId).orElse(null);
    }
    
}
