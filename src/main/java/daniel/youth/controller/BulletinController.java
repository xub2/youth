package daniel.youth.controller;

import daniel.youth.domain.Bulletin;
import daniel.youth.service.BulletinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class BulletinController {

    private final BulletinService bulletinService;

    /**
     * 관리자용: 주보 PDF 업로드 처리
     */
    @PostMapping("/admin/bulletin/upload")
    public String uploadBulletin(@RequestParam("file") MultipartFile file, RedirectAttributes re) {
        if (file.isEmpty() || !file.getContentType().equals("application/pdf")) {
            re.addFlashAttribute("error", "올바른 PDF 파일을 선택해주세요.");
            return "redirect:/";
        }

        try {
            bulletinService.updateBulletin(file);
            re.addFlashAttribute("message", "주보가 성공적으로 업데이트되었습니다.");
        } catch (IOException e) {
            re.addFlashAttribute("error", "파일 저장 중 오류가 발생했습니다.");
        }

        return "redirect:/";
    }

    /**
     * 사용자용: 최신 주보 PDF 스트리밍 서비스
     */
    @GetMapping("/bulletin/latest")
    public ResponseEntity<byte[]> viewLatestBulletin() {
        Bulletin bulletin = bulletinService.getLatestBulletin();

        if (bulletin == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                // inline 설정으로 브라우저에서 바로 열리도록 유도
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + bulletin.getFileName() + "\"")
                .body(bulletin.getFileData());
    }
}
