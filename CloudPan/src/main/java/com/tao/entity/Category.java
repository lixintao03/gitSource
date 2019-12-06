package com.tao.entity;

import java.io.Serializable;
import java.util.Date;

public class Category implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8109199634232098678L;
	private String categoryName;//目录名称
	private String userId;//所属用户
	private Date createTime;//创建时间
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	

}
