package com.tao.mySqlDao;

import org.apache.ibatis.annotations.Mapper;

import com.tao.entity.Stock;
@Mapper
public interface StockDao {
    /**
     * For table: stock
     *
     * @author Mybatis Map Generator
     * @since 2019-11-25 18:31:55
     *
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * For table: stock
     *
     * @author Mybatis Map Generator
     * @since 2019-11-25 18:31:55
     *
     */
    int insert(Stock record);

    /**
     * For table: stock
     *
     * @author Mybatis Map Generator
     * @since 2019-11-25 18:31:55
     *
     */
    int insertSelective(Stock record);

    /**
     * For table: stock
     *
     * @author Mybatis Map Generator
     * @since 2019-11-25 18:31:55
     *
     */
    Stock selectByPrimaryKey(Integer id);

    /**
     * For table: stock
     *
     * @author Mybatis Map Generator
     * @since 2019-11-25 18:31:55
     *
     */
    int updateByPrimaryKeySelective(Stock record);

    /**
     * For table: stock
     *
     * @author Mybatis Map Generator
     * @since 2019-11-25 18:31:55
     *
     */
    int updateByPrimaryKey(Stock record);
    
    int updateByOptimiticLock(Stock record);
}