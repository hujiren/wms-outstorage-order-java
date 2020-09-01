package com.apl.wms.outstorage.order.business;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication(
        scanBasePackages = {"com.apl.wms.outstorage.order.*",
                "com.apl.wms.warehouse.lib.*",
                "com.apl.lib",
                "com.apl.cache",
                "com.apl.db",
                "com.apl.amqp"},
        exclude = {DataSourceAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
@MapperScan(basePackages = {"com.apl.wms.outstorage.order.dao"})
@EnableFeignClients(basePackages = {"com.apl.sys.lib.feign", "com.apl.wms.warehouse.lib.feign"})
@EnableDiscoveryClient
@EnableSwagger2
public class WmsOrderOutstorageBusinessApplication {

    public static void main(String[] args) {

        //com.apl.db.datasource.DataSourceConfig

        SpringApplication.run(WmsOrderOutstorageBusinessApplication.class , args);
    }
}
