package com.apl.wms.outstorage.operator.dao;
import com.apl.wms.outstorage.operator.pojo.vo.PackingInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author hjr start
 * @date 2020/7/28 - 14:58
 */
@Repository
@Mapper
public interface PackMapper extends BaseMapper<PackingInfo> {

}
