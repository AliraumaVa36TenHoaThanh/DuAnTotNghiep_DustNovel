package com.fpoly.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fpoly.model.AiDich;
import com.fpoly.model.Chuong;

public interface AiDichRepository extends JpaRepository<AiDich, Long> {

    Optional<AiDich> findByChuongAndNgonNgu(Chuong chuong, String ngonNgu);

}