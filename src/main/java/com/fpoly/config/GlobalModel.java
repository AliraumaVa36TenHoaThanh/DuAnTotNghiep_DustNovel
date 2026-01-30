package com.fpoly.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.fpoly.model.NguoiDung;
import com.fpoly.security.SecurityUtil;

@ControllerAdvice
public class GlobalModel {
	 	@Autowired
	    SecurityUtil securityUtil;

	    @ModelAttribute("currentUser")
	    public NguoiDung currentUser() {
	       return securityUtil.getCurrentUserFromDB();
	    }
}
