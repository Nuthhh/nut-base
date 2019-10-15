package com.nut.base.redis;

import com.nut.base.redis.impl.RedisTool;
import com.nut.base.vo.Student;

import java.util.List;

/**
 * @Auther: han jianguo
 * @Date: 2019/10/15 15:42
 * @Description:
 **/
public class RedisDemo {

    public static void main(String[] args) {
        RedisTool redisTool = new RedisTool("localhost", 6379);
        // redisTool.set("test","ok");
        Student a = new Student();
        a.setName("a");
        a.setAge(1);
        // redisTool.setBean("a",a);
        Student b = new Student();
        b.setName("b");
        b.setAge(2);
        //  redisTool.setBean("b",b);

        List<Student> students = redisTool.mgetBean(Student.class, "c", "d");
        students.forEach(System.out::println);

        redisTool.executor(jedis -> jedis.set("test", a.toString()));
    }
}
