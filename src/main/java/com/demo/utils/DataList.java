package com.demo.utils;

import java.util.LinkedList;

public class DataList {
	public static LinkedList<String> cityList = new LinkedList<String>();//省市链接集合
	public static LinkedList<String> districtList = new LinkedList<String>();//每个城市的地区集合
	public static boolean cityFlag = true;//判断获取省市的集合有没有完成
	public static boolean districtFlag  = true;//判断获取省市各个区域的集合有没有完成
}
