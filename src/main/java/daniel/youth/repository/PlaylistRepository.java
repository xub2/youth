package daniel.youth.repository;

import daniel.youth.domain.playlist.Playlist;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    // 페치 조인 최적화
    // 생각해보니 이렇게 하면 메모리 페이징 시도
    @Query("select p from Playlist p left join fetch p.items order by p.uploadDate desc limit 1")
    Playlist findTopWithItemsOrderByUploadDateDesc();

    Playlist findTopByOrderByUploadDateDesc();

    // 2. ID로 상세 조회할 때만 연관된 items를 가져옴
    @EntityGraph(attributePaths = {"items"})
    @Query("select p from Playlist p where p.id = :id")
    Playlist findWithItemsById(Long id);
}
