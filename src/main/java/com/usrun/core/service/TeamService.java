package com.usrun.core.service;

import com.usrun.core.model.Team;
import com.usrun.core.model.User;
import com.usrun.core.model.type.LeagueType;
import com.usrun.core.repository.TeamRepository;
import com.usrun.core.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeamService {

    private static final Logger logger = LoggerFactory.getLogger(TeamService.class);

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AmazonClient amazonClient;

    @Transactional
    public Team createTeam(
            Long userId, String name, String img, String logo, Integer leagueId, String description
    ) {
        User user = userRepository.findById(userId).get();
//        if(user.getTeam() != null)
//            throw new CodeException(ErrorCode.USER_IS_OWNER);

        Team team = new Team();

        team.setUser(user);
        user.setTeam(team);

        team.setName(name);

        teamRepository.save(team);
        logger.info("Create team {}", team.getId());

        return updateTeam(user, name, img, logo, leagueId, description);
    }

    private Team updateTeam(
            User user, String name, String img, String logo, Integer leagueId, String description
    ) {
        Team team = user.getTeam();

        team.setName(name);

        LeagueType[] leagueTypes = LeagueType.values();
        if(leagueId != null && leagueId < leagueTypes.length) {
            team.setLeagueType(leagueTypes[leagueId]);
        }

        if(img != null) {
            String imgUrl = amazonClient.uploadFile(img, "team-" + team.getId() + "-img");
            team.setImg(imgUrl);
        }

        if(logo != null) {
            String logoUrl = amazonClient.uploadFile(logo, "team-" + team.getId() + "-logo");
            team.setLogo(logoUrl);
        }

        if(description != null) {
            team.setDescription(description);
        }

        teamRepository.save(team);
        logger.info("Update team {}", team.getId());
        return team;
    }
}
