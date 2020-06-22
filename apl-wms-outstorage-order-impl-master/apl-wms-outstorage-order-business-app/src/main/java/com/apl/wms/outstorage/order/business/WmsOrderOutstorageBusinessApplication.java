package com.apl.wms.outstorage.order.business;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication(scanBasePackages = {"com.apl.wms.outstorage.order.*", "com.apl.wms.warehouse.lib.*", "com.apl.lib", "com.apl.lib.handler"}, exclude = {DataSourceAutoConfiguration.class})
@MapperScan(basePackages = {"com.apl.wms.outstorage.order.mapper"})
@EnableFeignClients(basePackages = {"com.apl.sys.lib.feign", "com.apl.wms.warehouse.lib.feign"})
@EnableDiscoveryClient
@EnableSwagger2
public class WmsOrderOutstorageBusinessApplication {

    public static void main(String[] args) {

        SpringApplication.run(WmsOrderOutstorageBusinessApplication.class , args);
    }
}
