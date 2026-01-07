package daniel.youth.repository;

import daniel.youth.domain.Team;
import daniel.youth.domain.TeamName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByTeamName(TeamName teamName);
}
