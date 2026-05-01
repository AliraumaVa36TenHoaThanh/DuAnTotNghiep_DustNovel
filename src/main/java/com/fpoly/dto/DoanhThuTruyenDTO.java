package com.fpoly.dto;

import java.time.LocalDateTime;

import com.fpoly.model.enums.LoaiTruyen;
import com.fpoly.model.enums.TrangThaiTruyen;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoanhThuTruyenDTO {
    private Long truyenId;
    private String tenTruyen;
    private LoaiTruyen loaiTruyen;
    private TrangThaiTruyen trangThai;
    private Long tongDoanhThu;
    private LocalDateTime ngayMo;
}