package com.tao.limiter;

import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Component;

/**
 * 漏桶限流
 * @author 李新涛
 *
 */
public class LeakBucketLimiter extends Limiter{
	private final long capacity;                                            // 水桶容量, 一秒流光一桶水
    private double remainWater;                                             // 目前水桶剩下的水量
    private long lastTime;                                                  // 时间戳
    private ReentrantLock lock = new ReentrantLock();                       // 可重入锁

	public LeakBucketLimiter(int qps) {
		super(qps);
		capacity = qps;
		remainWater = capacity;
		lastTime = 0;
	}

	@Override
	public boolean tryAcquire() {
		//获取前先加锁
		lock.lock();
		long now = System.currentTimeMillis();//获取当前时间
		double outWater = ((now - lastTime)/1000.0)*capacity; //判断从到目前为止，一共流出了多少桶水
		lastTime = now;//将当前时间赋给最近时间
		//remainWater = Math.max(0, remainWater-outWater);//用当前桶的剩余量（如果是刚刚进来则此时是一满桶，即capacity）减去流出量来算出当前水桶剩余量
		if(outWater > remainWater) {//当前桶中已经没有水了
			remainWater = 1;
			lock.unlock();
			return true;
		}else {//当前桶中还有水
			remainWater-=outWater;
			if(remainWater+1 <= capacity) {//此时桶中剩下的水再加上新来的一滴水小于等于桶容量（即再加一滴水桶就满了）
				remainWater+=1;
				lock.unlock();
				return true;
			}else {
				lock.unlock();
				return false;
			}
		}
	}

	

}
