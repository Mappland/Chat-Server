package top.mappland.chat.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

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

    @Primary
    @Bean(name = "chatUserTransactionManager")
    public PlatformTransactionManager chatUserTransactionManager(@Qualifier("chatUserDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "chatGroupTransactionManager")
    public PlatformTransactionManager chatGroupTransactionManager(@Qualifier("chatGroupDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
