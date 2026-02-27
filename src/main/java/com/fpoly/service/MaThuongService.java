package com.fpoly.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.model.MaThuong;
import com.fpoly.repository.MaThuongRepository;

@Service
public class MaThuongService {

	    @Autowired
	    private MaThuongRepository repo;

	    public void create(MaThuong ma) {
	        ma.setDaNhap(0);
	        ma.setNgayTao(LocalDateTime.now());
	        repo.save(ma);
	    }

	    public List<MaThuong> findAll() {
	        return repo.findAll();
	    }
	
}
