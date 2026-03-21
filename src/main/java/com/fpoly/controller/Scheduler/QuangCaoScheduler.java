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

    @Scheduled(fixedRate = 10000) // chạy mỗi 10 giây
    public void capNhatQuangCao(){

        List<Banner> banners = bannerRepository.findAll();

        LocalDateTime now = LocalDateTime.now();

        for(Banner b : banners){

            if(b.getTokenDaDung() == null){
                b.setTokenDaDung(0L);
            }

            NguoiDung user = b.getTruyen().getNguoiDang();

            /* =========================
               1. BỎ QUA NẾU TẠM DỪNG
               ========================= */
            if("TAM_DUNG".equals(b.getTrangThai())){
                continue;
            }

            /* =========================
               2. CHƯA ĐẾN GIỜ CHẠY
               ========================= */
            if(now.isBefore(b.getNgayBatDau())){
                b.setTrangThai("CHO_CHAY");
                bannerRepository.save(b);
                continue;
            }

            /* =========================
               3. HẾT HẠN
               ========================= */
            if(now.isAfter(b.getNgayKetThuc())){
                b.setTrangThai("HET_HAN");
                bannerRepository.save(b);
                continue;
            }

            /* =========================
               4. HẾT TOKEN USER → PAUSE
               ========================= */
            if(user.getToken() <= 0){

                long conLai = Duration
                        .between(now, b.getNgayKetThuc())
                        .toMinutes();

                if(conLai < 0) conLai = 0;

                b.setThoiGianConLai(conLai);
                b.setNgayKetThuc(now);
                b.setTrangThai("TAM_DUNG");

                bannerRepository.save(b);
                continue;
            }

            /* =========================
               5. CHUYỂN SANG HOẠT ĐỘNG
               ========================= */
            if(!"HOAT_DONG".equals(b.getTrangThai())){
                b.setTrangThai("HOAT_DONG");
                bannerRepository.save(b);
            }

            /* =========================
               6. TÍNH TOKEN THEO THỜI GIAN THẬT
               ========================= */

            LocalDateTime last = b.getLastUpdateTime();

            // lần đầu chạy
            if(last == null){
                last = b.getNgayBatDau();
            }

            long seconds = Duration.between(last, now).getSeconds();
            if(seconds < 0) seconds = 0;

            double tokenPerSecond = b.getTokenMoiNgay() / 86400.0;

            double tokenTru = seconds * tokenPerSecond;

            long tokenCanTru = (long) tokenTru;

            if(tokenCanTru > 0){

                if(user.getToken() >= tokenCanTru){

                    //TRỪ TOKEN USER 
                    user.setToken(user.getToken() - tokenCanTru);
                    nguoiDungRepository.save(user);

                    //  CỘNG TOKEN ĐÃ DÙNG 
                    b.setTokenDaDung(
                            b.getTokenDaDung() + tokenCanTru
                    );

                    //  cập nhật mốc thời gian
                    b.setLastUpdateTime(now);

                    bannerRepository.save(b);

                }else{

                    // hết token giữa chừng → pause
                    long conLai = Duration
                            .between(now, b.getNgayKetThuc())
                            .toMinutes();

                    if(conLai < 0) conLai = 0;

                    b.setThoiGianConLai(conLai);
                    b.setNgayKetThuc(now);
                    b.setTrangThai("TAM_DUNG");

                    bannerRepository.save(b);
                }
            }
        }
    }
}