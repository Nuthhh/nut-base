package com.nut.base.tool.redis;

import redis.clients.jedis.Jedis;

/**
 * @Auther: han jianguo
 * @Date: 2019/10/15 11:49
 * @Description:
 **/
@FunctionalInterface
public interface RedisExecutor<T> {
    T run(Jedis jedis);
}
