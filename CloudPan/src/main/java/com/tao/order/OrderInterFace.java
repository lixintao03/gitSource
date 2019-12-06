package com.tao.order;

import java.util.List;

import com.tao.entity.Stock;
import com.tao.entity.StockOrder;
import com.tao.entity.User;
import com.tao.mq.MessageInfo;

/**
 * 同步方式实现
 * @author 李新涛
 *
 */
public interface OrderInterFace {
	
	void order(Integer sid,User user);
	int createOrder(Stock stock,User user);
	int consumerTopicToCreateOrderWithKafka(MessageInfo stock);
	public List<StockOrder> getOrder(String userId,Integer sid);
}
