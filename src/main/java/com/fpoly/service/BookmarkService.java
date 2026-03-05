package com.fpoly.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.model.Bookmark;
import com.fpoly.model.Chuong;
import com.fpoly.model.NguoiDung;
import com.fpoly.repository.BookmarkRepository;
import com.fpoly.repository.ChuongRepository;
import com.fpoly.repository.NguoiDungRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookmarkService {
	private final NguoiDungRepository nguoiDungRepo; 
    private final ChuongRepository chuongRepo;
    private final BookmarkRepository bookmarkRepo;

    public void saveBookmark(Long userId,
                             Long chuongId,
                             Integer viTri,
                             Float phanTram,
                             String text) {

        Bookmark bm = new Bookmark();
        NguoiDung user = nguoiDungRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));
                
        Chuong chuong = chuongRepo.findById(chuongId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Chương"));
        bm.setNguoiDung(user);
        bm.setChuong(chuong);
        bm.setViTriKyTu(viTri);
        bm.setPhanTramDoc(phanTram);
        bm.setDoanText(text);
        bm.setNgayCapNhat(LocalDateTime.now());

        bookmarkRepo.save(bm);
    }

    public List<Bookmark> getUserBookmarks(Long userId) {
        return bookmarkRepo.findByNguoiDungIdOrderByNgayCapNhatDesc(userId);
    }

    public void deleteBookmark(Long id) {
        bookmarkRepo.deleteById(id);
    }
}