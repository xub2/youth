package daniel.youth.repository;

import daniel.youth.domain.playlist.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    Playlist findTopByOrderByUploadDateDesc();

    // 페치 조인 최적화
    @Query("select p from Playlist p left join fetch p.items order by p.uploadDate desc limit 1")
    Playlist findTopWithItemsOrderByUploadDateDesc();
}
