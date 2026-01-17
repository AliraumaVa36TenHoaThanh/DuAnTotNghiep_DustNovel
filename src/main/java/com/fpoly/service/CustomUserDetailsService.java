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
    public UserDetails loadUserByUsername(String input)
            throws UsernameNotFoundException {

        NguoiDung user = repo.findByTenDangNhap(input)
                .or(() -> repo.findByEmail(input))
                .orElseThrow(() ->
                        new UsernameNotFoundException("User không tồn tại"));

        return new CustomUserDetails(user);
    }
}

