- 1、从安居客抓取南宁的楼盘情况，当然也可以改成其他城市，
  修改路径：
  ``` 
  src/main/java/com/demo/thread/CitySearchThread.java
  
  34行：if("https://nanning.anjuke.com".equals(url)) 
  
  修改为其他城市即可
  
- 2、做了一个基础的界面，显示最近两天或7天的房价有无变化
- 3、使用jetty启动即可
- 4、修改数据库配置：src/main/resources/database.properties
- 5、价格波动使用百度echarts实现
- 6、引用
``` 
  <dependency>
	<groupId>com.github.abel533</groupId>
	<artifactId>ECharts</artifactId>
	<version>3.0.0.6</version>
  </dependency>
		 
		
