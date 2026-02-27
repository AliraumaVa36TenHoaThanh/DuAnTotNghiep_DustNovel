package com.fpoly.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.model.Chuong;
import com.fpoly.model.MoKhoaChuong;
import com.fpoly.model.NguoiDung;
import com.fpoly.model.PhieuThuong;
import com.fpoly.repository.MoKhoaChuongRepository;
import com.fpoly.repository.PhieuThuongRepository;

import jakarta.transaction.Transactional;

@Service
public class PhieuThuongService {

    @Autowired
    PhieuThuongRepository phieuRepo;

    @Autowired
    MoKhoaChuongRepository moKhoaChuongRepo;

    @Transactional
    public boolean muaChuongBangPhieu(NguoiDung user, Chuong chuong) {

        PhieuThuong phieu = user.getPhieuThuong();
        if (phieu == null || phieu.getSoLuong() <= 0) {
            return false;
        }
        phieu.setSoLuong(phieu.getSoLuong() - 1);
        phieuRepo.save(phieu);
        MoKhoaChuong mk = new MoKhoaChuong();
        mk.setNguoiDung(user);
        mk.setChuong(chuong);
        mk.setGiaToken(0L); 
        moKhoaChuongRepo.save(mk);

        return true;
    }
    @Autowired
    private PhieuThuongRepository phieuThuongRepository;

    public Long getSoLuong(Long nguoiDungId) {
        return phieuThuongRepository.findSoLuongByNguoiDungId(nguoiDungId);
    }
}
