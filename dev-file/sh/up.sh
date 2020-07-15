
#项目绝对路径
MYPATH="/Volumes/data/java/apl-wms/apl-wms-outstorage-order-java"

scp  $MYPATH/apl-wms-outstorage-order-impl-master/apl-wms-outstorage-order-service-impl/src/main/resources/mapper/*.xml  root@192.168.1.185:/usr/local/java/basic/resource/mapper/wms-outstorage-order

scp  $MYPATH/apl-wms-outstorage-order-impl-master/apl-wms-outstorage-operator-service-impl/src/main/resources/mapper/*.xml  root@192.168.1.185:/usr/local/java/basic/resource/mapper/wms-outstorage-operator


# 商家出库订单
scp  $MYPATH/apl-wms-outstorage-order-impl-master/apl-wms-outstorage-order-business-app/target/apl-wms-outstorage-order-business-app-1.0.0.jar  root@192.168.1.185:/usr/local/java/basic/

# 买家出库订单
scp  $MYPATH/apl-wms-outstorage-order-impl-master/apl-wms-outstorage-order-buyer-app/target/apl-wms-outstorage-order-buyer-app-1.0.0.jar root@192.168.1.185:/usr/local/java/basic/


# 商家出库操作
scp  $MYPATH/apl-wms-outstorage-order-impl-master/apl-wms-outstorage-operator-app/target/apl-wms-outstorage-operator-app-1.0.0.jar root@192.168.1.185:/usr/local/java/basic/
