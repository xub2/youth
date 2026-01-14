package daniel.youth.service;

import daniel.youth.domain.playlist.Playlist;
import daniel.youth.domain.playlist.PlaylistItem;
import daniel.youth.repository.PlaylistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaylistService {

    private final PlaylistRepository playlistRepository;

    public Playlist getLatestPlaylist() {
//        return playlistRepository.findTopByOrderByUploadDateDesc();
        return playlistRepository.findTopWithItemsOrderByUploadDateDesc();
    }

    @Transactional
    public void updatePlaylist(List<String> titles, List<String> videoIds) {
        // 기존 최신 플레이리스트를 가져오거나 없으면 새로 생성
        Playlist playlist = getLatestPlaylist();
        if (playlist == null) {
            playlist = new Playlist();
        }

        playlist.setUploadDate(LocalDateTime.now());
        playlist.getItems().clear(); // 기존 곡 목록 삭제 (orphanRemoval로 DB에서도 삭제됨)

        for (int i = 0; i < titles.size(); i++) {
            if (titles.get(i).trim().isEmpty()) continue; // 제목 없는 경우 건너뜀

            PlaylistItem item = new PlaylistItem();
            item.setTitle(titles.get(i));
            item.setVideoId(extractVideoId(videoIds.get(i))); // ID 추출 로직 적용
            item.setDisplayOrder(i);
            item.setPlaylist(playlist);
            playlist.getItems().add(item);
        }

        playlistRepository.save(playlist);
    }

    private String extractVideoId(String url) {
        if (url == null || url.trim().isEmpty()) return "";
        url = url.trim();

        // 1. 이미 11자리 ID만 입력된 경우 (예: S_8qK65rXGk)
        if (url.length() == 11 && !url.contains("/") && !url.contains("=")) {
            return url;
        }

        // 2. 일반적인 watch?v= 형식 처리
        if (url.contains("v=")) {
            String[] parts = url.split("v=");
            String videoId = parts[1];
            int ampersandIndex = videoId.indexOf("&");
            return (ampersandIndex != -1) ? videoId.substring(0, ampersandIndex) : videoId;
        }

        // 3. 공유하기(youtu.be/) 단축 주소 형식 처리
        if (url.contains("youtu.be/")) {
            String[] parts = url.split("youtu.be/");
            String videoId = parts[1];
            int questionMarkIndex = videoId.indexOf("?");
            return (questionMarkIndex != -1) ? videoId.substring(0, questionMarkIndex) : videoId;
        }

        // 4. youtube.com/embed/ 형식 처리
        if (url.contains("embed/")) {
            String[] parts = url.split("embed/");
            String videoId = parts[1];
            int questionMarkIndex = videoId.indexOf("?");
            return (questionMarkIndex != -1) ? videoId.substring(0, questionMarkIndex) : videoId;
        }

        return url; // 분석 실패 시 일단 원문 반환
    }
}
