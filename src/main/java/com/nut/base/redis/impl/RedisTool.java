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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public <T> int setBean(String key, T object) {
        return set(key, JsonUtil.toString(object));
    }

    public int set(String key, String value) {
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

    public <T> int setBean(String key, T value, int expire) {
        return set(key, JsonUtil.toString(value), expire);
    }

    public int set(String key, String value, int expire) {
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

    public int append(String key, String value) {
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

    public <T> T getBean(String key, Class<T> clz) {
        return JsonUtil.toBean(get(key), clz);
    }

    public String get(String key) {
        return get(key, "");
    }

    public String get(String key, String defaultStr) {
        if (StringUtil.isNotEmpty(key)) {
            try (Jedis jedis = jedisPool.getResource()) {
                return StringUtil.getNotEmptyStr(jedis.get(key), defaultStr);
            } catch (Exception e) {
                log.error("redis获取string类型数据错误：" + e);
                return defaultStr;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return defaultStr;
        }
    }

    public <T> List<T> mgetBean(Class<T> clz, String... keyList) {
        return mget(keyList).stream().map(string -> JsonUtil.toBean(string, clz)).collect(Collectors.toList());
    }

    public List<String> mget(String... keyList) {
        if (Validator.isNotNull(keyList)) {
            try (Jedis jedis = jedisPool.getResource()) {
                return jedis.mget(keyList);
            } catch (Exception e) {
                log.error("redis批量获取string类型数据错误：" + e);
                return new ArrayList<>(0);
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return new ArrayList<>(0);
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
                log.error("redis添加hash字段错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public String hget(String key, String field, String defaultStr) {
        if (StringUtil.isNotEmpty(key) && StringUtil.isNotEmpty(field)) {
            try (Jedis jedis = jedisPool.getResource()) {
                return StringUtil.getNotEmptyStr(jedis.hget(key, field), defaultStr);
            } catch (Exception e) {
                log.error("redis获取hash字段错误：" + e);
                return defaultStr;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return defaultStr;
        }
    }


    public int hmset(String key, Map<String, String> fieldValueMap) {
        if (StringUtil.isNotEmpty(key) && Validator.isNotNull(fieldValueMap)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.hmset(key, fieldValueMap);
                return Constants.SUCCESS;
            } catch (Exception e) {
                log.error("redis批量添加hash字段错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public List<String> hmget(String key, String... fields) {
        if (StringUtil.isNotEmpty(key) && Validator.isNotNull(fields)) {
            try (Jedis jedis = jedisPool.getResource()) {
                List<String> result = jedis.hmget(key, fields);
                return result;
            } catch (Exception e) {
                log.error("redis批量获取hash字段错误：" + e);
                return new ArrayList<>(0);
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return new ArrayList<>(0);
        }
    }

    public Map<String, String> hgetAll(String key) {
        if (StringUtil.isNotEmpty(key)) {
            try (Jedis jedis = jedisPool.getResource()) {
                Map<String, String> map = jedis.hgetAll(key);
                return map;
            } catch (Exception e) {
                log.error("redis批量获取hash字段错误：" + e);
                return new HashMap<>(0);
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return new HashMap<>(0);
        }
    }

    public boolean hexists(String key, String field) {
        if (StringUtil.isNotEmpty(key) && StringUtil.isNotEmpty(field)) {
            try (Jedis jedis = jedisPool.getResource()) {
                boolean result = jedis.hexists(key, field);
                return result;
            } catch (Exception e) {
                log.error("redis查看hash字段是否存在错误：" + e);
                return false;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return false;
        }
    }

    public int hdel(String key, String... fields) {
        if (StringUtil.isNotEmpty(key) && Validator.isNotNull(fields)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.hdel(key, fields);
                return Constants.SUCCESS;
            } catch (Exception e) {
                log.error("redis删除hash字段错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public int hincrby(String key, String field, int increment) {
        if (StringUtil.isNotEmpty(key) && Validator.isNotNull(field)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.hincrBy(key, field, increment);
                return Constants.SUCCESS;
            } catch (Exception e) {
                log.error("redis hash字段自增错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public int hlen(String key) {
        if (StringUtil.isNotEmpty(key)) {
            try (Jedis jedis = jedisPool.getResource()) {
                return Math.toIntExact(jedis.hlen(key));
            } catch (Exception e) {
                log.error("redis获取hash长度错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public int lpush(String key, String... values) {
        if (StringUtil.isNotEmpty(key) && Validator.isNotNull(values)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.lpush(key, values);
                return Constants.SUCCESS;
            } catch (Exception e) {
                log.error("redis添加list错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public String lpop(String key, String defaultStr) {
        if (StringUtil.isNotEmpty(key)) {
            try (Jedis jedis = jedisPool.getResource()) {
                return StringUtil.getNotEmptyStr(jedis.lpop(key), defaultStr);
            } catch (Exception e) {
                log.error("redis获取list错误：" + e);
                return defaultStr;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return defaultStr;
        }
    }

    public int rpush(String key, String... values) {
        if (StringUtil.isNotEmpty(key) && Validator.isNotNull(values)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.rpush(key, values);
                return Constants.SUCCESS;
            } catch (Exception e) {
                log.error("redis添加list错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public String rpop(String key, String defaultStr) {
        if (StringUtil.isNotEmpty(key)) {
            try (Jedis jedis = jedisPool.getResource()) {
                return StringUtil.getNotEmptyStr(jedis.rpop(key), defaultStr);
            } catch (Exception e) {
                log.error("redis获取list错误：" + e);
                return defaultStr;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return defaultStr;
        }
    }

    public List<String> lrange(String key, int start, int stop) {
        if (StringUtil.isNotEmpty(key)) {
            try (Jedis jedis = jedisPool.getResource()) {
                return jedis.lrange(key, start, stop);
            } catch (Exception e) {
                log.error("redis获取list错误：" + e);
                return new ArrayList<>(0);
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return new ArrayList<>(0);
        }
    }

    public int ltrim(String key, int start, int stop) {
        if (StringUtil.isNotEmpty(key)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.ltrim(key, start, stop);
                return Constants.SUCCESS;
            } catch (Exception e) {
                log.error("redis修剪list错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }


    public int test() {

        try (Jedis jedis = jedisPool.getResource()) {
            //jedis.hexists()


            return Constants.SUCCESS;
        } catch (Exception e) {
            log.error("redis添加hash错误：" + e);
            return Constants.EXCEPTION;
        }

    }


}
