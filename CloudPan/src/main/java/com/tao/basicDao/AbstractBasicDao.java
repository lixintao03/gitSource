package com.tao.basicDao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.DeleteResult;

/**
 * dao层模板
 * @author 李新涛
 *
 * @param <T>
 * @param <PK>
 */
@Repository
public abstract class AbstractBasicDao<T extends Serializable,PK extends Serializable> {
	
	@Autowired
	private MongoTemplate template;
	
	protected abstract String  getPKColumn();
	 

	protected List<Map> find(String collectionName){
		//List<Map> list = template.find(query, Map.class, collectionName);
		List<Map> list = template.findAll(Map.class, collectionName);
		System.out.println(list);
		return list;
	 }
	//按条件查询
	protected List<T> findByQuery(Query query,Class<T> clazz,String collectionName){
		List<T> list = template.find(query, clazz, collectionName);
		System.out.println(list);
		return list;
	}
	//保存一条
	protected int saveOne(T t, String collectionName) {
		int result = 0;
		try {
			template.save(t, collectionName);
			result=1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	//删除一条
	protected int deleteOne(Query query,String collectionName) {
		long result = 0;
		try {
			DeleteResult delResult = template.remove(query, collectionName);
			result = delResult.getDeletedCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (int)result;
	}
	//修改一条
	protected int updateOne(Query query,Update update,String collectionName) {
		int result = 0;
		try {
			template.updateFirst(query, update, collectionName);
			result=1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**分页查询时间倒叙
	 * @param page 当前页码
	 * @param count 一页显示多少内容
	 * @param query 查询条件
	 * @param clazz 返回实体
	 * @param collectionName 查询的集合
	 * @return
	 */
	protected List<T> findByPage(int page,int count,Query query,Class<T> clazz,String collectionName) { 
		Pageable pageable = PageRequest.of(page, count);
		query.with(Sort.by(Order.desc("createTime")));
		List<T> list = template.find(query.with(pageable), clazz, collectionName);
		return list;
	}

}
