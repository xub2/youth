package daniel.youth.service;

import daniel.youth.domain.Team;
import daniel.youth.repository.MemberRepository;
import daniel.youth.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;

    public List<Team> findAll() {
        return teamRepository.findAll();
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
