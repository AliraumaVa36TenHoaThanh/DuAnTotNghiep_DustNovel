package com.fpoly.repository;

import com.fpoly.model.MaThuong;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MaThuongRepository extends JpaRepository<MaThuong, Long> {
    Optional<MaThuong> findByCode(String code);
}