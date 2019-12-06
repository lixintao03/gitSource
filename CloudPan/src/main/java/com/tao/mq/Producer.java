package com.tao.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tao.util.ThreadLocalUtil;


@Component
public class Producer {
	Logger logger = LoggerFactory.getLogger(Producer.class);
	public static final String TOPIC = "newTopic";
	@Autowired
	KafkaTemplate<String, Object> template;
	
	Gson gson = new GsonBuilder().create();

	/**
	 * 回调方法与原调用线程并不是同一个线程，所以原线程不会堵塞，调用玩send方法之后直接返回，
	 * 后续的回调方法是由producer的线程来执行。
	 * @param obj
	 */
	public void produce(Object obj) {
		ListenableFuture<SendResult<String, Object>> future = template.send(TOPIC, gson.toJson(obj));
		future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
			@Override
			public void onSuccess(SendResult<String, Object> result) {
				logger.info("消息发送成功！"+result.toString());
				//long end = System.currentTimeMillis();
				//long start = ThreadLocalUtil.getInstance();
				//ThreadLocalUtil.clear();
				//logger.info("生产者发送消息成功，耗时"+(end-start)+"ms");
				
			}

			@Override
			public void onFailure(Throwable ex) {
				logger.info("消息发送失败！"+ex.getMessage());
				//long end = System.currentTimeMillis();
				//long start = ThreadLocalUtil.getInstance();
				//ThreadLocalUtil.clear();
				//logger.info("生产者发送消息成功，耗时"+(end-start)+"ms");
				ex.printStackTrace();
				
			}
		});

}
}
