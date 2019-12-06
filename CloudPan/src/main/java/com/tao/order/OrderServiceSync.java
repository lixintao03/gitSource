package com.tao.order;

import java.util.List;

import org.springframework.stereotype.Component;

import com.tao.entity.Stock;
import com.tao.entity.StockOrder;
import com.tao.entity.User;
import com.tao.mq.MessageInfo;

/**
 * 下单服务
 * @author 李新涛
 *
 */
@Component("orderServiceSync")
public class OrderServiceSync implements OrderInterFace{

	@Override
	public void order(Integer sid, User user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int createOrder(Stock stock, User user) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int consumerTopicToCreateOrderWithKafka(MessageInfo stock) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<StockOrder> getOrder(String userId, Integer sid) {
		// TODO Auto-generated method stub
		return null;
	}


}
