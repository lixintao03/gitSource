package com.tao.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tao.config.MvcConfigger;
import com.tao.entity.StockOrder;
import com.tao.entity.User;
import com.tao.limiter.LeakBucketLimiter;
import com.tao.limiter.Limiter;
import com.tao.order.OrderInterFace;

@Controller
public class PaymentController {
	Logger logger = LoggerFactory.getLogger(PaymentController.class);
	@Resource(name = "orderServiceAsync")
	private OrderInterFace asyncOrderService;
	@Resource(name = "orderServiceSync")
	private OrderInterFace syncOrderService;
	@Resource
	StringRedisTemplate stringRedisTemplate;
	@Autowired
	private MvcConfigger mvcConfigger;
	@Resource
	private LeakBucketLimiter leakBucketLimiter;

	/**
	 * redis
	 * @param request
	 *1、从redis中获取热数据，获取当前是否还有存货	
	 *2、redis有存货，允许下单，发送到mq，消费者消费消息，产生订单对象，并扣减redis库存，保存订单到redis中
	 */
	@RequestMapping("/order")
	@ResponseBody
	public String pay(HttpServletRequest request,Integer sid){
		//获取登录用户信息
		User user = (User) request.getSession().getAttribute("user");
		if(user == null) {
			logger.info("从session中未获取到user对象");
			return null;
		}
		try {
			System.out.println("获取的leakBucketLimiter："+leakBucketLimiter);
			if(leakBucketLimiter== null) {
				leakBucketLimiter = mvcConfigger.leakBucketLimiter();
				System.out.println("获取的leakBucketLimiter为空");
			}
			//从限流策略中获取限流方法
			if(leakBucketLimiter.tryAcquire())
				asyncOrderService.order(sid,user);
			else
				return "队列拥挤，请重试！";
		} catch (Exception e) {
			logger.error("error:",e);
			return "下单失败";
		}
		return "正在下单，请稍后";
	}
	//获取支付结果
	@RequestMapping("/getPayResult")
	@ResponseBody
	public Map<String, Object> getPayResult(HttpServletRequest request) {
		//客户端轮询获取订单库以获取订单信息
		Integer sid = Integer.valueOf(request.getParameter("sid"));
		//获取登录用户信息
		User user = (User) request.getSession().getAttribute("user");
		if(user == null) {
			logger.info("从session中未获取到user对象");
			return null;
		}
		//0失败、1秒杀中、2成功
		String isSuccess = "0";
		//获取
		List<StockOrder> list = asyncOrderService.getOrder(user.getName(), sid);
		if(list == null || list.isEmpty()) {
			//获取库存是否还有
			Integer count = 0;
			String stockId = stringRedisTemplate.opsForValue().get("STOCK_COUNT_" +sid);
			if(!StringUtils.isEmpty(stockId)) {
				count = Integer.valueOf(stockId);
			}
			if(count > 0) {
				isSuccess = "1";
			}else {
				isSuccess = "0";
			}
		}else {
			isSuccess = "2";
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isSuccess", isSuccess);
		map.put("data", list);
		return map;
	}
}
