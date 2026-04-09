package com.fpoly.service;

import com.fpoly.model.BaoCaoTruyen;
import com.fpoly.model.NguoiDung;
import com.fpoly.model.Truyen;
import com.fpoly.repository.BaoCaoTruyenRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class BaoCaoTruyenService {

    @Autowired
    private BaoCaoTruyenRepository baoCaoRepo;

    public void guiBaoCao(NguoiDung nguoiBaoCao, Truyen truyen, String lyDo, String moTaChiTiet) {
        BaoCaoTruyen bc = new BaoCaoTruyen();
        bc.setNguoiBaoCao(nguoiBaoCao);
        bc.setTruyenBiBaoCao(truyen);
        bc.setLyDo(lyDo);
        
        if ("Khác".equals(lyDo)) {
            bc.setMoTaChiTiet(moTaChiTiet);
        }
        
        baoCaoRepo.save(bc);
    }
    
    public List<BaoCaoTruyen> layDanhSachBaoCao(String keyword, String lyDo) {
        return baoCaoRepo.timKiemBaoCao(keyword, lyDo);
    }

    public void xuLyBaoCao(Long id) {
        BaoCaoTruyen bc = baoCaoRepo.findById(id).orElse(null);
        if (bc != null) {
            bc.setTrangThai("DA_GIAI_QUYET"); 
            baoCaoRepo.save(bc);
        }
    }

    public void xoaBaoCao(Long id) {
        baoCaoRepo.deleteById(id);
    }
    
}