package com.nut.base.tool.rabbitmq.impl;

import com.nut.base.tool.rabbitmq.IRabbitMQTool;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

/**
 * @Auther: han jianguo
 * @Date: 2019/10/15 15:07
 * @Description:
 **/
public class RabbitMQTool implements IRabbitMQTool {

    private static Log log = LogFactory.getLog(RabbitMQTool.class);

    private ConnectionFactory connectionFactory;

    public RabbitMQTool(String host, int port, String username, String password) {
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
    }

    public RabbitMQTool(String host,int port,String username,String password,String virtualHost){
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
    }

    @Override
    public boolean isConnection() {
        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
        } catch (Exception e) {
            log.error("RabbitMQ服务错误");
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }


}
