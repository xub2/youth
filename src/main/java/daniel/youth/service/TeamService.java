package daniel.youth.service;

import daniel.youth.domain.Team;
import daniel.youth.repository.MemberRepository;
import daniel.youth.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;

    // [수정된 부분] 단순 조회가 아니라, 우리가 원하는 순서대로 정렬해서 반환
    public List<Team> findAll() {
        List<Team> teams = teamRepository.findAll();

        // 정렬 기준 리스트 ("DANIEL" 순서)
        List<String> order = List.of("D", "A", "N", "I", "E", "L");

        // 위 리스트의 인덱스 순서대로 정렬 (리스트에 없는 이름은 맨 앞으로)
        teams.sort(Comparator.comparingInt(team -> {
            int index = order.indexOf(team.getTeamName());
            // 혹시 리스트에 없는 이름이 있다면 맨 뒤로 보내기 (또는 -1로 맨 앞)
            return index == -1 ? 999 : index;
        }));

        return teams;
    }

    @Transactional
    public void createTeams(int count) {
        memberRepository.clearAllMemberTeams();
        teamRepository.deleteAllTeam();

        String[] names = {"D", "A", "N", "I", "E", "L"};

        for (int i = 0; i < count; i++) {
            Team team = new Team(names[i % names.length]);
            teamRepository.save(team);
        }
    }

}
