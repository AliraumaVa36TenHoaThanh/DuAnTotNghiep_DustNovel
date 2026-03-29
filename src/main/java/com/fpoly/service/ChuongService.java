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
import com.fpoly.model.MoKhoaChuong;
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
   
   public List<Chuong> findByTap(Long tapId) {
	    return chuongRepo.findByTapIdOrderBySoChuongAsc(tapId);
	}
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

        truyenRepo.tangLuotXem(chuong.getTruyen().getId());

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
    
    public Chuong layChuongDauCoTheDoc(Long truyenId, NguoiDung user) {

        List<Chuong> ds = chuongRepo.findTatCaChuongTheoThuTuAsc(truyenId);

        for (Chuong c : ds) {
            if (permissionService.canReadChuong(c, user)) {
                return c;
            }
        }
        return null;
    }
    public Chuong layChuongMoiCoTheDoc(Long truyenId, NguoiDung user) {

        List<Chuong> ds = chuongRepo.findTatCaChuongTheoThuTuDesc(truyenId);

        for (Chuong c : ds) {
            if (permissionService.canReadChuong(c, user)) {
                return c;
            }
        }
        return null;
    }
    
    @Transactional(rollbackOn = Exception.class)
    public void muaHangLoat(List<Long> chuongIds, NguoiDung user) throws Exception {
        if (chuongIds == null || chuongIds.isEmpty()) return;

        long tongTienCanThanhToan = 0;
        List<Chuong> danhSachCanMua = chuongRepo.findAllById(chuongIds);
        List<Chuong> danhSachThucSuMua = new java.util.ArrayList<>();
        
        // Lấy giá mặc định của hệ thống (phòng hờ chương bị lưu giá 0 trong DB)
        long giaChung = com.fpoly.config.GiaChuongKhoa.gia_chuong;

        for (Chuong c : danhSachCanMua) {
            if (!moKhoaChuongRepo.existsByNguoiDung_IdAndChuong_Id(user.getId(), c.getId())) {
                // Ưu tiên giá riêng của chương, nếu không có thì xài giá chung
                long giaThucTe = (c.getGiaToken() != null && c.getGiaToken() > 0) ? c.getGiaToken() : giaChung;
                tongTienCanThanhToan += giaThucTe;
                danhSachThucSuMua.add(c);
            }
        }

        if (danhSachThucSuMua.isEmpty()) return;

        if (user.getToken() < tongTienCanThanhToan) {
            throw new Exception("Không đủ Token để thanh toán!");
        }

        // Trừ tiền người mua
        user.setToken(user.getToken() - tongTienCanThanhToan);
        nguoiDungRepository.save(user);

        // Trả tiền tác giả & Lưu lịch sử
        for (Chuong c : danhSachThucSuMua) {
            long giaThucTe = (c.getGiaToken() != null && c.getGiaToken() > 0) ? c.getGiaToken() : giaChung;
            
            NguoiDung nguoiDang = c.getNguoiDang();
            if (nguoiDang != null) {
                long tokenHienTai = (nguoiDang.getToken() != null) ? nguoiDang.getToken() : 0L;
                nguoiDang.setToken(tokenHienTai + giaThucTe);
                nguoiDungRepository.save(nguoiDang);
            }

            MoKhoaChuong mk = new MoKhoaChuong();
            mk.setNguoiDung(user);
            mk.setChuong(c);
            mk.setGiaToken(giaThucTe);
            moKhoaChuongRepo.save(mk);
        }
    }
    
    @Transactional(rollbackOn = Exception.class)
    public void capNhatKhoaHangLoat(Long truyenId, List<Long> chuongKhoaIds) {
        List<Chuong> tatCaChuong = chuongRepo.findByTruyenId(truyenId);
        long giaChung = com.fpoly.config.GiaChuongKhoa.gia_chuong;

        for (Chuong c : tatCaChuong) {
            
            boolean isLocked = (chuongKhoaIds != null && chuongKhoaIds.contains(c.getId()));
            
            c.setKhoa(isLocked);
            
            if (isLocked) {
               
                if (c.getGiaToken() == null || c.getGiaToken() == 0) {
                    c.setGiaToken(giaChung);
                }
            } else {
               
                c.setGiaToken(0L); 
            }
        }
        chuongRepo.saveAll(tatCaChuong);
    }
}
