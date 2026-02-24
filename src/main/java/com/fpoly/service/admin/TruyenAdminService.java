package com.fpoly.service.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.repository.ChuongRepository;
import com.fpoly.repository.MoKhoaChuongRepository;
import com.fpoly.repository.TapRepository;
import com.fpoly.repository.TruyenRepository;
import jakarta.transaction.Transactional;

@Service
public class TruyenAdminService {
	@Autowired
    private ChuongRepository chuongRepo;
	
	@Autowired
    private MoKhoaChuongRepository MochuongRepo;

	@Autowired
    private TruyenRepository truyenRepo;

    @Autowired
    private TapRepository tapRepo;

    @Transactional
    public void xoaTruyen(Long truyenId) {
    	MochuongRepo.deleteByTruyenId(truyenId);
    	chuongRepo.deleteByTruyenId(truyenId);
        tapRepo.deleteByTruyenId(truyenId);
        truyenRepo.deleteById(truyenId);
    }
}
