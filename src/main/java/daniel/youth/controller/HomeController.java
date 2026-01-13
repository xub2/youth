package daniel.youth.controller;

import daniel.youth.domain.Notice;
import daniel.youth.domain.playlist.Playlist;
import daniel.youth.service.NoticeService;
import daniel.youth.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final NoticeService noticeService;
    private final PlaylistService playlistService;

    @GetMapping("/")
    public String home(Model model) {
        Notice latestNotice = noticeService.getLatestNotice();
        model.addAttribute("noticeContent", latestNotice != null ? latestNotice.getContent() : null);

        // 플레이리스트 추가
        Playlist latestPlaylist = playlistService.getLatestPlaylist();
        model.addAttribute("playlistItems", latestPlaylist != null ? latestPlaylist.getItems() : new ArrayList<>());

        return "index";
    }
}
