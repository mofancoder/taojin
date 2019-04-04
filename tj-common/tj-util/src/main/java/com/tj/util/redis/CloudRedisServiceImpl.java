package com.tj.util.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tj.util.enums.RedisKeys;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author yelo
 */
@Service
public class CloudRedisServiceImpl implements CloudRedisService {

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public boolean saveHash(final RedisKeys nameSpace, final Object key, final Object objParams, final Date outOfDate) {
        redisTemplate.opsForHash().put(nameSpace.toString(), key.toString(), objParams);
        long time = Math.abs(outOfDate.getTime() - new Date().getTime()) / 1000;
        return redisTemplate.expire(nameSpace.toString(), time, TimeUnit.SECONDS);
    }

    @Override
    public boolean saveHash(RedisKeys nameSpace, Object key, Object objParams, long expire) {
        redisTemplate.opsForHash().put(nameSpace.toString(), key.toString(), objParams);
        return redisTemplate.expire(nameSpace.toString(), expire, TimeUnit.SECONDS);
    }

    public <T> T selectHash(RedisKeys nameSpace, Object key, Class<T> clazz) {
        Object o = redisTemplate.opsForHash().get(nameSpace.toString(), key.toString());
        if (o == null) {
            return null;
        }
        if (isJson(o)) {
            return JSON.parseObject(JSON.toJSONString(o), clazz);
        }
        return (T) o;

    }

    @Override
    public boolean saveHash(RedisKeys nameSpace, Object key, Object objParams) {
        redisTemplate.opsForHash().put(nameSpace.toString(), key.toString(), objParams);
        return true;
    }

    @Override
    public long deleteHash(final RedisKeys nameSpace, final Object key) {
        return redisTemplate.opsForHash().delete(nameSpace.toString(), key.toString());
    }

    @Override
    public boolean update(final RedisKeys nameSpace, final Object key, final Object objParams) {
        redisTemplate.opsForHash().put(nameSpace.toString(), key.toString(), objParams);
        return true;
    }


    @Override
    public boolean save(final Object key, final Object value) {
        redisTemplate.opsForValue().set(key.toString(), value);
        return true;
    }

    @Override
    public long delete(final Object key) {
        redisTemplate.delete(key.toString());
        return 1L;
    }

    @Override
    public boolean update(final Object key, final Object value) {
        redisTemplate.opsForValue().set(key.toString(), value);
        return true;
    }

    @Override
    public <T> T select(Object key, Class<T> clazz) {
        Object o = redisTemplate.opsForValue().get(key.toString());

        if (o == null) {
            return null;
        }
        if (isJson(o)) {
            return JSON.parseObject(JSON.toJSONString(o), clazz);
        }
        return (T) o;

    }

    @Override
    public <T> List<T> selectList(Object key, Class<T> clazz) {
        JSONArray o = (JSONArray) redisTemplate.opsForValue().get(key.toString());
        return o == null ? null : JSONArray.parseArray(o.toJSONString(), clazz);
    }

    @Override
    public boolean save(final Object key, final Object value, final Long time) {
        redisTemplate.opsForValue().set(key.toString(), value);
        if (time != -1L) {
            return redisTemplate.expire(key.toString(), time, TimeUnit.SECONDS);
        }
        return true;
    }

    @Override
    public boolean save(final Object key, final Object value, final Date outOfDate) {
        long time = Math.abs(outOfDate.getTime() - new Date().getTime()) / 1000;
        return save(key.toString(), value, time);
    }

    @Override
    public boolean expire(final Object key, final Long time) {
        return redisTemplate.expire(key.toString(), time, TimeUnit.SECONDS);
    }

    @Override
    public boolean expire(Object key, Date outOfDate) {
        long time = Math.abs(outOfDate.getTime() - new Date().getTime()) / 1000;
        return expire(key.toString(), time);
    }

    @Override
    public boolean updateOutTime(Object key, Object value, Long time) {
        redisTemplate.opsForValue().set(key.toString(), value, time, TimeUnit.SECONDS);
        return true;
    }

    @Override
    public Long ttl(Object key) {
        return redisTemplate.getExpire(key.toString());
    }

    @Override
    public boolean setNX(Object key, Object value) {
        return (boolean) redisTemplate.execute((RedisCallback) redisConnection ->
                redisConnection.setNX(key.toString().getBytes(StandardCharsets.UTF_8),
                        value.toString().getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public Long rPush(Object key, Object value) {
        return redisTemplate.opsForList().rightPush(key.toString(), value);
    }

    @Override
    public Long lPush(Object key, Object value) {
        return redisTemplate.opsForList().leftPush(key.toString(), value);
    }

    @Override
    public <T> List<T> range(Object key, Integer start, Integer end, Class<T> clazz) {
        List<Object> range = redisTemplate.opsForList().range(key.toString(), start, end);
        return range == null ? null : JSON.parseArray(range.toString(), clazz);
    }

    @Override
    public Long size(Object key) {
        return redisTemplate.opsForList().size(key.toString());
    }

    @Override
    public <T> T lPop(Object key, Class<T> clazz) {
        Object o = redisTemplate.opsForList().leftPop(key.toString());
        if (o == null) {
            return null;
        }
        if (isJson(o)) {
            return JSON.parseObject(JSON.toJSONString(o), clazz);
        }
        return (T) o;
    }

    @Override
    public <T> T rPop(Object key, Class<T> clazz) {
        Object o = redisTemplate.opsForList().rightPop(key.toString());
        if (o == null) {
            return null;
        }
        if (isJson(o)) {
            return JSON.parseObject(JSON.toJSONString(o), clazz);
        }
        return (T) o;

    }

    public <T> T index(Object key, Integer index, Class<T> clazz) {
        Object o = redisTemplate.opsForList().index(key.toString(), index);

        if (o == null) {
            return null;
        }
        if (isJson(o)) {
            return JSON.parseObject(JSON.toJSONString(o), clazz);
        }
        return (T) o;
    }

    @Override
    public Double AutoIncrementOutTime(Object key, double value, Long time) {
        BoundValueOperations<String, Object> bound = redisTemplate.boundValueOps(key.toString());
        Double rs = bound.increment(value);
        bound.expire(time, TimeUnit.SECONDS);
        return rs;
    }

    @Override
    public Double AutoIncrementOrUpdate(Object key, double value) {
        BoundValueOperations<String, Object> bound = redisTemplate.boundValueOps(key.toString());
        Double rs = bound.increment(value);
        return rs;
    }

    private boolean isJson(Object o) {
        if (o instanceof JSONObject) {
            return true;
        } else if (o instanceof char[]) {
            char[] chars = (char[]) o;
            if (chars.length > 0) {
                if (chars[0] == '{') {
                    return true;
                }
            }
        }
        return false;
    }


}
