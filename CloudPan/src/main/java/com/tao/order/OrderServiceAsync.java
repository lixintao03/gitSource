package com.tao.order;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import com.tao.entity.Stock;
import com.tao.entity.StockOrder;
import com.tao.entity.User;
import com.tao.mq.MessageInfo;
import com.tao.mq.Producer;
import com.tao.mySqlDao.StockDao;
import com.tao.mySqlDao.StockOrderDao;
import com.tao.util.ThreadLocalUtil;

/**
 * 异步方式实现下单
 * 
 * @author 李新涛
 *
 */
@Component("orderServiceAsync")
public class OrderServiceAsync implements OrderInterFace {
	Logger logger = LoggerFactory.getLogger(OrderServiceAsync.class);
	@Autowired
	// RedisTemplate<String, Object> redisTemplate;
	@Resource
	StringRedisTemplate stringRedisTemplate;
	@Autowired
	StockDao stockDao;

	@Autowired
	Producer producer;
	@Autowired
	StockOrderDao orderDao;

	@Override
	public void order(Integer sid,User user) {
		// ThreadLocalUtil.getInstance();
		// 从redis中获取订单
		/*
		 * Integer count = (Integer)
		 * redisTemplate.opsForValue().get("STOCK_COUNT_"+sid);//获取存量 Integer sale =
		 * (Integer) redisTemplate.opsForValue().get("STOCK_SALE_"+sid);//获取存量 Integer
		 * version = (Integer)
		 * redisTemplate.opsForValue().get("STOCK_VERSION_"+sid);//获取版本号
		 * redisTemplate.setValueSerializer(new StringRedisSerializer()); String name =
		 * (String) redisTemplate.opsForValue().get("STOCK_NAME_"+sid);//获取name
		 */
		Integer count = Integer.valueOf(stringRedisTemplate.opsForValue().get("STOCK_COUNT_" + sid));
		Integer sale = Integer.valueOf(stringRedisTemplate.opsForValue().get("STOCK_SALE_" + sid));
		Integer version = Integer.valueOf(stringRedisTemplate.opsForValue().get("STOCK_VERSION_" + sid));
		String name = stringRedisTemplate.opsForValue().get("STOCK_NAME_" + sid);

		if (count < 1) {
			logger.info("库存不足");
			throw new RuntimeException("库存不足！");
		}
		Stock stock = new Stock();
		stock.setId(sid);
		stock.setCount(count);
		stock.setSale(sale);
		stock.setVersion(version);
		stock.setName(name);
		MessageInfo message = new MessageInfo();
		message.setUser(user);
		message.setStock(stock);
		producer.produce(message);
		logger.info("异步下单结束");
	}

	public int consumerTopicToCreateOrderWithKafka(MessageInfo messageInfo) {
		int i = 0;
		Stock stock = messageInfo.getStock();
		User user = messageInfo.getUser();
		// 乐观锁锁定库存
		int result = 1;//updateStockByOptmistic(stock);
		try {
			if(result > 0) {
				// 更新库存
				updateRedisStock(stock,user);
				
			}else {
				throw new RuntimeException("并发更新库失败");
			}
		} catch (Exception e) {
			//创建订单失败，需要回补redis中减掉的值
			stringRedisTemplate.execute(new SessionCallback<Object>() {
				@SuppressWarnings({"rawtypes" })
				@Override
				public Object execute(RedisOperations operations) throws DataAccessException {
					stringRedisTemplate.setEnableTransactionSupport(true);
					stringRedisTemplate.multi();
					stringRedisTemplate.opsForValue().increment("STOCK_COUNT_" + stock.getId(), 1);
					stringRedisTemplate.opsForValue().decrement("STOCK_SALE_" + stock.getId(), 1);
					stringRedisTemplate.opsForValue().decrement("STOCK_VERSION_" + stock.getId(), 1);
					return stringRedisTemplate.exec();
				}
			});
			e.printStackTrace();
		}
		return i;
	}

	// 更新redis
	public void updateRedisStock(Stock stock,User user) {
		stringRedisTemplate.execute(new SessionCallback<Object>() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Object execute(RedisOperations operations) throws DataAccessException {
				operations.watch("STOCK_COUNT_"+stock.getId());
				Integer count = 0;
				String stockId = stringRedisTemplate.opsForValue().get("STOCK_COUNT_" + stock.getId());
				if(!StringUtils.isEmpty(stockId)) {
					count = Integer.valueOf(stockId);
				}
				stringRedisTemplate.setEnableTransactionSupport(true);
				stringRedisTemplate.multi();
				if(count > 0) {
					stringRedisTemplate.opsForValue().decrement("STOCK_COUNT_" + stock.getId(), 1);
					stringRedisTemplate.opsForValue().increment("STOCK_SALE_" + stock.getId(), 1);
					stringRedisTemplate.opsForValue().increment("STOCK_VERSION_" + stock.getId(), 1);
					// 创建订单
					int i = createOrder(stock,user);
					if (i > 0) {
						logger.info("创建订单成功！");
					} else {
						logger.info("创建订单失败！");
					}
				}
				return stringRedisTemplate.exec();
			}
		});
	}

	public int createOrder(Stock stock,User user) {
		StockOrder order = new StockOrder();
		order.setCreateTime(new Date());
		order.setSid(stock.getId());
		order.setName(stock.getName());
		//order.setId(1);
		order.setUserId(user.getName());
		int i = orderDao.insert(order);
		if(i>0) {
			logger.info("创建订单成功" + order);
		}else {
			logger.info("创建订单失败" + order);
		}
		return i;
	}
	//获取订单数据
	
	/**根据用户名与sid（库存编号查询）
	 * @param userId
	 * @param sid
	 */
	@Override
	public List<StockOrder> getOrder(String userId,Integer sid) {
		//先查询redis没有再查库
		
		//查询数据库
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("sid", sid);
		data.put("userId", userId);
		List<StockOrder> list = orderDao.getOrderByUserIdSid(data);
		return list;
	}

	/**
	 * 乐观锁更新库存表
	 * 
	 * @return
	 */
	public int updateStockByOptmistic(Stock stock) {
		return stockDao.updateByOptimiticLock(stock);
	}
}
