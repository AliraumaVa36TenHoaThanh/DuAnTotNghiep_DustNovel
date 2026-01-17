package com.fpoly.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fpoly.model.NguoiDung;
import com.fpoly.repository.NguoiDungRepository;
import com.fpoly.security.CustomUserDetails;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private NguoiDungRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) {

        NguoiDung user = repo.findByTenDangNhap(username).orElseThrow(() ->
                        new UsernameNotFoundException("Không tìm thấy user")
                );

        return new CustomUserDetails(user);
    }

}

