package com.tj.util.unique.service.impl;

import com.tj.util.unique.service.IMachineFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.PreDestroy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 创建时间：2017/3/1
 * 创建人： by LeWis
 */
@Slf4j
public class RedisMachineFactory implements IMachineFactory {

    private String redisKey = "MACHINE_NS";
    private RedisOperations redis;
    private List<Long> machines;
    private Long mMachineId;
    private boolean debug = true;

    private RedisSerializer<String> getSerializer() {
        return new StringRedisSerializer();
    }

    @Override
    public Long machineId(long maxVal) {
        if (redis == null) {
            throw new RuntimeException("redis未配置");
        }
        log("机器码最大值：" + maxVal);
        Long machineId = -1l;
        boolean isOk = false;
        if (machines != null) {
            //已有机器码数量不能最大机器码数
            do {
                //增加机器码
                machineId++;
                //本地判断机器码是否存在
                if (!machines.contains(machineId)) {
                    //保存设备
                    isOk = save(machineId);
                    if (isOk) {
                        mMachineId = machineId;
                    }
                    log("当前机器码：" + machineId);
                } else if (machineId >= maxVal) {
                    throw new RuntimeException("机器码已用完！");
                }
            } while (machines.contains(machineId) && !isOk);
        }
        machines = null;
        return machineId;
    }

    @Override
    @PreDestroy
    public void destroy() {
        if (mMachineId != null) {
            remove(mMachineId);
        }
    }

    @Override
    public void init() {
        this.machines = loadMachine();
    }

    private void log(String info) {
        if (debug) {
            log.debug(info);
        }
    }

    //保存设备号
    private boolean save(final Long machineId) {
        //启用分布式锁
        boolean success = (boolean) redis.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("{\"t\":\"");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                stringBuilder.append(dateFormat.format(new Date()));
                stringBuilder.append("\"}");
                log("保存机器码：" + stringBuilder.toString());
                return redisConnection.hSetNX(
                        getSerializer().serialize(redisKey),
                        getSerializer().serialize(String.valueOf(machineId)),
                        getSerializer().serialize(stringBuilder.toString()));
            }
        });
        log("保存机器码结果：" + success);
        if (success) {
            return true;
        } else {
            log.debug("机器码已存在！");
            return false;
        }
    }

    //删除设备号
    private void remove(final Long machineId) {
        Long result = (Long) redis.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.hDel(
                        getSerializer().serialize(redisKey),
                        getSerializer().serialize(String.valueOf(machineId)));
            }
        });
        log("移除机器码结果：" + result);
    }

    //获取设备
    private List<Long> loadMachine() {
        log("获取已有机器码");
        List<Long> list = (List<Long>) redis.execute(new RedisCallback<List<Long>>() {
            @Override
            public List<Long> doInRedis(RedisConnection redisConnection) throws DataAccessException {
                Map<byte[], byte[]> map = redisConnection.hGetAll(getSerializer().serialize(redisKey));
                if (map == null) {
                    return new ArrayList<Long>();
                }
                List<Long> machineList = new ArrayList<Long>();
                for (byte[] key : map.keySet()) {
                    machineList.add(Long.parseLong(getSerializer().deserialize(key)));
                }
                return machineList;
            }
        });
        log("机器码数：" + list.size());
        StringBuilder stringBuilder = new StringBuilder();
        for (Long id : list) {
            stringBuilder.append("###############");
            stringBuilder.append(id);
            stringBuilder.append("###############\r\n");
        }
        if (stringBuilder.length() > 1) {
            log("机器码：\r\n" + stringBuilder.toString());
        }
        return list;
    }

    public RedisOperations getRedis() {
        return redis;
    }

    public void setRedis(RedisOperations redis) {
        this.redis = redis;
    }

    public String getRedisKey() {
        return redisKey;
    }

    public void setRedisKey(String redisKey) {
        this.redisKey = redisKey;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
