package com.apl;

import com.apl.wms.order.inner.WmsOrderInnerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {WmsOrderInnerApplication.class})// 指定启动类
public class OrderPutAwayTest {


    //上架流程
    // 1.查找主订单  2. 为每个子订单分配库位  3.同步订单数据  4. 提交上架数据  5 完成上架


    @Test
    public void autoAllotLocalTest() {

        //重置： 上架表： 上架表数据   库位表：将库位表状态重置为空


        //重置所有库位状态 为空 1
        //update storage_local set storage_status = 1;
        //清空库位库存 0
       // DELETE from storage_local_stocks

    }

    public void manualAllotLocalTest(){

        //手动分配库位

        //args : 库位sn  commodityId orderItemId

        // 查找库位信息：
        //          判断库位是否为空  if true : 上架表 绑定库位  锁定库位  返回库位分配成功
        //                              false    :    查找库位状态是否锁定   判断库位是否为该订单--》该商品锁定   if true 返回库位分配成功  else : 库位分配失败


    }

    @Test
    public void autoAsync() {
        //自动同步订单
        //


    }


    public void submitPutAway(){

        //重置： 修改主订单状态：待上架状态   修改子订单：已上架数量 ， 修改上架表： 将上架状态重置为未上架，提交产品数为零
        //       修改主库存： 总库存-对应商品上架数量  修改库位库存： 库位库存-上架表库存  修改库存记录： 删除订单id 对应库存记录

        //args: orderId  putAwayItems
        //  查找子订单 列表
        // 操作子订单 ： 上架的数量 ，
        //主订单： 如果子订单完成， 修改主订单状态 已完成
        // 修改 上架表 以上架数量 ， 状态为已提交，
        //保存库存记录   更新库位库存  更新总库存




    }




}