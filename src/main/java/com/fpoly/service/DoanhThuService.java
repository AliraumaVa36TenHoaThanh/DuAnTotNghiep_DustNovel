package com.fpoly.service;

import com.fpoly.dto.ChiTietMuaChuongDTO;
import com.fpoly.dto.DoanhThuTruyenDTO;
import com.fpoly.repository.MoKhoaChuongRepository;
import com.fpoly.repository.TruyenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoanhThuService {

    @Autowired
    private TruyenRepository truyenRepo;

    @Autowired
    private MoKhoaChuongRepository moKhoaChuongRepo;

    public List<DoanhThuTruyenDTO> layThongKeDoanhThu(Long userId) {
        return truyenRepo.layDoanhThuCacTruyenCuaUser(userId);
    }

    public List<ChiTietMuaChuongDTO> layChiTietTruyen(Long truyenId) {
        return moKhoaChuongRepo.layChiTietMuaChuongCuaTruyen(truyenId);
    }
}