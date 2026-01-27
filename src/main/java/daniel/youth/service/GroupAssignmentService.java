package daniel.youth.service;

import daniel.youth.domain.Member;
import daniel.youth.domain.MemberRole;
import daniel.youth.domain.Team;
import daniel.youth.repository.MemberRepository;
import daniel.youth.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupAssignmentService {
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    public void assignGroups() {
        List<Member> allMembers = memberRepository.findAll();
        List<Team> teams = teamRepository.findAll(); // 여기서 팀을 만들어서 findAll
        int teamCount = teams.size();

        // 1. 초기화
        allMembers.forEach(m -> {
            m.assignTeam(null);
            m.setMemberRole(MemberRole.NORMAL);
        });

        // [긴급 추가] 목장 편성 열외 인원 필터링
        List<Member> activeMembers = allMembers.stream()
                .filter(m -> !m.isExcluded()) // 열외가 아닌 사람만 선택
                .collect(Collectors.toList());

        // 이후 로직은 activeMembers를 기준으로 수행
        List<Member> nonDisabled = activeMembers.stream()
                .filter(m -> !m.isHard())
                .collect(Collectors.toList());

        List<Member> disabled = activeMembers.stream()
                .filter(Member::isHard)
                .collect(Collectors.toList());

//        List<Member> nonDisabled = allMembers.stream().filter(m -> !m.isHard()).collect(Collectors.toList());
//        List<Member> disabled = allMembers.stream().filter(Member::isHard).collect(Collectors.toList());

        Collections.shuffle(nonDisabled);
        Collections.shuffle(disabled);

        // 2. MC 배정 (비장애인 중 앞에서부터 최대 6명)
        int mcCount = Math.min(nonDisabled.size(), teamCount);
        for (int i = 0; i < mcCount; i++) {
            Member mc = nonDisabled.get(i);
            mc.setMemberRole(MemberRole.MC);
            teams.get(i).addMember(mc);
        }

        // 3. Helper 배정 (남은 비장애인 중 그다음부터 최대 6명)
        int helperStartIndex = mcCount;
        int helperCount = Math.min(nonDisabled.size() - helperStartIndex, teamCount);
        for (int i = 0; i < helperCount; i++) {
            Member helper = nonDisabled.get(helperStartIndex + i);
            helper.setMemberRole(MemberRole.HELPER);
            teams.get(i).addMember(helper);
        }

        // 4. 나머지 모든 인원(장애우 + 남은 일반인) 골고루 분산
        List<Member> remainingMembers = new ArrayList<>(disabled);
        if (nonDisabled.size() > (mcCount + helperCount)) {
            remainingMembers.addAll(nonDisabled.subList(mcCount + helperCount, nonDisabled.size()));
        }

        // 인원이 적은 팀부터 우선적으로 배정하기 위해 정렬된 팀 리스트 사용 가능
        int teamIndex = 0;
        for (Member m : remainingMembers) {
            // 모든 팀에 돌아가며 추가 (순환 배정)
            teams.get(teamIndex % teamCount).addMember(m);
            teamIndex++;
        }

        memberRepository.saveAll(allMembers);
    }
}
