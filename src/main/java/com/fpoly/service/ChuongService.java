package com.fpoly.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.model.Chuong;
import com.fpoly.repository.ChuongRepository;

@Service
public class ChuongService {

   @Autowired
   ChuongRepository chuongRepo;

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
}
