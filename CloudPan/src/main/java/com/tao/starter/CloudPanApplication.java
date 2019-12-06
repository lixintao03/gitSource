package com.tao.starter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.tao.config","com.tao.*"})
@MapperScan("com.tao.mySqlDao")
@EntityScan("com.tao.entity")
@EnableCaching
public class CloudPanApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudPanApplication.class, args);
	}

}
