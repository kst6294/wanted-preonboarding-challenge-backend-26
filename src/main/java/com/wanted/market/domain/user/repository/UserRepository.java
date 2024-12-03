package com.wanted.market.domain.user.repository;

import com.wanted.market.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일로 사용자를 조회합니다.
     * 로그인 및 회원가입 시 이메일 중복 검사에 사용됩니다.
     */
    Optional<User> findByEmail(String email);

    /**
     * 이메일로 사용자가 존재하는지 확인합니다.
     * 회원가입 시 이메일 중복 검사에 사용됩니다.
     */
    boolean existsByEmail(String email);

    /**
     * 이메일과 이름으로 사용자를 조회합니다.
     * 비밀번호 찾기 등에 사용됩니다.
     */
    Optional<User> findByEmailAndName(String email, String name);

    /**
     * 특정 기간 이후에 가입한 사용자 수를 조회합니다.
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :afterTime")
    long countUsersJoinedAfter(@Param("afterTime") java.time.LocalDateTime afterTime);
}