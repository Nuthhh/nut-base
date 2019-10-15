package com.nut.base.vo;

/**
 * @Auther: han jianguo
 * @Date: 2019/10/14 17:57
 * @Description:
 **/
public class Student {

    private String name;
    private Integer age;

    @Override
    public String toString() {
        return "com.nut.base.vo.Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
