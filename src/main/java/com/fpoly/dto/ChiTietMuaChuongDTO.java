package com.fpoly.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietMuaChuongDTO {
    private String tenNguoiMua;
    private String tenChuong;
    private Long soToken;
    private LocalDateTime ngayMua;
}