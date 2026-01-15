package daniel.youth.utils;

import daniel.youth.domain.Member;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class MemberCsvExporter {

    public void export(List<Member> members, OutputStream os) throws IOException {
        // 1. BOM 쓰기 (엑셀 한글 깨짐 방지)
        os.write(0xEF);
        os.write(0xBB);
        os.write(0xBF);

        // 2. Writer 생성
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
            // 헤더 작성
            bw.write("번호,이름");
            bw.newLine();

            // 데이터 작성
            int index = 1;
            for (Member m : members) {
                // CSV Injection 방지 및 포맷팅
                String line = String.format("%d,\"%s\"", index++, escapeSpecialCharacters(m.getName()));
                bw.write(line);
                bw.newLine();
            }

            // 푸터(총 인원) 작성
            bw.newLine();
            bw.write(String.format("총 인원,%d명", members.size()));
            bw.newLine();

            bw.flush();
        }
    }

    // (선택 사항) CSV 특수문자 처리 헬퍼 메서드
    private String escapeSpecialCharacters(String data) {
        if (data == null) return "";
        // 따옴표가 있는 경우 이스케이프 처리 (" -> "")
        if (data.contains("\"")) {
            data = data.replace("\"", "\"\"");
        }
        return data;
    }
}
