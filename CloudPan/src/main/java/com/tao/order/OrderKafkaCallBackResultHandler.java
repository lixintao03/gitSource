package com.tao.order;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;

import com.tao.util.ThreadLocalUtil;

/**
 * 本来打算把kafka发送回调单独抽出来作为一个类来处理，
 * 但是只要把泛型加上spring就不能注入进去，去掉就可以，所以直接使用future去处理了，暂不使用此类
 * 异步处理结果回调
 * @author 李新涛
 *
 */
//@Component
public class OrderKafkaCallBackResultHandler implements ProducerListener<String, Object>{

	Logger logger = LoggerFactory.getLogger(OrderKafkaCallBackResultHandler.class);
	@Override
	public void onSuccess(ProducerRecord<String, Object> producerRecord, RecordMetadata recordMetadata) {
		logger.info("消息发送成功！"+producerRecord.toString());
		long end = System.currentTimeMillis();
		long start = ThreadLocalUtil.getInstance();
		ThreadLocalUtil.clear();
		logger.info("生产者发送消息成功，耗时"+(end-start)+"ms");
	}

	@Override
	public void onError(ProducerRecord<String, Object> producerRecord, Exception exception) {
		logger.info("消息发送成功！"+producerRecord.toString());
		long end = System.currentTimeMillis();
		long start = ThreadLocalUtil.getInstance();
		ThreadLocalUtil.clear();
		logger.info("生产者发送消息成功，耗时"+(end-start)+"ms");
		exception.printStackTrace();
	}

	
	
}
