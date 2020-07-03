package com.apl.wms.outstorage.operator;

import com.apl.lib.config.MyBatisPlusConfig;
import com.apl.lib.datasource.DynamicDataSource;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = {"com.apl.wms.outstorage.operator.*" ,  "com.apl.wms.outstorage.order.*" , "com.apl.wms.warehouse.lib.*", "com.apl.lib", "com.apl.lib.handler"}, exclude = {DataSourceAutoConfiguration.class})
@EnableFeignClients(basePackages = {"com.apl.wms.outstorage.order.lib.feign", "com.apl.wms.warehouse.lib.feign", "com.apl.sys.lib.feign"})
@MapperScan({"com.apl.wms.outstorage.operator.mapper" , "com.apl.wms.outstorage.order.mapper"})
@EnableDiscoveryClient
@EnableSwagger2
public class WmsOutStorageOperatorApplication {
    public static void main(String[] args) {

        //com.apl.lib.datasource.DataSourceConfig
        //com.apl.lib.datasource.DynamicDataSource

        SpringApplication.run(WmsOutStorageOperatorApplication.class , args);
    }


}
