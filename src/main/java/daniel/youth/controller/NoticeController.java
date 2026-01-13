package daniel.youth.controller;

import daniel.youth.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping("/admin/notice/update")
    public String updateNotice(@RequestParam("content") String content, RedirectAttributes redirectAttributes) {
        try {
            noticeService.updateNotice(content);
            redirectAttributes.addFlashAttribute("message", "공지사항이 업데이트 되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "공지사항 업데이트 중 오류가 발생했습니다.");
        }

        return "redirect:/";
    }
}
