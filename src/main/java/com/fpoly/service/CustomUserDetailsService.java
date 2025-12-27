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
    public UserDetails loadUserByUsername(String tenDangNhap)
            throws UsernameNotFoundException {

        NguoiDung user = repo.findByTenDangNhap(tenDangNhap)
            .orElseThrow(() ->
                new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(user);
    }
}
