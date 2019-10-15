package com.nut.base.redis.impl;

import com.nut.base.core.common.Constants;
import com.nut.base.core.util.JsonUtil;
import com.nut.base.core.util.StringUtil;
import com.nut.base.core.util.Validator;
import com.nut.base.redis.RedisExecutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;
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

    public <T> int hsetBean(String key, String field, T value) {
        return hset(key, field, JsonUtil.toString(value));
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

    public <T> T hgetBean(String key, String field, Class<T> clz) {
        return JsonUtil.toBean(hget(key, field, ""), clz);
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

    public int hincrBy(String key, String field, int increment) {
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

    public <T> int lpushBean(String key, List<T> values) {
        return lpush(key, toStringArray(values));
    }

    private <T> String[] toStringArray(List<T> values) {
        String[] strings = new String[values.size()];
        for (int i = 0; i < values.size(); i++) {
            strings[i] = JsonUtil.toString(values.get(i));
        }
        return strings;
    }

    public <T> int lpushBean(String key, T... values) {
        return lpush(key, toStringArray(values));
    }

    private <T> String[] toStringArray(T... values) {
        String[] strings = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            strings[i] = JsonUtil.toString(values[i]);
        }
        return strings;
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

    public <T> T lpopBean(String key, Class<T> clz) {
        return JsonUtil.toBean(lpop(key, ""), clz);
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

    public <T> int rpushBean(String key, List<T> values) {
        return rpush(key, toStringArray(values));
    }

    public <T> int rpushBean(String key, T... values) {
        return rpush(key, toStringArray(values));
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

    public <T> T rpopBean(String key, Class<T> clz) {
        return JsonUtil.toBean(rpop(key, ""), clz);
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

    public <T> List<T> lrangeBean(String key, int start, int stop, Class<T> clz) {
        return lrange(key, start, stop).stream().map(s -> JsonUtil.toBean(s, clz)).collect(Collectors.toList());
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

    public <T> int saddBean(String key, List<T> members) {
        return sadd(key, toStringArray(members));
    }

    public <T> int saddBean(String key, T... members) {
        return sadd(key, toStringArray(members));
    }

    public int sadd(String key, String... members) {
        if (StringUtil.isNotEmpty(key) && Validator.isNotNull(members)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.sadd(key, members);
                return Constants.SUCCESS;
            } catch (Exception e) {
                log.error("redis添加set错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public <T> T spopBean(String key, Class<T> clz) {
        return JsonUtil.toBean(spop(key, ""), clz);
    }

    public String spop(String key, String defaultStr) {
        if (StringUtil.isNotEmpty(key)) {
            try (Jedis jedis = jedisPool.getResource()) {
                return StringUtil.getNotEmptyStr(jedis.spop(key), defaultStr);
            } catch (Exception e) {
                log.error("redis获取set错误：" + e);
                return defaultStr;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return defaultStr;
        }
    }

    public <T> Set<T> smembersBean(String key, Class<T> clz) {
        return smembers(key).stream().map(s -> JsonUtil.toBean(s, clz)).collect(Collectors.toSet());
    }

    public Set<String> smembers(String key) {
        if (StringUtil.isNotEmpty(key)) {
            try (Jedis jedis = jedisPool.getResource()) {
                return jedis.smembers(key);
            } catch (Exception e) {
                log.error("redis批量获取set错误：" + e);
                return new HashSet<>(0);
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return new HashSet<>(0);
        }
    }

    public int srem(String key, String... members) {
        if (StringUtil.isNotEmpty(key) && Validator.isNotNull(members)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.srem(key, members);
                return Constants.SUCCESS;
            } catch (Exception e) {
                log.error("redis删除set错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public int scard(String key) {
        if (StringUtil.isNotEmpty(key)) {
            try (Jedis jedis = jedisPool.getResource()) {
                return Math.toIntExact(jedis.scard(key));
            } catch (Exception e) {
                log.error("redis获取set长度错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }


    public boolean sismember(String key, String member) {
        if (StringUtil.isNotEmpty(key) && StringUtil.isNotEmpty(member)) {
            try (Jedis jedis = jedisPool.getResource()) {
                return jedis.sismember(key, member);
            } catch (Exception e) {
                log.error("redis判断set数据是否存在错误：" + e);
                return false;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return false;
        }
    }

    public int smove(String source, String destination, String member) {
        if (StringUtil.isNotEmpty(source) && StringUtil.isNotEmpty(destination) && StringUtil.isNotEmpty(member)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.smove(source, destination, member);
                return Constants.SUCCESS;
            } catch (Exception e) {
                log.error("redis判断set数据是否存在错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public Set<String> sdiff(String... keys) {
        if (Validator.isNotNull(keys)) {
            try (Jedis jedis = jedisPool.getResource()) {
                return jedis.sdiff(keys);
            } catch (Exception e) {
                log.error("redis获取set差集错误：" + e);
                return new HashSet<>(0);
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return new HashSet<>(0);
        }
    }

    public int sdiffstore(String destination, String... keys) {
        if (StringUtil.isNotEmpty(destination) && Validator.isNotNull(keys)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.sdiffstore(destination, keys);
                return Constants.SUCCESS;
            } catch (Exception e) {
                log.error("redis获取set差集错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public Set<String> sinter(String... keys) {
        if (Validator.isNotNull(keys)) {
            try (Jedis jedis = jedisPool.getResource()) {
                return jedis.sinter(keys);
            } catch (Exception e) {
                log.error("redis获取set交集错误：" + e);
                return new HashSet<>(0);
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return new HashSet<>(0);
        }
    }

    public int sinterstore(String destination, String... keys) {
        if (StringUtil.isNotEmpty(destination) && Validator.isNotNull(keys)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.sinterstore(destination, keys);
                return Constants.SUCCESS;
            } catch (Exception e) {
                log.error("redis获取set交集错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public Set<String> sunion(String... keys) {
        if (Validator.isNotNull(keys)) {
            try (Jedis jedis = jedisPool.getResource()) {
                return jedis.sunion(keys);
            } catch (Exception e) {
                log.error("redis获取set并集错误：" + e);
                return new HashSet<>(0);
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return new HashSet<>(0);
        }
    }

    public int sunionstore(String destination, String... keys) {
        if (StringUtil.isNotEmpty(destination) && Validator.isNotNull(keys)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.sunionstore(destination, keys);
                return Constants.SUCCESS;
            } catch (Exception e) {
                log.error("redis获取set并集错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public int zadd(String key, String member, double score) {
        Map<String, Double> map = new HashMap<>();
        map.put(member, score);
        return zadd(key, map);
    }

    public int zadd(String key, Map<String, Double> memberScoreMap) {
        if (StringUtil.isNotEmpty(key) && Validator.isNotNull(memberScoreMap)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.zadd(key, memberScoreMap);
                return Constants.SUCCESS;
            } catch (Exception e) {
                log.error("redis添加zset错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public int zcard(String key) {
        if (StringUtil.isNotEmpty(key)) {
            try (Jedis jedis = jedisPool.getResource()) {
                return Math.toIntExact(jedis.zcard(key));
            } catch (Exception e) {
                log.error("redis获取zset长度错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public int zcount(String key, double min, double max) {
        if (StringUtil.isNotEmpty(key)) {
            try (Jedis jedis = jedisPool.getResource()) {
                return Math.toIntExact(jedis.zcount(key, min, max));
            } catch (Exception e) {
                log.error("redis获取zset指定分数区间元素个数错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public int zincrby(String key, String member, double increment) {
        if (StringUtil.isNotEmpty(key) && StringUtil.isNotEmpty(member)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.zincrby(key, increment, member);
                return Constants.SUCCESS;
            } catch (Exception e) {
                log.error("redis获取zset指定分数区间元素个数错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public int zinterstore(String destination, String... keys) {
        if (StringUtil.isNotEmpty(destination) && Validator.isNotNull(keys)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.zinterstore(destination, keys);
                return Constants.SUCCESS;
            } catch (Exception e) {
                log.error("redis获取zset交集错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public int zunionstore(String destination, String... keys) {
        if (StringUtil.isNotEmpty(destination) && Validator.isNotNull(keys)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.zunionstore(destination, keys);
                return Constants.SUCCESS;
            } catch (Exception e) {
                log.error("redis获取zset并集错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public Set<String> zrangebyscore(String key, double min, double max) {
        if (StringUtil.isNotEmpty(key)) {
            try (Jedis jedis = jedisPool.getResource()) {
                return jedis.zrangeByScore(key, min, max);
            } catch (Exception e) {
                log.error("redis按分数获取zset信息错误：" + e);
                return new HashSet<>(0);
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return new HashSet<>(0);
        }
    }

    public int zrem(String key, String... members) {
        if (StringUtil.isNotEmpty(key) && Validator.isNotNull(members)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.zrem(key, members);
                return Constants.SUCCESS;
            } catch (Exception e) {
                log.error("redis删除zset错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public int zremrangebyscore(String key, double min, double max) {
        if (StringUtil.isNotEmpty(key)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.zremrangeByScore(key, min, max);
                return Constants.SUCCESS;
            } catch (Exception e) {
                log.error("redis删除zset错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public Set<String> zrevrangebyscore(String key, double min, double max) {
        if (StringUtil.isNotEmpty(key)) {
            try (Jedis jedis = jedisPool.getResource()) {
                return jedis.zrevrangeByScore(key, max, min);
            } catch (Exception e) {
                log.error("redis按分数获取zset错误：" + e);
                return new HashSet<>(0);
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return new HashSet<>(0);
        }
    }

    public long zrevrank(String key, String member) {
        if (StringUtil.isNotEmpty(key) && StringUtil.isNotEmpty(member)) {
            try (Jedis jedis = jedisPool.getResource()) {
                return jedis.zrevrank(key, member);
            } catch (Exception e) {
                log.error("redis获取zset指定成员排名错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public double zscore(String key, String member) {
        if (StringUtil.isNotEmpty(key) && StringUtil.isNotEmpty(member)) {
            try (Jedis jedis = jedisPool.getResource()) {
                return jedis.zscore(key, member);
            } catch (Exception e) {
                log.error("redis获取zset指定成员分数错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public int del(String... keys) {
        if (Validator.isNotNull(keys)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.del(keys);
                return Constants.SUCCESS;
            } catch (Exception e) {
                log.error("redis删除错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public boolean exists(String key) {
        if (StringUtil.isNotEmpty(key)) {
            try (Jedis jedis = jedisPool.getResource()) {
                return jedis.exists(key);
            } catch (Exception e) {
                log.error("redis判断某键是否存在错误：" + e);
                return false;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return false;
        }
    }

    public int expire(String key, int seconds) {
        if (StringUtil.isNotEmpty(key)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.expire(key, seconds);
                return Constants.SUCCESS;
            } catch (Exception e) {
                log.error("redis设置有效期错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public int expire(String key, long unixTime) {
        if (StringUtil.isNotEmpty(key)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.expireAt(key, unixTime);
                return Constants.SUCCESS;
            } catch (Exception e) {
                log.error("redis设置有效期错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public int persist(String key) {
        if (StringUtil.isNotEmpty(key)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.persist(key);
                return Constants.SUCCESS;
            } catch (Exception e) {
                log.error("redis去除有效期错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public long ttl(String key) {
        if (StringUtil.isNotEmpty(key)) {
            try (Jedis jedis = jedisPool.getResource()) {
                return jedis.ttl(key);
            } catch (Exception e) {
                log.error("redis获取有效期错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public int rename(String key, String newKey) {
        if (StringUtil.isNotEmpty(key) && StringUtil.isNotEmpty(newKey)) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.rename(key, newKey);
                return Constants.SUCCESS;
            } catch (Exception e) {
                log.error("redis获取有效期错误：" + e);
                return Constants.EXCEPTION;
            }
        } else {
            log.error("调用redis缓存方法的参数不能为空");
            return Constants.DATA_EMPTY;
        }
    }

    public boolean isConnection() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.isConnected() && "PONG".equals(jedis.ping());
        } catch (Exception e) {
            log.error("redis验证是否连接错误：" + e);
            return false;
        }
    }

    public <T> T executor(RedisExecutor<T> executor) {
        try (Jedis jedis = jedisPool.getResource()) {
            return executor.run(jedis);
        } catch (Exception e) {
            log.error("redis执行自定义执行器错误：" + e);
        }
        return null;
    }

}
