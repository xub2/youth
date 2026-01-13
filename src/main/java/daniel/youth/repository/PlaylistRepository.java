package daniel.youth.repository;

import daniel.youth.domain.playlist.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    Playlist findTopByOrderByUploadDateDesc();
}
