package com.demo.start;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Test5 {
	public static CountDownLatch count = new CountDownLatch(5);
	public static void main(String[] args) {
		ExecutorService pool = Executors.newFixedThreadPool(5);
		for(int i = 0; i<5; i++) {
			pool.submit(new T5Thread());
		}
		pool.shutdown();
		try {
			count.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("完成");
	}
}

class T5Thread extends Thread{
	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName());
		Test5.count.countDown();
	}
}
