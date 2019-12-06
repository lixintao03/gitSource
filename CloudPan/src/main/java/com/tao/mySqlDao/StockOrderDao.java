package com.tao.mySqlDao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.tao.entity.StockOrder;
@Mapper
public interface StockOrderDao {
    /**
     * For table: stock_order
     *
     * @author Mybatis Map Generator
     * @since 2019-11-25 18:36:53
     *
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * For table: stock_order
     *
     * @author Mybatis Map Generator
     * @since 2019-11-25 18:36:53
     *
     */
    int insert(StockOrder record);

    /**
     * For table: stock_order
     *
     * @author Mybatis Map Generator
     * @since 2019-11-25 18:36:53
     *
     */
    int insertSelective(StockOrder record);

    /**
     * For table: stock_order
     *
     * @author Mybatis Map Generator
     * @since 2019-11-25 18:36:53
     *
     */
    StockOrder selectByPrimaryKey(Integer id);

    /**
     * For table: stock_order
     *
     * @author Mybatis Map Generator
     * @since 2019-11-25 18:36:53
     *
     */
    int updateByPrimaryKeySelective(StockOrder record);

    /**
     * For table: stock_order
     *
     * @author Mybatis Map Generator
     * @since 2019-11-25 18:36:53
     *
     */
    int updateByPrimaryKey(StockOrder record);
    
    List<StockOrder> getOrderByUserIdSid(Map<String, Object> data);
}