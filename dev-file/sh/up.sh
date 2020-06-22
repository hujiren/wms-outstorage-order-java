
#项目绝对路径
MYPATH="/Volumes/data/java/apl-wms-service/apl-wms-service-impl"

#scp  $MYPATH/apl-wms-wh-service-impl/src/main/resources/mapper/*.xml  root@192.168.1.185:/usr/local/java/basic/resource/mapper/wms-wh

#scp  $MYPATH/apl-wms-order-service-impl/src/main/resources/mapper/*.xml  root@192.168.1.185:/usr/local/java/basic/resource/mapper/wms-order

# 商家仓库
scp  $MYPATH/apl-wms-warehouse-business-app/target/apl-wms-warehouse-business-app-1.0.0.jar   root@192.168.1.185:/usr/local/java/basic/

# 买家仓库
scp  $MYPATH/apl-wms-warehouse-buyer-app/target/apl-wms-warehouse-buyer-app-1.0.0.jar   root@192.168.1.185:/usr/local/java/basic/


# 商家入库订单
scp  $MYPATH/apl-wms-instorage-order-business-app/target/apl-wms-instorage-order-business-app-1.0.0.jar  root@192.168.1.185:/usr/local/java/basic/

# 买家入库订单
scp  $MYPATH/apl-wms-instorage-order-buyer-app/target/apl-wms-instorage-order-buyer-app-1.0.0.jar root@192.168.1.185:/usr/local/java/basic/


# 商家出库订单
scp  $MYPATH/apl-wms-outstorage-order-business-app/target/apl-wms-outstorage-order-business-app-1.0.0.jar  root@192.168.1.185:/usr/local/java/basic/

# 买家出库订单
scp  $MYPATH/apl-wms-outstorage-order-buyer-app/target/apl-wms-outstorage-order-buyer-app-1.0.0.jar root@192.168.1.185:/usr/local/java/basic/


# 商家入库操作
scp  $MYPATH/apl-wms-instorage-operator-app/target/apl-wms-instorage-operator-app-1.0.0.jar  root@192.168.1.185:/usr/local/java/basic/

# 商家出库操作
scp  $MYPATH/apl-wms-outstorage-operator-app/target/apl-wms-outstorage-operator-app-1.0.0.jar root@192.168.1.185:/usr/local/java/basic/
