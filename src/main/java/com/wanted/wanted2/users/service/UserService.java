package com.wanted.wanted2.users.service;

import com.wanted.wanted2.users.model.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    ResponseEntity<?> findByEmail(String email);
    ResponseEntity<?> save(UserDto user);
}
