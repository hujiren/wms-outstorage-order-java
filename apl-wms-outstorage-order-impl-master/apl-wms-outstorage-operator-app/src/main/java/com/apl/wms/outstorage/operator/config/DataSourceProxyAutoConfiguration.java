package com.apl.wms.outstorage.operator.config;

import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * @author hjr start
 * @date 2020/7/3 - 18:38
 */
@Configuration
public class DataSourceProxyAutoConfiguration {

    /**
     * 数据源属性配置
     * {@link DataSourceProperties}
     */
    private DataSourceProperties dataSourceProperties;

    public DataSourceProxyAutoConfiguration(DataSourceProperties dataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
    }

    /**
     * 配置数据源代理，用于事务回滚
     *
     * @return The default datasource
     * @see DataSourceProxy
     */
    @Primary
    @Bean("dataSource")
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(dataSourceProperties.getUrl());
        dataSource.setUsername(dataSourceProperties.getUsername());
        dataSource.setPassword(dataSourceProperties.getPassword());
        dataSource.setDriverClassName(dataSourceProperties.getDriverClassName());

        //dataSource.setJdbcUrl("jdbc:mysql://192.168.1.185:3307/pgs_wms_order_outstorage?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false");
        //dataSource.setUsername("root");
        //dataSource.setPassword("123456");
        //dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        return new DataSourceProxy(dataSource);
    }

}
