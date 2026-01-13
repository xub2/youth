package daniel.youth.service;

import daniel.youth.domain.Notice;
import daniel.youth.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;

    /**
     * 현재 최신 공지 사항 가져오기
     */
    public Notice getLatestNotice() {
        return noticeRepository.findTopByOrderByLastModifiedDesc();
    }

    @Transactional
    public void updateNotice(String content) {
        Notice latestNotice = getLatestNotice();

        if (latestNotice != null) {
            latestNotice.setContent(content);
            latestNotice.setLastModified(LocalDateTime.now());
        } else {
            Notice newNotice = new Notice();
            newNotice.setContent(content);
            newNotice.setLastModified(LocalDateTime.now());
            noticeRepository.save(newNotice);
        }
    }


}
