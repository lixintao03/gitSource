package com.tao.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Controller;
//import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tao.entity.Student;
import com.tao.mongoDao.StudentMongoDao;

/**
 * 页面跳转控制类
 * @author 李新涛
 *
 */
@Controller
public class PageController {
	@Autowired
	private StudentMongoDao mongoDao;

	@RequestMapping("/main")
	public String mainPage() {
		return "main";
	}
	@RequestMapping("/category")
	public String categoryPage() {
		return "categories";
	}
	
	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/query")
	@ResponseBody
	public List<Map> testQueryAll() {
		List<Map> list = mongoDao.find("student");
		return list;
	}
	
	/**
	 * @param name
	 * @return
	 */
	@RequestMapping("/queryOne")
	@ResponseBody
	public List<Student> testQueryOne(String name) {
		Query query = Query.query(Criteria.where("name").is(name));
		List<Student> list = mongoDao.findByQuery(query,Student.class,"student");
		return list;
	}
	@RequestMapping("/saveOne")
	@ResponseBody
	public Integer testSaveOne(String name) {
		Student student = new Student();
		student.setName(name);
		student.setAge(10);
		int i = mongoDao.saveOne(student, "student");
		return i;
	}
	@RequestMapping("/deleteOne")
	@ResponseBody
	public Integer testDeleteOne(String name) {
		Query query = Query.query(Criteria.where("name").is(name));
		int i = mongoDao.deleteOne(query, "student");
		return i;
	}
	@RequestMapping("/updateOne")
	@ResponseBody
	public Integer testUpateOne(String name) {
		Query query = Query.query(Criteria.where("name").is(name));
		Update update = new Update();
		update.set("name", name);
		int i = mongoDao.updateOne(query, update, "student");
		return i;
	}
}
