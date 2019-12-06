package com.tao.limiter;



public class TokenLimiter extends Limiter{

	private final int capacity;                             // 桶内能装多少令牌
    private double curTokenNum;                             // 现在桶内令牌数量(用double存)
    private long lastTime;                                  // 时间戳
	
	public TokenLimiter(int qps) {
		super(qps);
		capacity = qps;
		curTokenNum = 0;
		lastTime = 0;
	}

	@Override
	public synchronized boolean tryAcquire() {
		long now = System.currentTimeMillis();
		double intoToken = ((now-lastTime)/1000.0)*capacity; //经过这段时间往桶里放入了多少个令牌
		if(intoToken + curTokenNum > capacity) {//如果当前令牌桶中有足够多的令牌，就先不加如令牌桶
			curTokenNum = capacity-1;//因为当前线程进来要获取一个令牌，所以要减一
			return true;
			
		}else if(intoToken + curTokenNum >= 1){
			//还有令牌可以取
			curTokenNum += intoToken -1;
			return true;
		}else {//无令牌可取
			curTokenNum += intoToken;
			return false;
		}
	}

	

}
