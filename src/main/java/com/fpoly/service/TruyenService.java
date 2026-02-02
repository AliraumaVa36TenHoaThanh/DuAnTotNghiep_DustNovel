package com.fpoly.service;

import com.fpoly.repository.TheLoaiRepository;
import com.fpoly.repository.TruyenRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fpoly.model.Truyen;
import com.fpoly.model.enums.LoaiTruyen;
import com.fpoly.model.TheLoai;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

	public Truyen suaTruyen(Long id, Truyen truyen) {
		return truyenRepo.save(truyen);
	}

	public List<Truyen> timKiemNangCao(
	        String tenTruyen,
	        String tenTacGia,
	        LoaiTruyen loaiTruyen,   // 👈 THÊM
	        Map<String, String> params,
	        Boolean showTag18        // null = tất cả
	) {

	    List<Long> includeIds = new ArrayList<>();
	    List<Long> excludeIds = new ArrayList<>();

	    if (params != null) {
	        params.forEach((key, value) -> {
	            if (!key.startsWith("theLoai[")) return;

	            int state = Integer.parseInt(value);
	            if (state == 0) return;

	            Long id = Long.valueOf(
	                key.substring(key.indexOf('[') + 1, key.indexOf(']'))
	            );

	            if (state == 1) includeIds.add(id);
	            else if (state == -1) excludeIds.add(id);
	        });
	    }

	    boolean hasTheLoaiFilter = !includeIds.isEmpty() || !excludeIds.isEmpty();

	    // ✅ KHÔNG CHỌN THỂ LOẠI
	    if (!hasTheLoaiFilter) {
	        return truyenRepo.timKiemKhongTheLoai(
	            tenTruyen,
	            tenTacGia,
	            loaiTruyen,
	            showTag18
	        );
	    }

	    // ✅ CÓ CHỌN THỂ LOẠI
	    return truyenRepo.timKiemCoTheLoai(
	        tenTruyen,
	        tenTacGia,
	        loaiTruyen,
	        includeIds,
	        excludeIds,
	        showTag18,
	        includeIds.size(),
	        excludeIds.size()
	    );
	}

}