package com.apl.wms.outstorage.operator;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(
        scanBasePackages = {"com.apl.wms.outstorage.operator.*" ,
                "com.apl.wms.outstorage.order.*" ,
                "com.apl.wms.warehouse.lib.*",
                "com.apl.db.datasource",
                "com.apl.amqp",
                "com.apl.lib",
                "com.apl.lib.handler"}, exclude = {DataSourceAutoConfiguration.class})
@EnableFeignClients(basePackages = {"com.apl.wms.outstorage.order.lib.feign", "com.apl.wms.warehouse.lib.feign", "com.apl.sys.lib.feign"})
@MapperScan({"com.apl.wms.outstorage.operator.dao" , "com.apl.wms.outstorage.order.dao"})
@EnableDiscoveryClient
@EnableSwagger2
public class WmsOutStorageOperatorApplication {
    public static void main(String[] args) {

        //com.apl.db.datasource.DataSourceConfig
        //com.apl.lib.config.RedisConfig

        SpringApplication.run(WmsOutStorageOperatorApplication.class , args);
    }


}
