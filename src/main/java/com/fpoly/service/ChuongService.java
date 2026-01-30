package com.fpoly.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.fpoly.config.GiaChuongKhoa;
import com.fpoly.model.Chuong;
import com.fpoly.model.NguoiDung;
import com.fpoly.model.enums.VaiTro;
import com.fpoly.repository.ChuongRepository;
import com.fpoly.repository.MoKhoaChuongRepository;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.security.SecurityUtil;
import com.fpoly.dto.ChuongView;
import jakarta.transaction.Transactional;

@Service
public class ChuongService {
	@Autowired
	private SecurityUtil securityUtil;

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

   public List<Chuong> findByTruyen(Long truyenId) {
        return chuongRepo.findByTruyenIdOrderBySoChuongAsc(truyenId);
   }
    


    public Chuong chuongTruoc(Chuong c) {
        return chuongRepo
                .findByTruyenIdAndSoChuong(
                        c.getTruyen().getId(),
                        c.getSoChuong() - 1
                )
                .orElse(null);
    }

    public Chuong chuongSau(Chuong c) {
        return chuongRepo
                .findByTruyenIdAndSoChuong(
                        c.getTruyen().getId(),
                        c.getSoChuong() + 1
                )
                .orElse(null);
    }

    public int getNextSoChuong(Long truyenId) {
        Integer max = chuongRepo.findMaxSoChuongByTruyenId(truyenId);
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
            Long truyenId, NguoiDung user) {

        List<Chuong> ds = chuongRepo
                .findByTruyenIdOrderBySoChuongAsc(truyenId);

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
}
