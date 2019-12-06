package com.tao.limiter;


public class CountLimiter extends Limiter{
	private int count;                             // 桶内能装多少令牌
    private long lastTime;                                  // 时间戳
	

	CountLimiter(int qps) {
		super(qps);
		count = 0;
		lastTime = 0;
	}

	@Override
	public synchronized boolean tryAcquire() {
		//获取当前时间
		long now = System.currentTimeMillis();
		if(now - lastTime > 1000) {//当时间间隔大于一秒时，重新计数
			lastTime = now;
			count = 1;
			return true;
		}else if(count < qps) {//小于一秒时，count要小于qps
			count++;
			return true;
		}else {
			return false;
		}
	}

}
