package daniel.youth.controller;

import daniel.youth.domain.Member;
import daniel.youth.domain.Team;
import daniel.youth.repository.MemberRepository;
import daniel.youth.repository.TeamRepository;
import daniel.youth.service.GroupAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GroupAssignmentController {

    private final GroupAssignmentService groupAssignmentService;
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;

    /**
     * 랜덤 목장 배정 실행
     */
    @PostMapping("/assign")
    public String assignGroups(RedirectAttributes redirectAttributes) {
        try {
            groupAssignmentService.assignGroups();
            redirectAttributes.addFlashAttribute("message", "정상 등록 되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "배정 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/assign/result";
    }

    @GetMapping("/assign/result")
    public String getAssignmentResult(Model model) {
        List<Team> teams = teamRepository.findAll();
        List<Member> members = memberRepository.findAll(); // 전체 명단 다시 가져오기

        model.addAttribute("teams", teams);
        model.addAttribute("members", members); // 뷰에 명단 전달
        model.addAttribute("membersCount", members.size()); // 인원수 전달
        return "index";
    }
}