package com.tj.util.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ZkProperties.class)
@ConditionalOnClass(CuratorFramework.class)
@ConditionalOnProperty(prefix = "zk", name = "host", matchIfMissing = false)
public class ZkClientAutoConfiguration {
    private String hosts = "127.0.0.1:2181";
    private Integer sessionTimeout = 3000;
    private String namespace = "com.sac";
    private Integer connectionTimeout = 2000;
    private ZkProperties zkProperties;

    public ZkClientAutoConfiguration(ZkProperties zkProperties) {
        this.zkProperties = zkProperties;
    }

    public String getHosts() {
        return zkProperties.getHost() == null ? hosts : zkProperties.getHost();
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    public Integer getSessionTimeout() {
        return zkProperties.getSessionTimeout() == null ? sessionTimeout : zkProperties.getSessionTimeout();
    }

    public void setSessionTimeout(Integer sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public String getNamespace() {
        return zkProperties.getNamespace() == null ? namespace : zkProperties.getNamespace();
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Integer getConnectionTimeout() {
        return zkProperties.getConnectionTimeout() == null ? connectionTimeout : connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    @Bean
    public CuratorFramework curatorFramework() {
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
        return builder.connectString(getHosts()).sessionTimeoutMs(getSessionTimeout()).connectionTimeoutMs(getConnectionTimeout()).
                canBeReadOnly(true).namespace(getNamespace()).retryPolicy(new ExponentialBackoffRetry(1000, Integer.MAX_VALUE)).
                defaultData(null).build();

    }
}
