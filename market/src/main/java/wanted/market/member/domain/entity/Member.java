package wanted.market.member.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import wanted.market.global.domain.BaseEntity;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "member")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(name = "login_id", unique = true, nullable = false)
    private String loginId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "member_name", unique = true, nullable = false)
    private String memberName;

    @Column(name = "email", nullable = false)
    private String email;

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }
}
