package com.fpoly.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fpoly.model.Bookmark;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    List<Bookmark> findByNguoiDungIdOrderByNgayCapNhatDesc(Long userId);

    List<Bookmark> findByChuongIdAndNguoiDungId(Long chuongId, Long userId);
}