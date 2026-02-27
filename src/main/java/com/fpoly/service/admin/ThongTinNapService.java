package com.fpoly.service.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.model.NguoiDung;
import com.fpoly.repository.NapTienRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThongTinNapService {

    private final NapTienRepository napTienRepository;
    private final NguoiDungRepository nguoiDungRepository;

    public List<Object[]> getDoanhThuTheoNgay() {
        return napTienRepository.doanhThuTheoNgay();
    }

    public List<Object[]> getTopNapNhieu() {
        return napTienRepository.topNapNhieu();
    }

    public List<NguoiDung> getTopToken() {
        return nguoiDungRepository.findTop5ByOrderByTokenDesc();
    }
}