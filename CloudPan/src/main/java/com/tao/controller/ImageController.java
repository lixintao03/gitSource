package com.tao.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.tao.entity.FileInfo;
import com.tao.entity.User;
import com.tao.mongoDao.FileInfoMongoDao;
import com.tao.mq.Producer;

@Controller
public class ImageController {
	Logger logger = LoggerFactory.getLogger(ImageController.class);
	private static final String MESSAGE = "message";
	private static final String OK = "ok";
	@Autowired
	private GridFsTemplate gridFsTemplate;
	@Autowired
	private FileInfoMongoDao mongoDao;
	@Autowired
	private MongoDbFactory mongoDbFactory;
	@Autowired
	private Producer producer;

	@RequestMapping(value = "/images", produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public byte[] getImage(HttpServletResponse resp) throws IOException {
		FileInputStream input = null;
		byte[] bytes = null;
		File file = new File("C:\\Users\\chen\\Pictures\\img90.jpg");
		try {
			input = new FileInputStream(file);
			bytes = new byte[input.available()];
			input.read(bytes, 0, input.available());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			input.close();
		}
		return bytes;
	}

	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile[] files,HttpServletRequest request) throws Exception {
		Map<String, Object> map = new HashMap<>();
		List<String> fileIds = new ArrayList<>();
		List<String> fileNames = new ArrayList<>();
		String category = request.getParameter("category");
		logger.info("获取category:"+category);
		
		User user = (User) request.getSession().getAttribute("user");
		if(user == null) {
			logger.info("从session中未获取到user对象");
			return null;
		}
		logger.info("获取到用户登录信息user:"+user);
		int count = 0;
		for(MultipartFile multiportFile : files) {
			String fileName = multiportFile.getOriginalFilename();
			InputStream in = multiportFile.getInputStream();
			// 获取文件类型
			String type = multiportFile.getContentType();
			ObjectId objectId = gridFsTemplate.store(in, fileName, type);
			if (objectId == null) {
				map.put(OK, false);
				map.put(MESSAGE, "保存文件表失败！");
				return map;
			}
			String fileId = objectId.toString();
			FileInfo file = new FileInfo();
			file.setFileId(fileId);
			file.setFileName(fileName);
			file.setFilePath("/data");
			file.setCategory(category);
			file.setCreateTime(new Date());
			file.setUserId(user.getName());
			int i = mongoDao.saveOne(file, "fileInfo");
			if(i < 1) {
				map.put(OK, false);
				map.put(MESSAGE, "第"+(count+1)+"个文件，保存文件记录表失败！");
				return map;
			}
			fileIds.add(fileId);
			fileNames.add(fileName);
			
		}
		map.put(OK, true);
		map.put(MESSAGE, "上载成功！");
		map.put("fileIds", fileIds);
		map.put("fileNames", fileNames);
		return map;
	}

	@RequestMapping(value = "/queryOneFile", method = RequestMethod.GET)
	public void queryAllFile(String fileId, HttpServletResponse response) throws Exception {
		Query query = Query.query(Criteria.where("fileId").is(fileId));
		// 获取文件类型
		List<FileInfo> fileInfos = mongoDao.findByQuery(query, FileInfo.class, "fileInfo");
		if (fileInfos != null) {
			FileInfo fileInfo = fileInfos.get(0);
			Query gridFsQuery = Query.query(Criteria.where("_id").is(fileInfo.getFileId()));
			GridFSFile file = gridFsTemplate.findOne(gridFsQuery);
			if (file != null) {
				GridFSDownloadStream gridFSDownloadStream = GridFSBuckets.create(mongoDbFactory.getDb())
						.openDownloadStream(file.getObjectId());
				GridFsResource resource = new GridFsResource(file, gridFSDownloadStream);
				IOUtils.copy(resource.getInputStream(), response.getOutputStream());
			}
		}
	}

	/**
	 * 获取文件内容
	 * 
	 * @param fileName
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getImages", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> queryFileNames(HttpServletRequest request,String category, HttpServletResponse response) throws Exception {
		Query query = Query.query(Criteria.where("category").is(category));
		// 获取文件类型
		List<FileInfo> fileInfos = mongoDao.findByQuery(query, FileInfo.class, "fileInfo");
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("fileNames", fileInfos);
		return retMap;
	}

	/**
	   *     删除文件
	 * 
	 * @param fileId
	 * @return
	 */
	@RequestMapping(value = "/removeImages", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> removeFJile(String fileId) {
		Map<String, Object> map = new HashMap<>();
		Query query = Query.query(Criteria.where("fileId").is(fileId));
		List<FileInfo> fileInfos = mongoDao.findByQuery(query, FileInfo.class, "fileInfo");
		logger.info("文件删除--------->查询到文件对象："+fileInfos);
		if (!CollectionUtils.isEmpty(fileInfos)) {
			try {
				// 先删除文件记录表
				int i = mongoDao.deleteOne(query, "fileInfo");
				logger.info("删除文件记录表结束，删除结果为："+i);
				if (i < 1) {
					map.put(OK, false);
					map.put(MESSAGE, "文件记录表删除异常！");
					return map;
				}
				gridFsTemplate.delete(query);
			} catch (Exception e) {
				e.printStackTrace();
				map.put(OK, false);
				map.put(MESSAGE, "文件删除异常");
				return map;
			}
		} else {
			map.put(OK, false);
			map.put(MESSAGE, "文件不存在");
			return map;
		}
		map.put(OK, true);
		map.put(MESSAGE, "文件删除成功");
		return map;
	}
	
	/**
	 * 获取前N个内容
	 * 获取文件表中的前N个图片信息
	 * 按某位用户的所有画册的所有图片的创建时间倒序来获取count个
	 */
	@RequestMapping(value = "/getTopImages", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getTopCategory(HttpServletRequest request) {
		String countStr = request.getParameter("count");
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if(user == null) {
			logger.info("从session中未获取到user对象");
			return null;
		}
		logger.info("获取到用户登录信息user:"+user);
		String userId = user.getName();
		Integer count = Integer.valueOf(countStr);
		Query query = Query.query(Criteria.where("userId").is(userId));
		query = query.limit(count);
		query = query.with(Sort.by(Sort.Order.desc("createTime")));
		// 获取文件类型
		List<FileInfo> fileInfos = mongoDao.findByQuery(query, FileInfo.class, "fileInfo");
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("fileNames", fileInfos);
		return retMap;
	}
	/**
	 * 获取第top N个图片
	 * @param request
	 * @return
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	@RequestMapping(value = "/getTopNImage", method = RequestMethod.GET)
	public void getTopNImage(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String countStr = request.getParameter("topN");
		//String userId = request.getParameter("userId");
		String userId = "1001";
		Integer count = Integer.valueOf(countStr);
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if(user == null) {
			logger.info("从session中未获取到user对象");
			return;
		}
		logger.info("获取到用户登录信息user:"+user);
		userId = user.getName();
		Query query = Query.query(Criteria.where("userId").is(userId));
		query = query.with(Sort.by(Sort.Order.desc("createTime")));
		query = query.limit(count);
		// 获取文件类型
		List<FileInfo> fileInfos = mongoDao.findByQuery(query, FileInfo.class, "fileInfo");
		if (fileInfos != null && !fileInfos.isEmpty()) {
			FileInfo fileInfo = fileInfos.get(count-1);
			Query gridFsQuery = Query.query(Criteria.where("_id").is(fileInfo.getFileId()));
			GridFSFile file = gridFsTemplate.findOne(gridFsQuery);
			if (file != null) {
				GridFSDownloadStream gridFSDownloadStream = GridFSBuckets.create(mongoDbFactory.getDb())
						.openDownloadStream(file.getObjectId());
				GridFsResource resource = new GridFsResource(file, gridFSDownloadStream);
				IOUtils.copy(resource.getInputStream(), response.getOutputStream());
			}
		}
	}
	
	/**获取每个category的前N个
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/getTopNCategoryImage", method = RequestMethod.GET)
	public void getTopNCategoryImage(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String countStr = request.getParameter("topN");
		String category = request.getParameter("category");
		logger.info("topN="+countStr+"category="+category);
		//String userId = request.getParameter("userId");
		String userId = "1001";
		Integer count = Integer.valueOf(countStr);
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if(user == null) {
			logger.info("从session中未获取到user对象");
			return;
		}
		logger.info("获取到用户登录信息user:"+user);
		userId = user.getName();
		Query query = Query.query(Criteria.where("userId").is(userId).and("category").is(category));
		query = query.with(Sort.by(Sort.Order.desc("createTime")));
		query = query.limit(count);
		// 获取文件类型
		List<FileInfo> fileInfos = mongoDao.findByQuery(query, FileInfo.class, "fileInfo");
		if (fileInfos != null && !fileInfos.isEmpty()) {
			FileInfo fileInfo = fileInfos.get(count-1);
			Query gridFsQuery = Query.query(Criteria.where("_id").is(fileInfo.getFileId()));
			GridFSFile file = gridFsTemplate.findOne(gridFsQuery);
			if (file != null) {
				GridFSDownloadStream gridFSDownloadStream = GridFSBuckets.create(mongoDbFactory.getDb())
						.openDownloadStream(file.getObjectId());
				GridFsResource resource = new GridFsResource(file, gridFSDownloadStream);
				IOUtils.copy(resource.getInputStream(), response.getOutputStream());
			}
		}
	}
	private User getUser(HttpServletRequest request) {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if(user == null) {
			logger.info("从session中未获取到user对象");
			return null;
		}
		logger.info("获取到用户登录信息user:"+user);
		return user;
	}
}
