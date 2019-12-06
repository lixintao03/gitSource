package com.tao.mq;

import javax.annotation.Resource;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tao.entity.Stock;
import com.tao.order.OrderInterFace;


@Component
public class Consumer {
	Logger logger = LoggerFactory.getLogger(Consumer.class);
	Gson gson = new GsonBuilder().create();
	
	@Resource(name = "orderServiceAsync")
	private OrderInterFace asyncOrderService;
	@KafkaListener(topics = {Producer.TOPIC})
	public void Consume(ConsumerRecord<?, ?> record) {
		logger.info("接收到来自"+record.topic()+"的消息:"+record.value());
		MessageInfo messageInfo = gson.fromJson(record.value().toString(), MessageInfo.class);
		asyncOrderService.consumerTopicToCreateOrderWithKafka(messageInfo);
	}

}
