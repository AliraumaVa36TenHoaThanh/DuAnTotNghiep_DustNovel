package com.fpoly.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.fpoly.model.TheLoai;
import com.fpoly.repository.TheLoaiRepository;
import com.fpoly.service.TheLoaiService;

@ControllerAdvice
public class GlobalController {

    @Autowired
    private TheLoaiService theLoaiService;

    @ModelAttribute("theLoais")
    public List<TheLoai> loadTheLoai1() {
        return theLoaiService.getAllTheLoai();
    }
    
    @Autowired
    private TheLoaiRepository theLoaiRepository;

    @ModelAttribute("theLoais")
    public List<TheLoai> loadTheLoai() {
        return theLoaiRepository.findAll();
    }
    
    
}
