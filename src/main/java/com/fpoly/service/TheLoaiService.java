package com.fpoly.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.model.TheLoai;
import com.fpoly.model.Truyen;
import com.fpoly.repository.TheLoaiRepository;
import com.fpoly.repository.TruyenRepository;

@Service
public class TheLoaiService {

	@Autowired
    private TheLoaiRepository theLoaiRepo;

    @Autowired
    private TruyenRepository truyenRepo;

    // Lấy tất cả thể loại
    public List<TheLoai> getAllTheLoai() {
        return theLoaiRepo.findAll();
    }

    // Lấy danh sách truyện theo thể loại
    public List<Truyen> getTruyenByTheLoai(Long theLoaiId) {
        return truyenRepo.findByTheLoais_Id(theLoaiId); // sửa đúng method
    }

    // Tìm thể loại theo ID
    public TheLoai findById(Long id) {
        return theLoaiRepo.findById(id).orElse(null);
    }
	
}
