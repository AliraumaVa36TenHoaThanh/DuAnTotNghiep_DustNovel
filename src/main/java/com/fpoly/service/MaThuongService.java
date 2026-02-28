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

//	    @Autowired
//	    private MaThuongRepository repo;
//
//	    public void create(MaThuong ma) {
//	        ma.setDaNhap(0);
//	        ma.setNgayTao(LocalDateTime.now());
//	        repo.save(ma);
//	    }
//
//	    public List<MaThuong> findAll() {
//	        return repo.findAll();
//	    }
	    
	    @Autowired
	    private MaThuongRepository maThuongRepo;

	    @Autowired
	    private LichSuNhapMaRepository lichSuRepo;

	    @Autowired
	    private NguoiDungRepository nguoiDungRepo;

	    @Transactional
	    public String xuLyNhapCode(String code, NguoiDung user) {
	        
	        Optional<MaThuong> maOpt = maThuongRepo.findByCode(code);
	        
	        // 1. Kiểm tra mã có tồn tại không
	        if (maOpt.isEmpty()) {
	            return "Mã thưởng không tồn tại!";
	        }
	        
	        MaThuong ma = maOpt.get();

	        // 2. ✅ KIỂM TRA TRẠNG THÁI (Nếu bị Admin tắt thì chặn luôn)
	        if (ma.getStatusMaThuong() == StatusMaThuong.OFF) {
	            return "Mã thưởng này đã bị khóa hoặc không còn hiệu lực!";
	        }

	        // 3. Kiểm tra hạn sử dụng
	        if (ma.getNgayHetHan() != null && ma.getNgayHetHan().isBefore(LocalDateTime.now())) {
	            return "Mã thưởng này đã hết hạn!";
	        }

	        // 4. Kiểm tra giới hạn lượt nhập
	        if (ma.getDaNhap() >= ma.getSoLuongNhap()) {
	            return "Mã thưởng này đã đạt giới hạn sử dụng!";
	        }

	        // 5. Kiểm tra user đã nhập mã này chưa
	        if (lichSuRepo.existsByNguoiDungAndMaThuong(user, ma)) {
	            return "Bạn đã sử dụng mã thưởng này rồi!";
	        }

	        	PhieuThuong phieuCuaUser = user.getPhieuThuong();
	        
	        // 2. Nếu user này mới tinh, chưa từng có giỏ chứa phiếu thưởng nào trong DB
	        if (phieuCuaUser == null) {
	            phieuCuaUser = new PhieuThuong();
	            phieuCuaUser.setNguoiDung(user); // Liên kết giỏ này với user
	            // Giả sử bảng PhieuThuong của bạn có biến lưu số lượng là "soLuong", 
                // nếu bạn đặt tên khác (như soPhieu, tongPhieu...) thì đổi lại giúp tớ nhé!
	            phieuCuaUser.setSoLuong(0L); 
	            user.setPhieuThuong(phieuCuaUser);
	        }

	        // 3. Lấy số lượng hiện tại và cộng thêm phần thưởng
	        Long phieuHienTai = phieuCuaUser.getSoLuong() != null ? phieuCuaUser.getSoLuong() : 0L;
	        phieuCuaUser.setSoLuong(phieuHienTai + ma.getSoPhieuThuong());
	        
	        // 4. Lưu lại (Vì NguoiDung có cascade = CascadeType.ALL nên lưu user sẽ lưu luôn PhieuThuong)
	        nguoiDungRepo.save(user);

	        // Tăng lượt đã nhập của mã lên 1
	        ma.setDaNhap(ma.getDaNhap() + 1);
	        maThuongRepo.save(ma);

	        // Lưu vào lịch sử để chống spam
	        LichSuNhapMa lichSu = new LichSuNhapMa();
	        lichSu.setNguoiDung(user);
	        lichSu.setMaThuong(ma);
	        lichSuRepo.save(lichSu);

	        return "SUCCESS:" + ma.getSoPhieuThuong();
	    }
	    
	    public List<MaThuong> layDanhSachMaThuong() {
	        return maThuongRepo.findAll(Sort.by(Sort.Direction.DESC, "ngayTao"));
	    }

	    // Lấy 1 mã theo ID
	    public MaThuong layMaThuongTheoId(Long id) {
	        return maThuongRepo.findById(id).orElse(null);
	    }

	    // Thêm mã mới (Có kiểm tra trùng code)
	    public void themMaThuong(MaThuong maThuong) throws Exception {
	        if (maThuongRepo.findByCode(maThuong.getCode()).isPresent()) {
	            throw new Exception("Mã code này đã tồn tại, vui lòng nhập mã khác!");
	        }
	        maThuongRepo.save(maThuong);
	    }

	    // Sửa mã thưởng
	    public void suaMaThuong(Long id, MaThuong maThuongSua) throws Exception {
	        MaThuong maThuongCu = maThuongRepo.findById(id)
	                .orElseThrow(() -> new Exception("Không tìm thấy mã thưởng!"));

	        // Check xem code mới có bị trùng với code của thằng khác không
	        if (!maThuongCu.getCode().equals(maThuongSua.getCode()) &&
	                maThuongRepo.findByCode(maThuongSua.getCode()).isPresent()) {
	            throw new Exception("Mã code mới đã tồn tại!");
	        }

	        // Cập nhật các thông tin được phép sửa
	        maThuongCu.setCode(maThuongSua.getCode());
	        maThuongCu.setSoPhieuThuong(maThuongSua.getSoPhieuThuong());
	        maThuongCu.setSoLuongNhap(maThuongSua.getSoLuongNhap());
	        maThuongCu.setNgayHetHan(maThuongSua.getNgayHetHan());
	        
	        maThuongRepo.save(maThuongCu);
	    }

	    // Đổi trạng thái Bật/Tắt (ON/OFF)
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
