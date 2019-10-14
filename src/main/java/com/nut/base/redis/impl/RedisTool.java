package com.nut.base.redis.impl;

import com.nut.base.core.common.Constants;
import com.nut.base.core.util.JsonUtil;
import com.nut.base.core.util.StringUtil;
import com.nut.base.core.util.Validator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: han jianguo
 * @Date: 2019/10/12 11:47
 * @Description:
 **/
public class RedisTool {

    private static Log log = LogFactory.getLog(RedisTool.class);
    private JedisPool jedisPool;

    public RedisTool(String host, int port) {
        this.jedisPool = new JedisPool(host, port);
    }

    public RedisTool(JedisPoolConfig jedisPoolConfig, String host, int port) {
        this.jedisPool = new JedisPool(jedisPoolConfig, host, port);
    }

    public RedisTool(JedisPoolConfig jedisPoolConfig, String host, int port, int timeout) {
        this.jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout);
    }

    public RedisTool(JedisPoolConfig jedisPoolConfig, String host, int port, int timeout, String password) {
        this.jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password);
    }

    public RedisTool(JedisPoolConfig jedisPoolConfig, String host, int port, int timeout, String password, int database) {
        this.jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password, database);
    }

    public RedisTool(JedisPoolConfig jedisPoolConfig, String host, int port, int timeout, String password, int database, String clientName) {
        this.jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password, database, clientName);
    }

    public <T> int setString(String key, T object) {
        return setString(key, JsonUtil.toString(object));
    }

    public int setString(String key, String value) {
        if (StringUtil.isNotEmpty(key) && StringUtil.isNotEmpty(value)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.set(key, value);
            } catch (Exception e) {
                log.error("redis添加string类型错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("添加缓存的参数不能为空");
            return Constants.DATA_EMPTY;
        }
        return Constants.SUCCESS;
    }

    public <T> int setString(String key, T value, int expire) {
        return setString(key, JsonUtil.toString(value), expire);
    }

    public int setString(String key, String value, int expire) {
        if (StringUtil.isNotEmpty(key) && StringUtil.isNotEmpty(value)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.setex(key, expire, value);
            } catch (Exception e) {
                log.error("redis添加string类型错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
        return Constants.SUCCESS;
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public int appendString(String key, String value) {
        if (StringUtil.isNotEmpty(key) && StringUtil.isNotEmpty(value)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.append(key, value);
            } catch (Exception e) {
                log.error("redis添加string类型错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
        return Constants.SUCCESS;
    }

    public String getString(String key, String defaultStr) {
        if (StringUtil.isNotEmpty(key)) {
            try (Jedis jedis = jedisPool.getResource()) {
                String var = jedis.get(key);
                var = StringUtil.getNotEmptyStr(var, defaultStr);
                return var;
            } catch (Exception e) {
                log.error("redis获取string类型数据错误：" + e);
                return defaultStr;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return defaultStr;
        }
    }

    public List<String> mgetString(String... keyList) {
        if (Validator.isNotNull(keyList)) {
            try (Jedis jedis = jedisPool.getResource()) {
                return jedis.mget(keyList);
            } catch (Exception e) {
                log.error("redis批量获取string类型数据错误：" + e);
                return new ArrayList<>(keyList.length);
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return new ArrayList<>(keyList.length);
        }
    }

    public int incr(String key) {
        if (StringUtil.isNotEmpty(key)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.incr(key);
                return Constants.SUCCESS;
            } catch (Exception e) {
                log.error("redis自增错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public int incr(String key, int increment) {
        if (StringUtil.isNotEmpty(key)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.incrBy(key, Long.valueOf(increment));
                return Constants.SUCCESS;
            } catch (Exception e) {
                log.error("redis自增错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public int decr(String key) {
        if (StringUtil.isNotEmpty(key)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.decr(key);
                return Constants.SUCCESS;
            } catch (Exception e) {
                log.error("redis自减错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public int decr(String key, int decrement) {
        if (StringUtil.isNotEmpty(key)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.decrBy(key, Long.valueOf(decrement));
                return Constants.SUCCESS;
            } catch (Exception e) {
                log.error("redis自减错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public int hset(String key, String field, String value) {
        if (StringUtil.isNotEmpty(key) && StringUtil.isNotEmpty(field) && StringUtil.isNotEmpty(value)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.hset(key, field, value);
                return Constants.SUCCESS;
            } catch (Exception e) {
                log.error("redis添加hash错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }


}
