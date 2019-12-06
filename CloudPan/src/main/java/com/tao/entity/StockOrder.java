package com.tao.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @auther G.Fukang
 * @date 6/7 10:37
 */
@Getter
@Setter
public class StockOrder implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -5315467549081662180L;

	private Integer id;//订单id

    private Integer sid;//库存商品id
    
    private String userId;//用户id

    private String name;//订单名称（或商品名称）

    private Date createTime;//创建时间

	@Override
	public String toString() {
		return "StockOrder [id=" + id + ", sid=" + sid + ", name=" + name + ", createTime=" + createTime + "]";
	}
    
}
