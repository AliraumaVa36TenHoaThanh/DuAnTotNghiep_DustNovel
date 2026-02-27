package com.fpoly.service;

import com.fpoly.repository.ChuongRepository;
import com.fpoly.repository.LichSuDocRepository;
import com.fpoly.repository.TheLoaiRepository;
import com.fpoly.repository.TruyenRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import com.fpoly.model.Truyen;
import com.fpoly.model.enums.LoaiTruyen;
import com.fpoly.model.enums.TrangThaiTruyen;
import com.fpoly.model.TheLoai;
import com.fpoly.model.NguoiDung;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.ui.Model;


@Service
public class TruyenService {
	@Autowired
	TheLoaiRepository theLoaiRepo;
	@Autowired
	TruyenRepository truyenRepo;
	@Autowired
    private LichSuDocRepository lichSuDocRepo;
	@Autowired
    private ChuongRepository chuongRepo;
	public List<Truyen> findAll() {
		return truyenRepo.findAll();
	}

	public Truyen save(Truyen truyen, List<Long> theLoaiIds) {
		List<TheLoai> dsTheLoai = theLoaiRepo.findAllById(theLoaiIds);
		truyen.setTheLoais(dsTheLoai);
		return truyenRepo.save(truyen);
	}
	public Truyen save2(Truyen truyen) {
	    return truyenRepo.save(truyen);
	}
	public Truyen findById(Long id) {
		return truyenRepo.findById(id).orElse(null);
	}
	public void save(Truyen t) {
	    truyenRepo.save(t);
	}

	public List<Truyen> getTruyenSangTac() {
		return truyenRepo.findByLoaiTruyen(LoaiTruyen.SÁNG_TÁC);
	}

	public List<Truyen> getTruyenDich() {
		return truyenRepo.findByLoaiTruyen(LoaiTruyen.DỊCH);
	}
	
	public void xoaTruyen(Long id) {
		lichSuDocRepo.deleteByTruyenId(id);
        chuongRepo.deleteByTruyenId(id);
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
	
	public void themTruyenSangTac(Truyen truyen, NguoiDung user) {

        truyen.setLoaiTruyen(LoaiTruyen.SÁNG_TÁC);
        truyen.setNguoiDang(user);
        truyen.setTrangThai(TrangThaiTruyen.DANG_TIEN_HANH);
        truyen.setTag18(false);

        truyenRepo.save(truyen);
    }
	
	// ===== TRUYỆN SÁNG TÁC THEO USER =====
	public List<Truyen> getTruyenSangTacByUser(Long userId) {
	    return truyenRepo.findByUserAndLoai(
	            userId,
	            LoaiTruyen.SÁNG_TÁC
	    );
	}

	// ===== TRUYỆN DỊCH THEO USER =====
	public List<Truyen> getTruyenDichByUser(Long userId) {
	    return truyenRepo.findByUserAndLoai(
	            userId,
	            LoaiTruyen.DỊCH
	    );
	}
	
}