package daniel.youth.controller;

import daniel.youth.domain.Member;
import daniel.youth.domain.MemberRole;
import daniel.youth.domain.Team;
import daniel.youth.repository.MemberRepository;
import daniel.youth.repository.TeamRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    /**
     * 메인 화면: 출석 명단 리스트 + 등록 폼
     */
    @GetMapping("/")
    public String index(Model model) {
        List<Member> members = memberRepository.findAll();
        List<Team> teams = teamRepository.findAll(); // 배정 결과가 있다면 보여주기 위해 추가

        model.addAttribute("members", members);
        model.addAttribute("membersCount", members.size());
        model.addAttribute("teams", teams);
        return "index";
    }

    @GetMapping("/member/list/admin")
    public String memberListForAdmin(Model model) {
        List<Member> members = memberRepository.findAll();
        model.addAttribute("members", members);

        return "member-list-for-admin";
    }


    /**
     * 명단 한 명씩 추가
     */
    @PostMapping("/member/add")
    public String addMember(@RequestParam String name,
                            @RequestParam(defaultValue = "false") boolean isHard,
                            RedirectAttributes redirectAttributes) {

        Member member = Member.builder()
                .name(name)
                .isHard(isHard)
                .role(MemberRole.NORMAL)
                .build();

        memberRepository.save(member);
        redirectAttributes.addFlashAttribute("message", name + " 님이 등록되었습니다.");
        return "redirect:/member/list/admin";
    }

    /**
     * 개별 명단 삭제
     */
    @PostMapping("/member/delete/{id}")
    public String deleteMember(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        memberRepository.findById(id).ifPresent(member -> {
            String name = member.getName();
            memberRepository.delete(member);
            redirectAttributes.addFlashAttribute("message", name + " 님이 삭제되었습니다.");
        });
        return "redirect:/";
    }

    // 관리자 페이지 전용 삭제 로직
    @PostMapping("/member/admin/delete/{id}")
    public String deleteMemberFromAdmin(@PathVariable Long id) {
        memberRepository.deleteById(id);
        // 삭제 후 다시 관리자 명단 페이지로 리다이렉트
        return "redirect:/member/list/admin";
    }

    /**
     * 전체 명단 초기화 (매주 새로 시작)
     */
    @PostMapping("/member/clear")
    public String clearMembers(RedirectAttributes redirectAttributes) {
        memberRepository.deleteAll();
        redirectAttributes.addFlashAttribute("message", "모든 명단이 초기화되었습니다.");
        return "redirect:/";
    }

    // 다운로드 ?
    @GetMapping("member/export")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        // 1. 파일명 설정 (날짜 포맷 정렬)
        String dateStr = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now());
        String fileName = URLEncoder.encode(dateStr + "_출석명단.csv", StandardCharsets.UTF_8);

        List<Member> members = memberRepository.findAll();

        // 2. 응답 설정 (UTF-8 명시)
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);


        // 3. 한글 깨짐 및 칸 밀림 방지를 위해 BufferedWriter와OutputStreamWriter 사용
        // OutputStream에 BOM(\ufeff)을 먼저 쓰고 시작합니다.
        response.getOutputStream().write(0xEF);
        response.getOutputStream().write(0xBB);
        response.getOutputStream().write(0xBF);

        PrintWriter writer = new PrintWriter(new java.io.OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8));

        // 4. 헤더 작성
        // (A열은 비우고 B열 1행에 '이름' 배치)
        // CSV에서 빈 칸은 쉼표(,)로 구분합니다.
        writer.println("번호, 이름");

        // 5. 데이터 작성
        int index = 1;
        for (Member m : members) {
            // 혹시 이름에 쉼표가 들어있을 경우를 대비해 큰따옴표로 감쌉니다.
            writer.println(String.format("%d,%s", index++, m.getName()));
        }

        writer.println(); // 한 줄 띄우기
        writer.println(String.format("총 인원,%d명", members.size()));

        writer.flush();
        writer.close();
    }
}