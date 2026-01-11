package daniel.youth.controller;

import daniel.youth.domain.Member;
import daniel.youth.domain.Team;
import daniel.youth.service.GroupAssignmentService;
import daniel.youth.service.MemberService;
import daniel.youth.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GroupAssignmentController {

    private final GroupAssignmentService groupAssignmentService;
    private final TeamService teamService;
    private final MemberService memberService;

    /**
     * 랜덤 목장 생성 및 배정 실행
     */
    @PostMapping("/assign")
    public String assignGroups(@RequestParam(defaultValue = "6") int teamCount,
                               RedirectAttributes redirectAttributes) {
        try {
            // 1. 선택된 개수만큼 팀을 먼저 생성 (기존 팀 삭제 포함)
            teamService.createTeams(teamCount);

            // 2. 생성된 팀을 바탕으로 랜덤 배정 실행
            groupAssignmentService.assignGroups();

            redirectAttributes.addFlashAttribute("message", teamCount + "개 목장으로 편성이 완료되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "배정 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/assign/result";
    }

    @GetMapping("/assign/result")
    public String getAssignmentResult(Model model) {
        // 배정된 결과를 보여주기 위해 최신 데이터 로드
        List<Team> teams = teamService.findAll();
        List<Member> members = memberService.findAll();

        model.addAttribute("teams", teams);
        model.addAttribute("members", members);
        model.addAttribute("membersCount", members.size());
        return "index";
    }
}