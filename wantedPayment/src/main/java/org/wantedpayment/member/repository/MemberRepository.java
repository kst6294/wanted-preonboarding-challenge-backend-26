package org.wantedpayment.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wantedpayment.member.domain.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginId(String id);
}
