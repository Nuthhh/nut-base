package com.nut.base.tool.rabbitmq.impl;

import com.nut.base.tool.rabbitmq.IRabbitMQTool;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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


    @Override
    public boolean isConnection() {
        return false;
    }
}
