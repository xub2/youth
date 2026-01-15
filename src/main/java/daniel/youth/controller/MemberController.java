package daniel.youth.controller;

import daniel.youth.domain.Member;
import daniel.youth.domain.MemberRole;
import daniel.youth.domain.Team;
import daniel.youth.service.MemberService;
import daniel.youth.service.TeamService;
import daniel.youth.utils.MemberCsvExporter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final TeamService teamService;

    private final MemberCsvExporter memberCsvExporter;

//    @Value("${kakao.map.api.key}")
//    private String kakaoMapKey;

    /**
     * 메인 화면: 출석 명단 리스트 + 등록 폼
     */
    @GetMapping("/member/assign")
    public String index(Model model) {
        List<Member> members = memberService.findAll();
        List<Team> teams = teamService.findAll(); // 배정 결과가 있다면 보여주기 위해 추가

        model.addAttribute("members", members);
        model.addAttribute("membersCount", members.size());
        model.addAttribute("teams", teams);
//        model.addAttribute("kakaoMapKey", kakaoMapKey);
        return "assignment-view";
    }

    @GetMapping("/member/list/admin")
    public String memberListForAdmin(Model model) {
        List<Member> members = memberService.findAllByNameAsc();
        model.addAttribute("members", members);

        return "member-list-for-admin";
    }


    /**
     * 명단 한 명씩 추가
     * 동명이인 로직 개선 1/11
     */
    @PostMapping("/member/add")
    public String addMember(@RequestParam String name,
                            @RequestParam(defaultValue = "false") boolean isHard,
                            RedirectAttributes redirectAttributes) {

        try {
            Member member = Member.builder()
                    .name(name)
                    .isHard(isHard)
                    .role(MemberRole.NORMAL)
                    .isExcluded(false)
                    .build();

            memberService.save(member);
            redirectAttributes.addFlashAttribute("message", name + " 님이 등록되었습니다.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/member/list/admin";
    }

    /**
     * 개별 명단 삭제
     */
    @PostMapping("/member/delete/{id}")
    public String deleteMember(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        memberService.findById(id).ifPresent(member -> {
            String name = member.getName();
            memberService.delete(member);
            redirectAttributes.addFlashAttribute("message", name + " 님이 삭제되었습니다.");
        });
        return "redirect:/";
    }

    // 관리자 페이지 전용 삭제 로직
    @PostMapping("/member/admin/delete/{id}")
    public String deleteMemberFromAdmin(@PathVariable Long id) {
        memberService.deleteById(id);
        // 삭제 후 다시 관리자 명단 페이지로 리다이렉트
        return "redirect:/member/list/admin";
    }

    /**
     * 전체 명단 초기화 (매주 새로 시작)
     */
    @PostMapping("/member/clear")
    public String clearMembers(RedirectAttributes redirectAttributes) {
//        memberService.deleteAll(); // 여기서 N+1 문제 발생했었음
        memberService.clear();
        redirectAttributes.addFlashAttribute("message", "모든 명단이 초기화되었습니다.");
        return "redirect:/member/list/admin";
    }

    @PostMapping("/member/unassign")
    public String unassignMember(RedirectAttributes redirectAttributes) {
        try {
            memberService.unassignAllMembers();
            redirectAttributes.addFlashAttribute("message", "목장 배정 결과가 초기화되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "초기화 중 오류가 발생했습니다.");
        }

        return "redirect:/member/assign";
    }

    /**
     * 출석 인원 CSV 다운로드
     */
    @GetMapping("member/export")
    public void exportToCSV(HttpServletResponse response) throws IOException {

        // 1. 데이터 조회
        List<Member> members = memberService.findAllByNameAsc();

        // 2. 파일명 생성
        String dateStr = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now());
        String fileName = URLEncoder.encode(dateStr + "_출석명단.csv", StandardCharsets.UTF_8);

        // 3. HTTP 응답 헤더 설정
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        // 4. CSV 생성 위임 (핵심 로직 분리)
        memberCsvExporter.export(members, response.getOutputStream());
    }

    @PostMapping("/member/exclude/{id}")
    public String toggleExclude(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        try {
            memberService.toggleExclude(id);
            // redirectAttributes.addFlashAttribute("message", "상태가 변경되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "변경 중 오류가 발생했습니다.");
        }
        return "redirect:/member/list/admin";
    }
}