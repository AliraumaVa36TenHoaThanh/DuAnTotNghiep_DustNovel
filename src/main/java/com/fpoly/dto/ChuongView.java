package com.fpoly.dto;


import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ChuongView {
	private Long id;
    private String tieuDe;
    private boolean khoa;
    private long giaToken;

    private boolean coTheDoc;
    private boolean canMua;
    private boolean canRead;
}
