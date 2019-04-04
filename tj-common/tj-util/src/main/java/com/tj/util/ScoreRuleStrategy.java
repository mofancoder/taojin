package com.tj.util;

import com.tj.dto.ScoreRule;
import com.tj.util.enums.BetTypeEnum;
import org.springframework.stereotype.Component;

/**
 * @program: tj-core
 * @description: ${description}
 * @author: liang.song
 * @create: 2018-12-17-10:24
 **/
@Component
public class ScoreRuleStrategy extends AbstractRule<ScoreRule> {

    @Override
    public BetTypeEnum type() {
        return BetTypeEnum.score;
    }
}
