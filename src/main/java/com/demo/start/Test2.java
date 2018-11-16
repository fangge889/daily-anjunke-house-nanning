package com.demo.start;

import java.util.LinkedList;

public class Test2 {
	public static void main(String[] args) {
//		new T2ThreadA().start();
//		new T2ThreadB().start();
		new T2ThreadC().start();
		new T2ThreadD().start();
	}
}
class T2ThreadA extends Thread{
	@Override
	public void run() {
		T2Util.test1();
	}
}
class T2ThreadB extends Thread{
	@Override
	public void run() {
		T2Util.test2();
	}
}
class T2ThreadC extends Thread{
	@Override
	public void run() {
		T2Util.test3();
	}
}
class T2ThreadD extends Thread{
	@Override
	public void run() {
		T2Util.test4();
	}
}

class T2Util{
	public synchronized static void test1() {
		System.out.println(Thread.currentThread().getName() + "访问test1");
		try {
			Thread.currentThread().sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized static void test2() {
		System.out.println(Thread.currentThread().getName() + "访问test2");
		try {
			Thread.currentThread().sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static LinkedList<String> list = new LinkedList<String>();
	
	public static void test3() {
		synchronized (T2Util.list) {
			list.add("aaa");
			System.out.println(Thread.currentThread().getName() + "访问test3");
		}
		try {
			Thread.currentThread().sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void test4() {
		synchronized (T2Util.list) {
			list.add("aaa");
			System.out.println(Thread.currentThread().getName() + "访问test4");
		}
		try {
			Thread.currentThread().sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
