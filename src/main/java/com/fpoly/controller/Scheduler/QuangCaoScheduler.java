package com.fpoly.controller.Scheduler;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fpoly.model.Banner;
import com.fpoly.model.NguoiDung;
import com.fpoly.repository.BannerRepository;
import com.fpoly.repository.NguoiDungRepository;

@Component
public class QuangCaoScheduler {

    @Autowired
    BannerRepository bannerRepository;

    @Autowired
    NguoiDungRepository nguoiDungRepository;
    
    /* =========================
       CHẠY MỖI 1 GIÂY
       ========================= */
    @Scheduled(fixedRate = 1000) 
    public void capNhatQuangCao(){

        List<Banner> banners = bannerRepository.findAll();

        LocalDateTime now = LocalDateTime.now();

        for(Banner b : banners){

            if(b.getTokenDaDung() == null){
                b.setTokenDaDung(0L);
            }

            NguoiDung user = b.getTruyen().getNguoiDang();

            /* =========================
               BỎ QUA NẾU TẠM DỪNG
               ========================= */
            if("TAM_DUNG".equals(b.getTrangThai())){
                continue;
            }

            /* =========================
               CHƯA ĐẾN GIỜ CHẠY
               ========================= */
            if(now.isBefore(b.getNgayBatDau())){
                b.setTrangThai("CHO_CHAY");
                bannerRepository.save(b);
                continue;
            }

            /* =========================
               HẾT HẠN
               ========================= */
            if(now.isAfter(b.getNgayKetThuc())){
                b.setTrangThai("HET_HAN");
                bannerRepository.save(b);
                continue;
            }


            /* =========================
               CHUYỂN SANG HOẠT ĐỘNG
               ========================= */
            if("CHO_CHAY".equals(b.getTrangThai()) && now.isAfter(b.getNgayBatDau())){

                // reset thời điểm bắt đầu = hiện tại
                b.setNgayBatDau(now);

                // GIỮ NGUYÊN thời lượng 
                if(b.getSoNgayChay() != null){
                    b.setNgayKetThuc(now.plusDays(b.getSoNgayChay()));
                }

                b.setTrangThai("HOAT_DONG");
                bannerRepository.save(b);
            }
        }
    }
}