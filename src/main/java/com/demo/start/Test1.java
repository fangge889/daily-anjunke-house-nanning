package com.demo.start;

public class Test1 {
	public static void main(String[] args) {
		new T1ThreadB().start();
		new T1ThreadA().start();
	}
}
class T1ThreadA extends Thread{
	@Override
	public void run() {
		for(int i=0; i<10; i++) {
			synchronized (T1Data.count) {
				if(T1Data.count == 9) {
					T1Data.count.notifyAll();
				}
				System.out.println("A加锁的对象" + T1Data.count.hashCode());
				T1Data.count = T1Data.count + 1;
			}
		}
	}
}
class T1ThreadB extends Thread{
	@Override
	public void run() {
		while(true) {
			synchronized (T1Data.count) {
				System.out.println("B加锁的对象" + T1Data.count.hashCode());
				if(T1Data.count == 10) {
					break;
				}else {
					try {
						T1Data.count.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}
class T1Data{
	public static Integer count = new Integer(0);
}
