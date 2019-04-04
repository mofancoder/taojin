package com.tj.util.redis;


import com.tj.util.enums.RedisKeys;

import java.util.Date;
import java.util.List;

/**
 * redis，集群共享用
 *
 * @author yelo
 */
public interface CloudRedisService {

    /**
     * 保存
     *
     * @param redisKey  域名空间 CUSTOMER
     * @param key       键 112313123123
     * @param objParams 值 {"id:...","name":""}
     * @param outOfDate now()+30
     */
    boolean saveHash(final RedisKeys redisKey, final Object key, final Object objParams, final Date outOfDate);

    /**
     * 保存
     *
     * @param redisKey  域名空间 CUSTOMER
     * @param key       键 112313123123
     * @param objParams 值 {"id:...","name":""}
     * @param expire    time unit: second
     */
    boolean saveHash(final RedisKeys redisKey, final Object key, final Object objParams, final long expire);

    <T> T selectHash(RedisKeys nameSpace, Object key, Class<T> clazz);

    boolean saveHash(final RedisKeys redisKey, final Object key, final Object objParams);

    /**
     * 删除
     *
     * @param redisKey 域名空间
     * @param key      键
     */
    long deleteHash(RedisKeys redisKey, Object key);

    /**
     * 更新
     */
    boolean update(RedisKeys redisKey, Object key, Object objParams);

    /**
     * 保存
     */
    boolean save(Object key, Object value);

    /**
     * 删除
     */
    long delete(Object key);

    /**
     * 更新
     */
    boolean update(Object key, Object value);

    /**
     * 更新值，并更新失效时间
     */
    boolean updateOutTime(Object key, Object value, Long time);

    /**
     * 查询
     */
//    <T> T select(Object key);

    <T> T select(Object key, Class<T> clazz);

    /**
     * 查询
     */
//    <T> List<T> selectList(Object key);

    <T> List<T> selectList(Object key, Class<T> clazz);

    /**
     * 保存，设置保存时间
     */
    boolean save(Object key, Object value, Long time);

    /**
     * 保存，设置到期时间
     */
    boolean save(Object key, Object value, Date outOfDate);

    /**
     * 设置key的保存时间
     */
    boolean expire(Object key, Long time);

    /**
     * 设置key的过期时间
     */
    boolean expire(Object key, Date outOfDate);

    //public RedisTemplate<String,String> getRedisTemplate();

    /**
     * 当 key 不存在时，返回 -2 。 当 key 存在但没有设置剩余生存时间时，返回 -1 。 否则，以秒为单位，返回 key 的剩余生存时间。
     * 注意：在 Redis 2.8 以前，当 key 不存在，或者 key 没有设置剩余生存时间时，命令都返回 -1 。
     */
    Long ttl(Object key);

    /**
     * 将 key 的值设为 value ，当且仅当 key 不存在。
     * 设置成功，返回 1 。
     * 设置失败，返回 0 。
     */
    boolean setNX(Object key, Object value);

    /**
     * 列表right-push
     *
     * @param key
     * @param value
     * @return
     */
    Long rPush(Object key, Object value);

    /**
     * 列表left-push
     *
     * @param key
     * @param value
     * @return
     */
    Long lPush(Object key, Object value);

    /**
     * 获取list
     *
     * @param key
     * @param <T>
     * @return
     */
    <T> List<T> range(Object key, Integer start, Integer end, Class<T> clazz);

    /**
     * 获取list长度
     *
     * @param key
     * @return
     */
    Long size(Object key);

    /**
     * list 左pop
     *
     * @param key
     * @param <T>
     * @return
     */
    <T> T lPop(Object key, Class<T> clazz);

    /**
     * list 左pop
     *
     * @param key
     * @param <T>
     * @return
     */
    <T> T rPop(Object key, Class<T> clazz);


    <T> T index(Object key, Integer index, Class<T> clazz);

    /**
     * 在原值上加上value ，如果key 不存在，默认是0
     *
     * @param key
     * @param value 递增值
     * @param time
     * @return
     */
    public Double AutoIncrementOutTime(Object key, double value, Long time);

    /**
     * 自增，不带时间限制或者之前已经设置了时间限制，只做更新
     *
     * @param key
     * @param value
     * @return
     */
    public Double AutoIncrementOrUpdate(Object key, double value);


}
