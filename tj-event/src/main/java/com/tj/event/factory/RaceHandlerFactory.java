package com.tj.event.factory;

import com.tj.event.service.RaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RaceHandlerFactory {

    @Autowired
    private Map<String, RaceService> handlerMap;

    public RaceService getRaceHandleByName(String name) {
        return handlerMap.get(name);
    }

}
