package com.fpoly.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.model.NguoiDung;
import com.fpoly.model.enums.VaiTro;
import com.fpoly.repository.NguoiDungRepository;

@Service
public class RegisterService {

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    public String register(NguoiDung nd, String reMatKhau) {

        if (nguoiDungRepository.existsByTenDangNhap(nd.getTenDangNhap())) {
            return "Tên đăng nhập đã tồn tại";
        }

        if (nguoiDungRepository.existsByEmail(nd.getEmail())) {
            return "Email đã được sử dụng";
        }

        if (!nd.getMatKhau().equals(reMatKhau)) {
            return "Mật khẩu nhập lại không khớp";
        }
        nd.setVaiTro(VaiTro.USER);  
        nguoiDungRepository.save(nd);
        
        nd.setTrangThai("HOAT_DONG");
        nd.setVaiTro(VaiTro.USER);
        nguoiDungRepository.save(nd);


        return null; // thành công
    }
}
