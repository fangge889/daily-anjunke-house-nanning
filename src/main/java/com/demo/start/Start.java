package com.demo.start;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.ibatis.session.SqlSession;

import com.demo.mapper.HouseMapper;
import com.demo.model.HousePriceRecord;
import com.demo.thread.CitySearchThread;
import com.demo.thread.DistrictSearchThread;
import com.demo.thread.HouseSearchThread;
import com.demo.utils.MyBatisUtil;

public class Start {
	public static void main(String[] args) {
		
		SqlSession sqlSession = MyBatisUtil.getSqlSession(true);
		HouseMapper mapper = sqlSession.getMapper(HouseMapper.class);
		HousePriceRecord housePriceRecord = mapper.findHousePriceRecordToday();
		if(null != housePriceRecord) {
			 String createTime = housePriceRecord.getCreateTime().substring(0, 10);
			 String nowTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			 //今天没有抓取，执行抓取
			 if(!createTime.equals(nowTime)) { 
				 System.out.println("今天没有抓取，开始执行！");
				 new CitySearchThread().start();
				 new DistrictSearchThread().start();
				 new HouseSearchThread().start();
			 }else {
				 System.out.println("今天已抓取，不执行！");
			 }
		}
	}
}
