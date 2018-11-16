package com.demo.controller;
 

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
	 
	@RequestMapping(value = "/test/start", method = RequestMethod.GET)
	public void index() {
		SqlSession sqlSession = MyBatisUtil.getSqlSession(true);
		HouseMapper mapper = sqlSession.getMapper(HouseMapper.class);
		HousePriceRecord housePriceRecord = mapper.findHousePriceRecordToday();
		if(null != housePriceRecord && StringUtils.isNotBlank(housePriceRecord.getCreateTime())) {
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

	@RequestMapping(value = "/test/list", method = RequestMethod.GET)
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
		    		if(Integer.parseInt(priceList.get(0).getPrice()) == Integer.parseInt(priceList.get(1).getPrice())) {
		    			h.setIsChangePrice(0);
		    		}else if(Integer.parseInt(priceList.get(0).getPrice()) > Integer.parseInt(priceList.get(1).getPrice())){
		    			h.setIsChangePrice(1);
		    		}else {
		    			h.setIsChangePrice(2);
		    		}
		    	}
		    	
		    	//最近两次价格是否有变化
		    	mapper.findPrice7DayList(h.getName());
		    	if(priceList.size() == 7) {
		    		if(Integer.parseInt(priceList.get(0).getPrice()) == Integer.parseInt(priceList.get(6).getPrice())) {
		    			h.setIsChange7DayPrice(0);
		    		}else if(Integer.parseInt(priceList.get(0).getPrice()) > Integer.parseInt(priceList.get(6).getPrice())){
		    			h.setIsChange7DayPrice(1);
		    		}else {
		    			h.setIsChange7DayPrice(2);
		    		}
		    	}
		    }
		}
		
        model.addAttribute("list", list);
		return "list";
	}
	
	@RequestMapping(value = "/test/info", method = RequestMethod.GET)
	public String nameInfo(Model model,HttpServletRequest request) {		
		String name = request.getParameter("name");
		
		SqlSession sqlSession = MyBatisUtil.getSqlSession(true);
		HouseMapper mapper = sqlSession.getMapper(HouseMapper.class);
		 
        List<String> dayList = testDay(15);
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
        		dayList.get(11),dayList.get(12),dayList.get(13),dayList.get(14)));
        
        option.yAxis(new ValueAxis());

        Line l1 = new Line("价格走势");
        l1.smooth(true).itemStyle().normal().areaStyle().typeDefault();
        
        List<HousePriceRecord> list = mapper.findRecordList(name);
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
        	l1.data(priceList.get(0));
        } else if(priceList.size() == 2) {
        	l1.data(priceList.get(0),priceList.get(1));
        } else if(priceList.size() == 3) {
        	l1.data(priceList.get(0),priceList.get(1),priceList.get(2));
        } else if(priceList.size() == 4) {
        	l1.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3));
        } else if(priceList.size() == 5) {
        	l1.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4));
        } else if(priceList.size() == 6) {
        	l1.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5));
        } else if(priceList.size() == 7) {
        	l1.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6));
        } else if(priceList.size() == 8) {
        	l1.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7));
        } else if(priceList.size() == 9) {
        	l1.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8));
        } else if(priceList.size() == 10) {
        	l1.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8),priceList.get(9));
        } else if(priceList.size() == 11) {
        	l1.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8),priceList.get(9),priceList.get(10));
        } else if(priceList.size() == 12) {
        	l1.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8),priceList.get(9),priceList.get(10),priceList.get(11));
        } else if(priceList.size() == 13) {
        	l1.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8),priceList.get(9),priceList.get(10),priceList.get(11),priceList.get(12));
        } else if(priceList.size() == 14) {
        	l1.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8),priceList.get(9),priceList.get(10),priceList.get(11),priceList.get(12),priceList.get(13));
        } else if(priceList.size() == 15) {
        	l1.data(priceList.get(0),priceList.get(1),priceList.get(2),priceList.get(3),priceList.get(4),priceList.get(5),priceList.get(6),priceList.get(7)
        			,priceList.get(8),priceList.get(9),priceList.get(10),priceList.get(11),priceList.get(12),priceList.get(13),priceList.get(14));
        } 
        
        option.series(l1);
		 
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
	 
}