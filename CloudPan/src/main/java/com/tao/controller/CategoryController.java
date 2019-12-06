package com.tao.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tao.entity.Category;
import com.tao.entity.User;
import com.tao.mongoDao.CategoryMongoDao;

@Controller
public class CategoryController {
	Logger logger = LoggerFactory.getLogger(CategoryController.class);
	@Autowired
	private CategoryMongoDao mongoDao;
	
	@RequestMapping(value = "/getCategorys", method = RequestMethod.GET)
	@ResponseBody
	public Object getCategorys(HttpServletRequest request){
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if(user == null) {
			return null;
		}
		logger.info("查询到的user"+user);
		Query query = Query.query(Criteria.where("userId").is(user.getName()));
		List<Category> list = mongoDao.findByQuery(query, Category.class, "category");
		logger.info("查询到的categories"+list);
		return list;
	}
	

	

}
