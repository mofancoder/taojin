package com.tj.event.subscribe;

public interface RedisMsg {
    void handleMessage(String message);
}
