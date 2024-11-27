package com.wanted.wanted2.users.service.impl;

import com.wanted.wanted2.users.model.UserDto;
import com.wanted.wanted2.users.model.UserEntity;
import com.wanted.wanted2.users.repository.UserRepository;
import com.wanted.wanted2.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<?> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> ResponseEntity.ok("Email is already in use"))
                .orElseGet(() -> ResponseEntity.ok("Email is available"));
    }

    @Override
    public ResponseEntity<?> save(UserDto user) {
        return userRepository.findByEmail(user.getEmail())
                .map(existingUser -> ResponseEntity.badRequest().body("Email is already in use"))
                .orElseGet(() -> {
                    UserEntity newUser = UserEntity.builder()
                            .email(user.getEmail())
                            .password(passwordEncoder.encode(user.getPassword()))
                            .name(user.getName())
                            .phoneNumber(user.getPhoneNumber())
                            .address(user.getAddress())
                            .postcode(user.getPostcode())
                            .build();

                    UserEntity savedUser = userRepository.save(newUser);
                    return ResponseEntity.ok("Saved user is " + savedUser);
                });
    }
}
