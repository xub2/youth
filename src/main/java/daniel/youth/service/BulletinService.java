package daniel.youth.service;

import daniel.youth.domain.Bulletin;
import daniel.youth.repository.BulletinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BulletinService {

    private final BulletinRepository bulletinRepository;

    /**
     * 최신 주보 가져오기
     */
    public Bulletin getLatestBulletin(){
        return bulletinRepository.findTopByOrderByUploadDateDesc();
    }

    /**
     * 주보 업로드 -> 기존 주보 날리고 새로 등록
     */
    @Transactional
    public void updateBulletin(MultipartFile file) throws IOException {
        bulletinRepository.deleteAll();

        Bulletin bulletin = new Bulletin();
        bulletin.setFileData(file.getBytes());
        bulletin.setFileName(file.getOriginalFilename());
        bulletin.setUploadDate(LocalDateTime.now());

        bulletinRepository.save(bulletin);
    }
}
