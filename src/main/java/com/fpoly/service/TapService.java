package com.fpoly.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.model.Tap;
import com.fpoly.model.Truyen;
import com.fpoly.repository.TapRepository;

@Service
public class TapService {

    @Autowired
    private TapRepository tapRepo;

    @Autowired
    private TruyenService truyenService;

    public Tap findById(Long id) {
        return tapRepo.findById(id).orElse(null);
    }

    public List<Tap> findByTruyen(Long truyenId) {
        return tapRepo.findByTruyenIdOrderBySoTapAsc(truyenId);
    }

    public Tap add(Long truyenId, String tenTap) {
        Truyen truyen = truyenService.findById(truyenId);
        if (truyen == null) throw new RuntimeException("Không tìm thấy truyện");

        Integer max = tapRepo.findMaxSoTapByTruyenId(truyenId);
        int nextSo = (max == null) ? 1 : max + 1;

        Tap tap = new Tap();
        tap.setTruyen(truyen);
        tap.setTenTap(tenTap);
        tap.setSoTap(nextSo);

        return tapRepo.save(tap);
    }

    public Tap update(Long tapId, String tenTap) {
        Tap tap = findById(tapId);
        if (tap == null) throw new RuntimeException("Không tìm thấy tập");

        tap.setTenTap(tenTap);
        return tapRepo.save(tap);
    }

    public void delete(Long tapId) {
        tapRepo.deleteById(tapId);
    }
}

