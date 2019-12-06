package com.tao.entity;

import java.io.Serializable;
import java.util.Date;

public class FileInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8634580164761259912L;
	
	private String fileName;
	private String fileId;
	private String filePath;
	private Date createTime;
	private String category;
	private String userId;//用户Id
	private String description;
	
	
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	@Override
	public String toString() {
		return "FileInfo [fileName=" + fileName + ", fileId=" + fileId + ", filePath=" + filePath + ", createTime="
				+ createTime + "]";
	}
	
	
}
