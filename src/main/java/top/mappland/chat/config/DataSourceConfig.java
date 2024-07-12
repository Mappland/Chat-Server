package top.mappland.chat.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    @Primary
    @Bean(name = "chatUserDataSourceProperties")
    @ConfigurationProperties("spring.datasource.chat-user")
    public DataSourceProperties chatUserDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "chatUserDataSource")
    public DataSource chatUserDataSource(@Qualifier("chatUserDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "chatGroupDataSourceProperties")
    @ConfigurationProperties("spring.datasource.chat-group")
    public DataSourceProperties chatGroupDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "chatGroupDataSource")
    public DataSource chatGroupDataSource(@Qualifier("chatGroupDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean
    public DataSource routingDataSource(@Qualifier("chatUserDataSource") DataSource chatUserDataSource,
                                        @Qualifier("chatGroupDataSource") DataSource chatGroupDataSource) {
        AbstractRoutingDataSource routingDataSource = new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                return DataSourceContextHolder.getDataSourceKey();
            }
        };
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(DataSourceKey.CHAT_USER, chatUserDataSource);
        dataSourceMap.put(DataSourceKey.CHAT_GROUP, chatGroupDataSource);

        routingDataSource.setDefaultTargetDataSource(chatUserDataSource);
        routingDataSource.setTargetDataSources(dataSourceMap);
        return routingDataSource;
    }

    @Primary
    @Bean
    public PlatformTransactionManager transactionManager(DataSource routingDataSource) {
        return new DataSourceTransactionManager(routingDataSource);
    }
}
