package wanted.market.member.repository;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wanted.market.member.domain.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberByLoginIdAndPassword(@NotBlank String login_id, @NotBlank String password);

    Optional<Member> findByLoginId(String loginId);

    @Query("SELECT m FROM Member m WHERE m.loginId = :loginId AND m.email = :email")
    Optional<Member> findByLoginIdAndEmail(@Param("loginId") String loginId, @Param("email") String email);

}
