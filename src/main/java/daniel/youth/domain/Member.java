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

    // 목장 편성 열외 여부 (true면 제외 하는걸로)
    @Builder.Default // 이 어노테이션을 붙여야 빌더가 기본값(false)을 인식합니다.
    @Column(columnDefinition = "boolean default false") // DDL 생성 시 기본값 힌트
    private boolean isExcluded = false;

    public void assignTeam(Team team) {
        this.team = team;
    }

    public void setMemberRole(MemberRole role) {
        this.role = role;
    }

    public void setExcluded(boolean excluded) {
        this.isExcluded = excluded;
    }

}
