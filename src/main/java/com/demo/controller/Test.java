package com.demo.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Test {
	public static void main(String args[]) {
		List<String> list = new ArrayList<String>() {
			private static final long serialVersionUID = -6469181946992000438L;
			{
				this.add("aaa");
				this.add("bbb");
				this.add("ccc");
				this.add("aaa");
				this.add("bbb");
				this.add("ddd");
				this.add("eee");
			}
		};
		List<String> list1 = new ArrayList<String>();
		Set<String> set = new HashSet<String>();
		for (String ls : list) {
			if (set.contains(ls)) { 
				list1.add(ls);
			}
			set.add(ls);
		}
		System.out.println(set);
		System.out.println(list1);
	}
}
