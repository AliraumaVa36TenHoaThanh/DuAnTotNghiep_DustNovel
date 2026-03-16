package com.fpoly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fpoly.model.AiTomTat;
import com.fpoly.model.Chuong;
import java.util.Optional;

@Repository
public interface AiTomTatRepository extends JpaRepository<AiTomTat, Long> {
    Optional<AiTomTat> findByChuong(Chuong chuong);
}