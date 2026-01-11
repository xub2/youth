package daniel.youth.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
@Table(name = "team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private String teamName;

    @Builder.Default
    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Member> members = new ArrayList<>();

    public Team(String teamName) {
        this.teamName = teamName;
        this.members = new ArrayList<>(); // 이 부분이 누락되면 null 에러 발생
    }

    public void addMember(Member member) {
        this.members.add(member);
        member.assignTeam(this);
    }
}