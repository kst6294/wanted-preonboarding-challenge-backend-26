package hwijae.portonepayment.domain.repository;

import hwijae.portonepayment.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberRepository extends JpaRepository<Member, Long> {
}
