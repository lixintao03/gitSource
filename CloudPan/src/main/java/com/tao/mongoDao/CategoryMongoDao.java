package com.tao.mongoDao;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.tao.basicDao.AbstractBasicDao;
import com.tao.entity.Category;

@Repository
public class CategoryMongoDao extends AbstractBasicDao<Category, ObjectId> {

	@Override
	public String getPKColumn() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map> find(String collectionName) {
		return super.find(collectionName);
	}

	@Override
	public List<Category> findByQuery(Query query, Class<Category> clazz, String collectionName) {
		return super.findByQuery(query, clazz, collectionName);
	}

	@Override
	public int saveOne(Category t, String collectionName) {
		return super.saveOne(t, collectionName);
	}

	@Override
	public int deleteOne(Query query, String collectionName) {
		return super.deleteOne(query, collectionName);
	}

	@Override
	public int updateOne(Query query, Update update, String collectionName) {
		return super.updateOne(query, update, collectionName);
	}

	@Override
	public List<Category> findByPage(int page, int count, Query query, Class<Category> clazz,
			String collectionName) {
		return super.findByPage(page, count, query, clazz, collectionName);
	}

}
