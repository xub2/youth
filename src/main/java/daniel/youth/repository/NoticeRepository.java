package daniel.youth.repository;

import daniel.youth.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    Notice findTopByOrderByLastModifiedDesc();
}
