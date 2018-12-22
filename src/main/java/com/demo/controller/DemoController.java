package com.demo.controller;
 

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.demo.mapper.HouseMapper;
import com.demo.model.HousePriceRecord;
import com.demo.thread.CitySearchThread;
import com.demo.thread.DistrictSearchThread;
import com.demo.thread.HouseSearchThread;
import com.demo.utils.MyBatisUtil;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.Magic; 
import com.github.abel533.echarts.code.Tool;
import com.github.abel533.echarts.code.Trigger; 
import com.github.abel533.echarts.feature.MagicType;
import com.github.abel533.echarts.series.Line;
import com.github.abel533.echarts.util.EnhancedOption; 

@Controller
public class DemoController {
	 
	@RequestMapping(value = "/test/start")
	public void index(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		SqlSession sqlSession = MyBatisUtil.getSqlSession(true);
		HouseMapper mapper = sqlSession.getMapper(HouseMapper.class);
		HousePriceRecord housePriceRecord = mapper.findHousePriceRecordToday();
		String msg ="今天已抓取，不执行！";
		if(null != housePriceRecord && StringUtils.isNotBlank(housePriceRecord.getCreateTime())) {
			 String createTime = housePriceRecord.getCreateTime().substring(0, 10);
			 String nowTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			 //今天没有抓取，执行抓取
			 if(!createTime.equals(nowTime)) { 
				 msg ="今天没有抓取，开始执行！";
				 new CitySearchThread().start();
				 new DistrictSearchThread().start();
				 new HouseSearchThread().start();
			 } 
		}
		PrintWriter out;
		response.setContentType("text/json;charset=UTF-8");
		out = response.getWriter();
		response.getWriter().write(msg);
		out.close();
	}

	@RequestMapping(value = "/test/list")
	public String nameList(Model model,HttpServletRequest request) {	 
		
		SqlSession sqlSession = MyBatisUtil.getSqlSession(true);
		HouseMapper mapper = sqlSession.getMapper(HouseMapper.class);
		List<HousePriceRecord> list = mapper.findAllHouseNameList(); 
		 
		Pattern pattern = Pattern.compile("[0-9]*"); 
		for(HousePriceRecord h:list) {
			//是否有历史价格
			String price =h.getPrice(); 
	    	price = price.substring(2, price.length()); 
	    	String[] priceArry = price.split("元"); 
	    	String prices = priceArry[0];
	    	String pricess = prices.substring(prices.length()-1, prices.length());
	    	if(pricess.equals("套") || pricess.equals("万")) {
	    		prices = pricess;
	    	}
	    	Matcher isNum = pattern.matcher(prices);
		    if( isNum.matches() ){
			   h.setIsPrice(1);
		    }else {
		       h.setIsPrice(0);
		    }  
		    
		    //最近两次价格是否有变化
		    if(h.getIsPrice() == 1) {
		    	List<HousePriceRecord> priceList = mapper.findPriceList(h.getName());
		    	if(priceList.size() == 2) {
		    		String price1 = priceList.get(0).getPrice();
		    		String price2 = priceList.get(1).getPrice(); 
		            Matcher isNum1 = pattern.matcher(price1);
		            Matcher isNum2 = pattern.matcher(price2);
		    		if(isNum1.matches() && isNum2.matches()) {
		    			if(Integer.parseInt(price1) == Integer.parseInt(price2)) {
			    			h.setIsChangePrice(0);
			    		}else if(Integer.parseInt(price1) > Integer.parseInt(price2)){
			    			h.setIsChangePrice(1);
			    		}else {
			    			h.setIsChangePrice(2);
			    		}
		    		} 
		    	}
		    	
		    	//最近两次价格是否有变化
		    	mapper.findPrice30DayList(h.getName());
		    	if(priceList.size() >= 30) {
		    		if(Integer.parseInt(priceList.get(0).getPrice()) == Integer.parseInt(priceList.get(29).getPrice())) {
		    			h.setIsChange7DayPrice(0);
		    		}else if(Integer.parseInt(priceList.get(0).getPrice()) > Integer.parseInt(priceList.get(29).getPrice())){
		    			h.setIsChange7DayPrice(1);
		    		}else {
		    			h.setIsChange7DayPrice(2);
		    		}
		    	}
		    }
		}
		 
	    	List<HousePriceRecord> areaList = new ArrayList<>(); 
			List<String> areaNameList = mapper.findAreaList();
			for(String area:areaNameList) {
				HousePriceRecord housePriceRecord = new HousePriceRecord();
				housePriceRecord.setAreaName(area);
				
				Integer averagePriceSum = 0;
				List<HousePriceRecord> areaRecordList = mapper.findAreaRecordList(area);
				for(HousePriceRecord h:areaRecordList) {
		        	String price =h.getPrice(); 
		        	price = price.substring(2, price.length()); 
		        	String[] priceArry = price.split("元"); 
		        	Matcher isNum = pattern.matcher(priceArry[0]);
		    	   if( isNum.matches() ){
		    		   averagePriceSum = averagePriceSum + Integer.parseInt(priceArry[0]);
		    	   }  
		        }  
				Integer averagePrice = averagePriceSum/areaRecordList.size();
				housePriceRecord.setAveragePrice(averagePrice.toString());
				areaList.add(housePriceRecord);
			}
			
		model.addAttribute("areaList", areaList);
        model.addAttribute("list", list);
		return "list";
	}
	
	@RequestMapping(value = "/test/info")
	public String nameInfo(Model model,HttpServletRequest request) {		
		String name = request.getParameter("name");
		
		SqlSession sqlSession = MyBatisUtil.getSqlSession(true);
		HouseMapper mapper = sqlSession.getMapper(HouseMapper.class);
		 
        List<String> dayList = testDay(30);
        Collections.sort(dayList); 
        
        EnhancedOption option = new EnhancedOption();
        option.title(name);
        option.tooltip().trigger(Trigger.axis);
        option.legend("价格走势");
        option.toolbox().show(true).feature(Tool.mark,
                Tool.dataView,
                new MagicType(Magic.line, Magic.bar, Magic.stack, Magic.tiled),
                Tool.restore,
                Tool.saveAsImage).padding(20);
        option.calculable(true);
               
        option.xAxis(new CategoryAxis().boundaryGap(false).data(dayList.get(0),dayList.get(1),dayList.get(2),dayList.get(3),
        		dayList.get(4),dayList.get(5),dayList.get(6),dayList.get(7),dayList.get(8),dayList.get(9),dayList.get(10),
        		dayList.get(11),dayList.get(12),dayList.get(13),dayList.get(14),dayList.get(15),dayList.get(16),dayList.get(17),dayList.get(18),
        		dayList.get(19),dayList.get(20),dayList.get(21),dayList.get(22),dayList.get(23),dayList.get(24),dayList.get(25),
        		dayList.get(26),dayList.get(27),dayList.get(28),dayList.get(29)));
        
        option.yAxis(new ValueAxis());

        Line line = new Line("价格走势");
        line.smooth(true).itemStyle().normal().areaStyle().typeDefault();
        
        List<HousePriceRecord> list = mapper.findRecordList(name,30);
        List<Integer> priceList = new ArrayList<>();
        Pattern pattern = Pattern.compile("[0-9]*"); 
        
        for(HousePriceRecord h:list) {
        	String price =h.getPrice(); 
        	price = price.substring(2, price.length()); 
        	String[] priceArry = price.split("元"); 
        	Matcher isNum = pattern.matcher(priceArry[0]);
    	   if( isNum.matches() ){
    		   priceList.add(Integer.parseInt(priceArry[0]));
    	   }  
        }  
        
        if(priceList.size() == 1) {
        	line.data(priceList.get(0));
        } else if(priceList.size() == 2) {
        	line.data(priceList.get(0),priceList.get(1));
        } else if(priceList.size() == 3) {
        	line.data(priceList.get(0),priceList.get(1),priceList.get(2));
        } else if(priceList.size() == 4) {
        	line.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3));
        } else if(priceList.size() == 5) {
        	line.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4));
        } else if(priceList.size() == 6) {
        	line.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5));
        } else if(priceList.size() == 7) {
        	line.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6));
        } else if(priceList.size() == 8) {
        	line.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7));
        } else if(priceList.size() == 9) {
        	line.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8));
        } else if(priceList.size() == 10) {
        	line.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8),priceList.get(9));
        } else if(priceList.size() == 11) {
        	line.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8),priceList.get(9),priceList.get(10));
        } else if(priceList.size() == 12) {
        	line.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8),priceList.get(9),priceList.get(10),priceList.get(11));
        } else if(priceList.size() == 13) {
        	line.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8),priceList.get(9),priceList.get(10),priceList.get(11),priceList.get(12));
        } else if(priceList.size() == 14) {
        	line.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8),priceList.get(9),priceList.get(10),priceList.get(11),priceList.get(12),priceList.get(13));
        } else if(priceList.size() == 15) {
        	line.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8),priceList.get(9),priceList.get(10),priceList.get(11),priceList.get(12),priceList.get(13),priceList.get(14));
        } else if(priceList.size() == 16) {
        	line.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8),priceList.get(9),priceList.get(10),priceList.get(11),priceList.get(12),priceList.get(13),priceList.get(14),priceList.get(15));
        } else if(priceList.size() == 17) {
        	line.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8),priceList.get(9),priceList.get(10),priceList.get(11),priceList.get(12),priceList.get(13),priceList.get(14),priceList.get(15)
        			,priceList.get(16));
        } else if(priceList.size() == 18) {
        	line.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8),priceList.get(9),priceList.get(10),priceList.get(11),priceList.get(12),priceList.get(13),priceList.get(14),priceList.get(15)
        			,priceList.get(16),priceList.get(17));
        } else if(priceList.size() == 19) {
        	line.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8),priceList.get(9),priceList.get(10),priceList.get(11),priceList.get(12),priceList.get(13),priceList.get(14),priceList.get(15)
        			,priceList.get(16),priceList.get(17),priceList.get(18));
        } else if(priceList.size() == 20) {
        	line.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8),priceList.get(9),priceList.get(10),priceList.get(11),priceList.get(12),priceList.get(13),priceList.get(14),priceList.get(15)
        			,priceList.get(16),priceList.get(17),priceList.get(18),priceList.get(19));
        } else if(priceList.size() == 21) {
        	line.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8),priceList.get(9),priceList.get(10),priceList.get(11),priceList.get(12),priceList.get(13),priceList.get(14),priceList.get(15)
        			,priceList.get(16),priceList.get(17),priceList.get(18),priceList.get(19),priceList.get(20));
        } else if(priceList.size() == 22) {
        	line.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8),priceList.get(9),priceList.get(10),priceList.get(11),priceList.get(12),priceList.get(13),priceList.get(14),priceList.get(15)
        			,priceList.get(16),priceList.get(17),priceList.get(18),priceList.get(19),priceList.get(20),priceList.get(21));
        } else if(priceList.size() == 23) {
        	line.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8),priceList.get(9),priceList.get(10),priceList.get(11),priceList.get(12),priceList.get(13),priceList.get(14),priceList.get(15)
        			,priceList.get(16),priceList.get(17),priceList.get(18),priceList.get(19),priceList.get(20),priceList.get(21),priceList.get(22));
        } else if(priceList.size() == 24) {
        	line.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8),priceList.get(9),priceList.get(10),priceList.get(11),priceList.get(12),priceList.get(13),priceList.get(14),priceList.get(15)
        			,priceList.get(16),priceList.get(17),priceList.get(18),priceList.get(19),priceList.get(20),priceList.get(21),priceList.get(22),priceList.get(23));
        } else if(priceList.size() == 25) {
        	line.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8),priceList.get(9),priceList.get(10),priceList.get(11),priceList.get(12),priceList.get(13),priceList.get(14),priceList.get(15)
        			,priceList.get(16),priceList.get(17),priceList.get(18),priceList.get(19),priceList.get(20),priceList.get(21),priceList.get(22),priceList.get(23)
        			,priceList.get(24));
        } else if(priceList.size() == 26) {
        	line.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8),priceList.get(9),priceList.get(10),priceList.get(11),priceList.get(12),priceList.get(13),priceList.get(14),priceList.get(15)
        			,priceList.get(16),priceList.get(17),priceList.get(18),priceList.get(19),priceList.get(20),priceList.get(21),priceList.get(22),priceList.get(23)
        			,priceList.get(24),priceList.get(25));
        } else if(priceList.size() == 27) {
        	line.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8),priceList.get(9),priceList.get(10),priceList.get(11),priceList.get(12),priceList.get(13),priceList.get(14),priceList.get(15)
        			,priceList.get(16),priceList.get(17),priceList.get(18),priceList.get(19),priceList.get(20),priceList.get(21),priceList.get(22),priceList.get(23)
        			,priceList.get(24),priceList.get(25),priceList.get(26));
        } else if(priceList.size() == 28) {
        	line.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8),priceList.get(9),priceList.get(10),priceList.get(11),priceList.get(12),priceList.get(13),priceList.get(14),priceList.get(15)
        			,priceList.get(16),priceList.get(17),priceList.get(18),priceList.get(19),priceList.get(20),priceList.get(21),priceList.get(22),priceList.get(23)
        			,priceList.get(24),priceList.get(25),priceList.get(26),priceList.get(27));
        } else if(priceList.size() == 29) {
        	line.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8),priceList.get(9),priceList.get(10),priceList.get(11),priceList.get(12),priceList.get(13),priceList.get(14),priceList.get(15)
        			,priceList.get(16),priceList.get(17),priceList.get(18),priceList.get(19),priceList.get(20),priceList.get(21),priceList.get(22),priceList.get(23)
        			,priceList.get(24),priceList.get(25),priceList.get(26),priceList.get(27),priceList.get(28));
        } else if(priceList.size() == 30) {
        	line.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8),priceList.get(9),priceList.get(10),priceList.get(11),priceList.get(12),priceList.get(13),priceList.get(14),priceList.get(15)
        			,priceList.get(16),priceList.get(17),priceList.get(18),priceList.get(19),priceList.get(20),priceList.get(21),priceList.get(22),priceList.get(23)
        			,priceList.get(24),priceList.get(25),priceList.get(26),priceList.get(27),priceList.get(28),priceList.get(29));
        } 
        
        option.series(line);
		 
        model.addAttribute("option", option);
		return "nameInfo";
	}
	
	 
	
	/**
     * 获取过去或者未来 任意天内的日期数组
     * @param intervals      intervals天内
     * @return              日期数组
     */
    public static List<String> testDay(int intervals ) {
        ArrayList<String> pastDaysList = new ArrayList<>();
        ArrayList<String> fetureDaysList = new ArrayList<>();
        for (int i = 0; i <intervals; i++) {
            pastDaysList.add(getPastDate(i));
            fetureDaysList.add(getFetureDate(i));
        }
        return pastDaysList;
    }
 
    /**
     * 获取过去第几天的日期
     *
     * @param past
     * @return
     */
    public static String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today); 
        return result;
    }
 
    /**
     * 获取未来 第 past 天的日期
     * @param past
     * @return
     */
    public static String getFetureDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today); 
        return result;
    }
    
    @RequestMapping(value = "/test/areaList")
	public String areaList(Model model,HttpServletRequest request) {	 
    	String areaName = request.getParameter("areaName");
		SqlSession sqlSession = MyBatisUtil.getSqlSession(true);
		HouseMapper mapper = sqlSession.getMapper(HouseMapper.class);
		List<HousePriceRecord> list = mapper.findHouseNameListByarea(areaName); 
		 
		Pattern pattern = Pattern.compile("[0-9]*"); 
		for(HousePriceRecord h:list) {
			//是否有历史价格
			String price =h.getPrice(); 
	    	price = price.substring(2, price.length()); 
	    	String[] priceArry = price.split("元"); 
	    	Matcher isNum = pattern.matcher(priceArry[0]);
		    if( isNum.matches() ){
			   h.setIsPrice(1);
		    }else {
		       h.setIsPrice(0);
		    }  
		    
		    //最近两次价格是否有变化
		    if(h.getIsPrice() == 1) {
		    	List<HousePriceRecord> priceList = mapper.findPriceList(h.getName());
		    	if(priceList.size() == 2) {
		    		String price1 = priceList.get(0).getPrice();
		    		String price2 = priceList.get(1).getPrice(); 
		            Matcher isNum1 = pattern.matcher(price1);
		            Matcher isNum2 = pattern.matcher(price2);
		            if(isNum1.matches() && isNum2.matches()) {
			    		if(Integer.parseInt(price1) == Integer.parseInt(price2)) {
			    			h.setIsChangePrice(0);
			    		}else if(Integer.parseInt(price1) > Integer.parseInt(price2)){
			    			h.setIsChangePrice(1);
			    		}else {
			    			h.setIsChangePrice(2);
			    		}
		            }
		    	}
		    	
		    	//最近两次价格是否有变化
		    	mapper.findPrice30DayList(h.getName());
		    	if(priceList.size() >= 30) {
		    		if(Integer.parseInt(priceList.get(0).getPrice()) == Integer.parseInt(priceList.get(29).getPrice())) {
		    			h.setIsChange7DayPrice(0);
		    		}else if(Integer.parseInt(priceList.get(0).getPrice()) > Integer.parseInt(priceList.get(29).getPrice())){
		    			h.setIsChange7DayPrice(1);
		    		}else {
		    			h.setIsChange7DayPrice(2);
		    		}
		    	}
		    }
		}
		 
        model.addAttribute("list", list);
		return "areaInfo";
	}
	 
}