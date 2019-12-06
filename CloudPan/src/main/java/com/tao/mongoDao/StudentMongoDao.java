package com.tao.mongoDao;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.tao.basicDao.AbstractBasicDao;
import com.tao.entity.Student;

@Repository
public class StudentMongoDao extends AbstractBasicDao<Student, ObjectId> {

	@Override
	public List<Student> findByQuery(Query query, Class<Student> clazz, String collectionName) {
		return super.findByQuery(query, clazz, collectionName);
	}


	@Override
	public List<Map> find(String collectionName) {
		return super.find(collectionName);
	}
	

	@Override
	public int saveOne(Student t, String collectionName) {
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
	protected String getPKColumn() {
		return null;
	}
}
