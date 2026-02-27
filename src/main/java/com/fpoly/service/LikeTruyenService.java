package com.fpoly.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.model.LikeTruyen;
import com.fpoly.model.NguoiDung;
import com.fpoly.model.Truyen;
import com.fpoly.repository.LikeTruyenRepository;
import com.fpoly.repository.TruyenRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class LikeTruyenService {

    @Autowired
    LikeTruyenRepository likeRepo;
    public List<Truyen> getTruyenDaLike(NguoiDung user) {
        List<LikeTruyen> likes = likeRepo.findByNguoiDung(user);

        return likes.stream()
                .map(LikeTruyen::getTruyen)
                .toList();
    }
    
    
    
    @Autowired
    TruyenRepository truyenRepo;

    public long toggleLike(Long userId, Long truyenId) {

        Truyen truyen = truyenRepo.findById(truyenId)
                .orElseThrow(() -> new RuntimeException("Truyện không tồn tại"));

        var likeOpt = likeRepo.findByNguoiDung_IdAndTruyen_Id(userId, truyenId);

        if (likeOpt.isPresent()) {
            likeRepo.delete(likeOpt.get());
            truyen.setTongLike(Math.max(0, truyen.getTongLike() - 1));
        } else {
            LikeTruyen like = new LikeTruyen();
            NguoiDung nd = new NguoiDung();
            nd.setId(userId);

            like.setNguoiDung(nd);
            like.setTruyen(truyen);

            likeRepo.save(like);
            truyen.setTongLike(truyen.getTongLike() + 1);
        }

        truyenRepo.save(truyen); // ⭐ BẮT BUỘC
        return truyen.getTongLike();
    }
    
}
