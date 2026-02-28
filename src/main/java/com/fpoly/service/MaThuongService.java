package com.fpoly.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fpoly.model.LichSuNhapMa;
import com.fpoly.model.MaThuong;
import com.fpoly.model.NguoiDung;
import com.fpoly.model.PhieuThuong;
import com.fpoly.model.enums.StatusMaThuong;
import com.fpoly.repository.LichSuNhapMaRepository;
import com.fpoly.repository.MaThuongRepository;
import com.fpoly.repository.NguoiDungRepository;

import jakarta.transaction.Transactional;

@Service
public class MaThuongService {
	    
	    @Autowired
	    private MaThuongRepository maThuongRepo;

	    @Autowired
	    private LichSuNhapMaRepository lichSuRepo;

	    @Autowired
	    private NguoiDungRepository nguoiDungRepo;

	    @Transactional
	    public String xuLyNhapCode(String code, NguoiDung user) {
	        
	        Optional<MaThuong> maOpt = maThuongRepo.findByCode(code);
	        
	        if (maOpt.isEmpty()) {
	            return "Mã thưởng không tồn tại!";
	        }
	        
	        MaThuong ma = maOpt.get();
	        if (ma.getStatusMaThuong() == StatusMaThuong.OFF) {
	            return "Mã thưởng này đã bị khóa hoặc không còn hiệu lực!";
	        }
	        if (ma.getNgayHetHan() != null && ma.getNgayHetHan().isBefore(LocalDateTime.now())) {
	            return "Mã thưởng này đã hết hạn!";
	        }
	        if (ma.getDaNhap() >= ma.getSoLuongNhap()) {
	            return "Mã thưởng này đã đạt giới hạn sử dụng!";
	        }
	        if (lichSuRepo.existsByNguoiDungAndMaThuong(user, ma)) {
	            return "Bạn đã sử dụng mã thưởng này rồi!";
	        }

	        	PhieuThuong phieuCuaUser = user.getPhieuThuong();
	        
	        if (phieuCuaUser == null) {
	            phieuCuaUser = new PhieuThuong();
	            phieuCuaUser.setNguoiDung(user); 
	            phieuCuaUser.setSoLuong(0L); 
	            user.setPhieuThuong(phieuCuaUser);
	        }

	        Long phieuHienTai = phieuCuaUser.getSoLuong() != null ? phieuCuaUser.getSoLuong() : 0L;
	        phieuCuaUser.setSoLuong(phieuHienTai + ma.getSoPhieuThuong());
	        
	        nguoiDungRepo.save(user);

	        ma.setDaNhap(ma.getDaNhap() + 1);
	        maThuongRepo.save(ma);

	        LichSuNhapMa lichSu = new LichSuNhapMa();
	        lichSu.setNguoiDung(user);
	        lichSu.setMaThuong(ma);
	        lichSuRepo.save(lichSu);

	        return "SUCCESS:" + ma.getSoPhieuThuong();
	    }
	    
	    public List<MaThuong> layDanhSachMaThuong() {
	        return maThuongRepo.findAll(Sort.by(Sort.Direction.DESC, "ngayTao"));
	    }
	    public MaThuong layMaThuongTheoId(Long id) {
	        return maThuongRepo.findById(id).orElse(null);
	    }

	    public void themMaThuong(MaThuong maThuong) throws Exception {
	        if (maThuongRepo.findByCode(maThuong.getCode()).isPresent()) {
	            throw new Exception("Mã code này đã tồn tại, vui lòng nhập mã khác!");
	        }
	        maThuongRepo.save(maThuong);
	    }

	    public void suaMaThuong(Long id, MaThuong maThuongSua) throws Exception {
	        MaThuong maThuongCu = maThuongRepo.findById(id)
	                .orElseThrow(() -> new Exception("Không tìm thấy mã thưởng!"));

	        if (!maThuongCu.getCode().equals(maThuongSua.getCode()) &&
	                maThuongRepo.findByCode(maThuongSua.getCode()).isPresent()) {
	            throw new Exception("Mã code mới đã tồn tại!");
	        }

	        maThuongCu.setCode(maThuongSua.getCode());
	        maThuongCu.setSoPhieuThuong(maThuongSua.getSoPhieuThuong());
	        maThuongCu.setSoLuongNhap(maThuongSua.getSoLuongNhap());
	        maThuongCu.setNgayHetHan(maThuongSua.getNgayHetHan());
	        
	        maThuongRepo.save(maThuongCu);
	    }

	    public void doiTrangThai(Long id) {
	        MaThuong ma = maThuongRepo.findById(id).orElse(null);
	        if (ma != null) {
	            if (ma.getStatusMaThuong() == StatusMaThuong.ON) {
	                ma.setStatusMaThuong(StatusMaThuong.OFF);
	            } else {
	                ma.setStatusMaThuong(StatusMaThuong.ON);
	            }
	            maThuongRepo.save(ma);
	        }
	    }
}	
