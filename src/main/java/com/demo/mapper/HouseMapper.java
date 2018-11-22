package com.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.demo.model.HousePriceRecord;
 
public interface HouseMapper {
	 
	 //查询楼盘今天是否已抓取
	 @Select("select createTime from house_price_record where name =#{name} ORDER BY createTime desc LIMIT 1 ")
	 public HousePriceRecord findHousePriceRecord(String name);
	 
	 //查询今天是否抓取楼盘
	 @Select("select createTime from house_price_record ORDER BY createTime desc LIMIT 1 ")
	 public HousePriceRecord findHousePriceRecordToday();
	 
	 //添加每天房价记录
	 @Insert("insert into house_price_record values(null,#{name}, #{address}, #{state}, #{describe}, #{price},now(),#{areaName})")
	 public int addHousePriceRecord(HousePriceRecord house);
	 
	 //查询单个楼盘房价记录
	 @Select("select * from house_price_record where name =#{name} ")
	 public List<HousePriceRecord> findRecordList(String name);
	 
	 //查询所有楼盘名称
	 @Select("select * from house_price_record GROUP BY name ORDER BY substring(SUBSTRING_INDEX(price,'元',1), 3)  asc ")
	 public List<HousePriceRecord> findAllHouseNameList();
	 
	//查询待售、在售、售罄的楼盘房价记录
	 @Select("select * from house_price_record where state =#{state} ")
	 public List<HousePriceRecord> findRecordStateList(String state);
		 		 
	//查询住宅、办公楼、商住的楼盘房价记录
	 @Select("select * from house_price_record where describe =#{describe} ")
	 public List<HousePriceRecord> findRecordDescribeList(String describe);
		 
	//查询售价区域楼盘房价记录
	 @Select("select * from house_price_record where price >= #{price}  and price < #{endPrice} ")
	 public List<HousePriceRecord> findRecordPriceList(String price,String endPrice);
	  
	//查询时间区域楼盘房价记录
	 @Select("select * from house_price_record where createTime >= #{createTime}  and createTime < #{endTime}  ")
	 public List<HousePriceRecord> findRecordTimeList(String createTime,String endTime);
	 
	 //最近两次价格是否有变化
	 @Select("SELECT substring(SUBSTRING_INDEX(price,'元',1), 3) as price from house_price_record where name= #{name} ORDER BY createTime DESC LIMIT 2  ")
	 public List<HousePriceRecord> findPriceList(String name);
	 
	//最近7天价格是否有变化
	@Select("SELECT substring(SUBSTRING_INDEX(price,'元',1), 3) as price from house_price_record where name= #{name} "+ 
			" and createTime between DATE_FORMAT(DATE_SUB(NOW(),INTERVAL 8 Day),'%Y-%m-%d')  and DATE_ADD(DATE_FORMAT(NOW(),'%Y-%m-%d'),INTERVAL 1 Day)  ORDER BY createTime DESC ") 
	public List<HousePriceRecord> findPrice7DayList(String name);
	 
	 //查询所有楼盘房价记录
	 @Select("select * from house_price_record ")
	 public List<HousePriceRecord> findAllRecordList();
	  
}
