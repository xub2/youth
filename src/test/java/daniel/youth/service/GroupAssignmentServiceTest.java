package daniel.youth.service;

import daniel.youth.domain.*;
import daniel.youth.repository.MemberRepository;
import daniel.youth.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GroupAssignmentServiceTest {

    @InjectMocks
    private GroupAssignmentService groupAssignmentService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TeamRepository teamRepository;

    private List<Team> mockTeams;
    private List<Member> mockMembers;

    @BeforeEach
    void setUp() {
        // 1. 6개의 고정 팀 생성 (D, A, N, I, E, L)
        mockTeams = List.of(TeamName.values()).stream()
                .map(name -> Team.builder().teamName(String.valueOf(name)).members(new ArrayList<>()).build())
                .collect(Collectors.toList());

        // 2. 50명의 테스트 멤버 생성 (장애우 5명 포함)
        mockMembers = new ArrayList<>();

        // 일반 인원 45명
        IntStream.range(0, 45).forEach(i ->
                mockMembers.add(Member.builder().name("일반" + i).isHard(false).role(MemberRole.NORMAL).build())
        );

        // 장애우 인원 5명
        IntStream.range(0, 5).forEach(i ->
                mockMembers.add(Member.builder().name("장애우" + i).isHard(true).role(MemberRole.NORMAL).build())
        );
    }

    @Test
    @DisplayName("랜덤 배정 시 MC와 Helper는 팀당 1명씩 배정되고 인원이 균등해야 한다")
    void assignGroupsTest() {
        // given
        given(memberRepository.findAll()).willReturn(mockMembers);
        given(teamRepository.findAll()).willReturn(mockTeams);

        // when
        groupAssignmentService.assignGroups();

        // then
        // 1. 전체 인원 배정 확인 (50명 모두 팀이 있어야 함)
        long assignedCount = mockMembers.stream()
                .filter(m -> m.getTeam() != null)
                .count();
        assertThat(assignedCount).isEqualTo(50);

        // 2. 각 팀별 검증
        for (Team team : mockTeams) {
            List<Member> members = team.getMembers();

            // 팀당 MC가 정확히 1명인가?
            long mcCount = members.stream().filter(m -> m.getRole() == MemberRole.MC).count();
            assertThat(mcCount).isEqualTo(1);

            // 팀당 Helper가 정확히 1명인가?
            long helperCount = members.stream().filter(m -> m.getRole() == MemberRole.HELPER).count();
            assertThat(helperCount).isEqualTo(1);

            // 팀당 인원 균형 (50명 / 6팀 = 8명 또는 9명이어야 함)
            assertThat(members.size()).isIn(8, 9);
        }

        // 3. 장애우 분산 검증 (5명은 각각 다른 팀에 있어야 함)
        for (Team team : mockTeams) {
            long disabledInTeam = team.getMembers().stream().filter(Member::isHard).count();
            assertThat(disabledInTeam).isLessThanOrEqualTo(1);
            // 5명인데 팀이 6개이므로 각 팀에 최대 1명씩만 있어야 함
        }
    }
}