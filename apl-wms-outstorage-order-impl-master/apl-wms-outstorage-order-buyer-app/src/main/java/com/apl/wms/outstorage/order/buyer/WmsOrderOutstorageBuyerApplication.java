package com.apl.wms.outstorage.order.buyer;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication(
        scanBasePackages = {
                "com.apl.lib", //APL基本工具类
                "com.apl.cache", // redis代理
                "com.apl.tenant", //多租户
                //"com.apl.abatis", // sqlSession封装
                "com.apl.db.adb", // adb数据库操作助手
                //"com.apl.db.dynamicdb", //动态数据源
                "com.apl.shardingjdbc", // 分库
                "com.apl.amqp", //消息队列代理

                "com.apl.wms.outstorage.order",
                "com.apl.wms.warehouse.lib"
        }, exclude = {DruidDataSourceAutoConfigure.class})
@MapperScan(basePackages = {"com.apl.wms.outstorage.order.mapper"}, sqlSessionFactoryRef = "sqlSessionFactoryForShardingjdbc")
@EnableFeignClients(basePackages = {"com.apl.sys.lib.feign", "com.apl.wms.warehouse.lib.feign"})
@EnableDiscoveryClient
@EnableSwagger2
public class WmsOrderOutstorageBuyerApplication {

    //com.apl.db.datasource.DataSourceConfig

    public static void main(String[] args) {
        SpringApplication.run(WmsOrderOutstorageBuyerApplication.class , args);
    }
}
