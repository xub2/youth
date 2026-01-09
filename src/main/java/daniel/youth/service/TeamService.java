package daniel.youth.service;

import daniel.youth.domain.Team;
import daniel.youth.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;

    public List<Team> findAll() {
        return teamRepository.findAll();
    }


}
