package daniel.youth.repository;

import daniel.youth.domain.Team;
import daniel.youth.domain.TeamName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByTeamName(TeamName teamName);

    @Modifying(flushAutomatically = true)
    @Query("delete from Team t")
    void deleteAllTeam();

}
