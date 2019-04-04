package com.tj.event.factory;

import com.tj.dto.EventSimpleNameDto;
import com.tj.dto.RedisRaceInfo;
import com.tj.event.dao.CategoryMappingMapper;
import com.tj.event.dao.TeamMappingMapper;
import com.tj.event.domain.*;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.util.List;

public class EventNameConvert {
    @Resource
    private TeamMappingMapper teamMappingMapper;
    @Resource
    private CategoryMappingMapper categoryMappingMapper;

    private List<TeamMapping> enTeam;
    private List<CategoryMapping> enCategory;

    public EventSimpleNameDto convertName(Object object) {
        RedisRaceInfo redisRaceInfo = null;
        if (object instanceof RedisRaceInfo) {
            redisRaceInfo = (RedisRaceInfo) object;
        }else if (object instanceof RaceInfo){
            BeanUtils.copyProperties(object, redisRaceInfo);
        }

        EventSimpleNameDto eventSimpleName = EventSimpleNameDto.builder()
                .simpleCategory(convertCategory(redisRaceInfo.getCategory()))
                .simpleHomeTeam(convertTeam(redisRaceInfo.getHomeTeam()))
                .simpleVisitTeam(convertTeam(redisRaceInfo.getVisitTeam()))
                .build();

        return eventSimpleName;
    }

    public String convertCategory(String category) {
        CategoryMappingExample categoryExample = new CategoryMappingExample();
        categoryExample.or().andSimpleFullCategoryEqualTo(category);
        enCategory = categoryMappingMapper.selectByExample(categoryExample);
        return enCategory.size() > 0 ? enCategory.get(0).getSimpleShortCategory() : category;
    }

    public String convertTeam(String team) {
        TeamMappingExample teamExample = new TeamMappingExample();
        teamExample.or().andSimpleTeamEqualTo(team);
        enTeam = teamMappingMapper.selectByExample(teamExample);
        return enTeam.size() > 0 ? enTeam.get(0).getSimpleTeam() : team;
    }

    public String convertTeams(String teams) {
        String [] teamNames = null;
        if (teams.trim().contains(":")) {
            teamNames = teams.split(":");
            return convertTeam(teamNames[0]) + ":" + convertTeam(teamNames[1]);
        }else if (teams.trim().contains("-")) {
            return convertTeam(teamNames[0]) + "-" + convertTeam(teamNames[1]);
        }
        return teams;
    }
}
