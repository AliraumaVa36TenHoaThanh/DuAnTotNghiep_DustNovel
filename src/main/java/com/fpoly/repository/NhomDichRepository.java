package com.fpoly.repository;

import com.fpoly.model.NhomDich;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NhomDichRepository extends JpaRepository<NhomDich, Long> { 
    List<NhomDich> findByTrangThai(String trangThai);
}