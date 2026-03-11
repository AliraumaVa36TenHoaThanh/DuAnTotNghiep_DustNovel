package com.fpoly.repository;

import com.fpoly.model.ThueBanner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThueBannerRepository extends JpaRepository<ThueBanner, Long> {

    List<ThueBanner> findByNguoiDungId(Long nguoiDungId);

    List<ThueBanner> findByBannerId(Long bannerId);

}