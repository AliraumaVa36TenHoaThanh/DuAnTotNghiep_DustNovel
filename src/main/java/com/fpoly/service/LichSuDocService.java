package com.fpoly.service;

import com.fpoly.model.Chuong;
import com.fpoly.model.LichSuDoc;
import com.fpoly.model.NguoiDung;
import com.fpoly.model.Truyen;
import com.fpoly.repository.ChuongRepository;
import com.fpoly.repository.LichSuDocRepository;
import com.fpoly.repository.TruyenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LichSuDocService {

    @Autowired
    private LichSuDocRepository lichSuDocRepository;

    @Autowired
    private TruyenRepository truyenRepository;

    @Transactional
    public void luuLichSuVaTangView(NguoiDung user, Chuong chuong) {
        Truyen truyen = chuong.getTruyen();
        boolean isNewView = false;

        if (user != null) {
 
            Optional<LichSuDoc> optLsd = lichSuDocRepository.findByNguoiDungAndChuong(user, chuong);
            
            LichSuDoc lsd;
            if (optLsd.isPresent()) {
                lsd = optLsd.get();
            } else {
                lsd = new LichSuDoc();
                isNewView = true; 
            }
            lsd.setNguoiDung(user);
            lsd.setTruyen(truyen);
            lsd.setChuong(chuong);
            lsd.setLanDocCuoi(LocalDateTime.now());

            lichSuDocRepository.save(lsd);
        } else {
            isNewView = true;
        }
        if (isNewView) {
            Long luotXemHienTai = truyen.getLuotXem() != null ? truyen.getLuotXem() : 0L;
            truyen.setLuotXem(luotXemHienTai + 1);
            truyenRepository.save(truyen);
        }
    }
    
    
    public List<LichSuDoc> layLichSu(Long userId) {
        return lichSuDocRepository.findLastReadPerTruyen(userId);
    }
       

}