package com.demo.start;

import java.util.LinkedList;

public class Test3 {
	public static void main(String[] args) {
		new T3ThreadA().start();
		new T3ThreadB().start();
	}
}
class T3ThreadA extends Thread{
	@Override
	public void run() {
		try {
			Thread.currentThread().sleep(100);//为了让B有沉睡的可能
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=0; i<20; i++) {
			synchronized (T3Data.list) {
				String s = new String("呵呵" + i);
				T3Data.list.add(s);
				if(T3Data.list.size() == 1) {
					T3Data.list.notifyAll();
				}
			}
		}
		T3Data.flag = false;
	}
}
class T3ThreadB extends Thread{
	@Override
	public void run() {
		boolean flag = true;
		while(flag) {
			String s = null;
			synchronized (T3Data.list) {
				System.out.println("B获得锁");
				try {
					s = T3Data.list.getFirst();
					T3Data.list.removeFirst();
				}catch (Exception e) {
				}
				if(s == null) {
					if(T3Data.flag == false) {
						flag = T3Data.flag;
					}else {
						try {
							System.out.println("沉睡");
							T3Data.list.wait(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("被唤醒了");
					}
				}
			}
			System.out.println("处理得到的数据" + s);
		}
	}
}
class T3Data{
	public static LinkedList<String> list = new LinkedList<String>();
	public static boolean flag = true;
}
