package com.tao.entity;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * @auther G.Fukang
 * @date 6/7 10:36
 */
@Getter
@Setter
public class Stock implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 4834930025259634405L;

	private Integer id;//库存Id

    private String name;//库存商品名称

    private Integer count;//存量

    private Integer sale;//销量

    private Integer version;//版本号
}
