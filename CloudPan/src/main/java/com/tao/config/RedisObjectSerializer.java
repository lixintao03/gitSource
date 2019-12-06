package com.tao.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * 自定义 RedisObjectSerializer.java
 * @author 李新涛
 *
 */
public class RedisObjectSerializer implements RedisSerializer<Object>
{
	private Converter<Object, byte[]> serializingConverter = new SerializingConverter();
	private Converter<byte[], Object> deserializingConverter = new DeserializingConverter();
	
	@Override
	public byte[] serialize(Object obj) throws SerializationException
	{
		if(obj == null) {
			return new byte[0];
		}
		//将对象转换为字节数组
		return this.serializingConverter.convert(obj);
	}

	@Override
	public Object deserialize(byte[] bytes) throws SerializationException
	{
		if (bytes == null || bytes.length == 0)
			return null;
		return this.deserializingConverter.convert(bytes);
	}
}
