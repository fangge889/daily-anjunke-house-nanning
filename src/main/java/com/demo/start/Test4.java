package com.demo.start;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Test4 {
	public static void main(String[] args) {
		new T4ThreadA().start();
		new T4ThreadB().start();
	}
}

class T4ThreadA extends Thread {
	@Override
	public void run() {
		try {
			Thread.currentThread().sleep(100);// 为了让B有沉睡的可能
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < 20; i++) {
			try {
				T4Data.lock.lock();
				String s = new String("呵呵" + i);
				T4Data.list.add(s);
				if (T4Data.list.size() == 1) {
					T4Data.condition.signalAll();
				}
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				T4Data.lock.unlock();
			}
		}
		System.out.println("A完成");
		T4Data.flag = false;
	}
}

class T4ThreadB extends Thread {
	@Override
	public void run() {
		boolean flag = true;
		while (flag) {
			String s = null;
			try {
				T4Data.lock.lock();
				System.out.println("B获得锁");
				try {
					s = T4Data.list.getFirst();
					T4Data.list.removeFirst();
				} catch (Exception e) {
				}
				if (s == null) {
					if (T4Data.flag == false) {
						flag = T4Data.flag;
					} else {
						System.out.println("沉睡");
						T4Data.condition.await(10, TimeUnit.SECONDS);
						System.out.println("被唤醒了");
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				T4Data.lock.lock();
			}
			System.out.println("处理得到的数据" + s);
		}
	}
}



class T4Data {
	public static ReentrantLock lock = new ReentrantLock();
	public static Condition condition = lock.newCondition();
	public static LinkedList<String> list = new LinkedList<String>();
	public static boolean flag = true;
	
}