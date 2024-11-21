package wanted.marketapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wanted.marketapi.domain.member.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByLoginIdAndPassword(String loginId, String password);
}
