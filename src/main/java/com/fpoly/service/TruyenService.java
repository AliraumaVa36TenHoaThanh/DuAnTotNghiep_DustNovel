package com.fpoly.service;

import com.fpoly.repository.TheLoaiRepository;
import com.fpoly.repository.TruyenRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fpoly.model.Truyen;
import com.fpoly.model.enums.LoaiTruyen;
import com.fpoly.model.TheLoai;
import java.util.List;
@Service
public class TruyenService {
	@Autowired
	TheLoaiRepository theLoaiRepo;
	@Autowired
	TruyenRepository truyenRepo;
	
	
    public List<Truyen> findAll() {
        return truyenRepo.findAll();
    }

    public Truyen save(Truyen truyen, List<Long> theLoaiIds) {
        List<TheLoai> dsTheLoai = theLoaiRepo.findAllById(theLoaiIds);
        truyen.setTheLoais(dsTheLoai);
        return truyenRepo.save(truyen);
    }

    public Truyen findById(Long id) {
        return truyenRepo.findById(id).orElse(null);
    }
    
    public List<Truyen> getTruyenSangTac() {
        return truyenRepo.findByLoaiTruyen(LoaiTruyen.SÁNG_TÁC);
    }

    public List<Truyen> getTruyenDich() {
        return truyenRepo.findByLoaiTruyen(LoaiTruyen.DỊCH);
    }
    public void xoaTruyen(Long id) {
    	truyenRepo.deleteById(id);
    }
    public Truyen suaTruyen(Long id, Truyen truyen){
    	return truyenRepo.save(truyen);
    }
}