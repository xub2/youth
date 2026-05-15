package daniel.youth.service;

import daniel.youth.domain.Member;
import daniel.youth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    @Transactional
    public void save(Member member) {
        try {
            memberRepository.save(member);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    member.getName() + " 은(는) 이미 존재하는 이름입니다. "
                            + member.getName() + "A 와 같이 입력해주세요."
            );
        }
    }

    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    @Transactional
    public void delete(Member member) {
        memberRepository.delete(member);
    }

    @Transactional
    public void deleteById(Long id) {
        memberRepository.deleteById(id);
    }

    @Transactional
    public void deleteAll() {
        memberRepository.deleteAll();
    }

    // 벌크연산 주의
    @Transactional
    public void clear() {
        memberRepository.clearAll();
    }

    public List<Member> findAllByNameAsc() {
        return memberRepository.findAllByOrderByNameAsc();
    }

    // 팀에 소속된 멤버들 지우기 -> 즉 모든 멤버의 팀을 null 로 만들기
    @Transactional
    public void unassignAllMembers() {
        memberRepository.clearAllMemberTeams();
    }

    public boolean existsByName(String name) {
        return memberRepository.existsByName(name);
    }

    @Transactional
    public void toggleExclude(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 인원입니다."));

        // 현재 상태의 반대값으로 설정 (true -> false, false -> true)
        member.setExcluded(!member.isExcluded());
    }
}
