package com.fpoly.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.fpoly.model.NguoiDung;
import com.fpoly.security.SecurityUtil;

@ControllerAdvice
public class GlobalModelAttribute {
	private final SecurityUtil securityUtil;

    public GlobalModelAttribute(SecurityUtil securityUtil) {
        this.securityUtil = securityUtil;
    }

    @ModelAttribute("currentUser")
    public NguoiDung addCurrentUser() {
        return securityUtil.getCurrentUser();
    }
}
