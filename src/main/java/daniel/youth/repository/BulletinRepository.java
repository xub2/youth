package daniel.youth.repository;

import daniel.youth.domain.Bulletin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BulletinRepository extends JpaRepository<Bulletin, Long> {
    Bulletin findTopByOrderByUploadDateDesc();
}
