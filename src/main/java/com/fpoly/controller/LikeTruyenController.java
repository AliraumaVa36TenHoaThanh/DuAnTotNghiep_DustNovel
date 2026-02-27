package com.fpoly.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.fpoly.model.NguoiDung;
import com.fpoly.security.SecurityUtil;
import com.fpoly.service.LikeTruyenService;

@Controller
@RequestMapping("/DustNovel/like")
public class LikeTruyenController {

    @Autowired
    LikeTruyenService service;

    @Autowired
    SecurityUtil securityUtil;

    @PostMapping("/{id}")
    @ResponseBody
    public Map<String, Object> like(@PathVariable Long id) {

        NguoiDung user = securityUtil.getCurrentUserFromDB();
        Map<String, Object> res = new HashMap<>();

        if (user == null) {
            res.put("error", "NOT_LOGIN");
            return res;
        }

        long tongLike = service.toggleLike(user.getId(), id);
        res.put("tongLike", tongLike);

        return res;
    }
}
