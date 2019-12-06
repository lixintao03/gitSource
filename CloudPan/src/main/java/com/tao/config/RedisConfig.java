package com.tao.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonAutoDetect;;

@Configuration
public class RedisConfig {
	@Autowired
	private RedisConnectionFactory redisConnectionFactory;

	@Bean
    public RedisTemplate<String, Object> setRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        /*RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 设置value的序列化规则和 key的序列化规则
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        System.out.println(redisTemplate);
        return redisTemplate;*/
		 RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
	        redisTemplate.setConnectionFactory(redisConnectionFactory);
	        redisTemplate.setKeySerializer(new StringRedisSerializer()); // key的序列化类型

	        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
	        ObjectMapper objectMapper = new ObjectMapper();
	        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
	        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

	        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
	        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer); // value的序列化类型
	        System.out.println(redisTemplate);
	        return redisTemplate;
    }
	@Bean("conn")
	RedisConnection getConn() {
		return redisConnectionFactory.getConnection();
	}
	@Bean
    StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
	    StringRedisTemplate template = new StringRedisTemplate();
	    template.setConnectionFactory(connectionFactory);
	    //template.setKeySerializer(new StringRedisSerializer());
	    //template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
	    return template;
    }
}
