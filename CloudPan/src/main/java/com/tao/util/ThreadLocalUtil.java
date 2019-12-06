package com.tao.util;

public class ThreadLocalUtil {

	private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
	
	public static Long getInstance() {
		Long start = threadLocal.get();
		if(null == start) {
			start = System.currentTimeMillis();
			threadLocal.set(start);
		}
		return start;
	}
	public static void clear() {
		threadLocal.remove();
	}

}
