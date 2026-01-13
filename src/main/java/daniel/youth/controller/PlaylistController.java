package daniel.youth.controller;

import daniel.youth.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping("/admin/playlist/update")
    public String updatePlaylist(@RequestParam("titles") List<String> titles,
                                 @RequestParam("videoIds") List<String> videoIds,
                                 RedirectAttributes redirectAttributes) {
        try {
            playlistService.updatePlaylist(titles, videoIds);
            redirectAttributes.addFlashAttribute("message", "플레이리스트가 성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "플레이리스트 업데이트 중 오류가 발생했습니다.");
        }
        return "redirect:/";
    }
}
