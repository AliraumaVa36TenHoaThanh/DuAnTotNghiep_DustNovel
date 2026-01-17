package com.fpoly.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.repository.NguoiDungRepository;

@Service
public class NguoiDungService {

    @Autowired
    private NguoiDungRepository repo;

    public void xoaTaiKhoan(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Không tìm thấy tài khoản");
        }
        repo.deleteById(id);
    }
}

