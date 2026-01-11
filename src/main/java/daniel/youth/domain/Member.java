package daniel.youth.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Enumerated(value = EnumType.STRING)
    private MemberRole role;

    private boolean isHard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public void assignTeam(Team team) {
        this.team = team;
    }

    public void setMemberRole(MemberRole role) {
        this.role = role;
    }

}
