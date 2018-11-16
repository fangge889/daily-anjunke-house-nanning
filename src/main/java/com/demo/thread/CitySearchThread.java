package com.demo.thread;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.demo.utils.DataList;
import com.demo.utils.HttpUtils;

/**
 * 搜索全国有哪些城市
 * @author duiya
 *
 */
public class CitySearchThread extends Thread {
	public void run() {
		String result = null;
		result = HttpUtils.CreatHttpGet("https://www.anjuke.com/sy-city.html");
		if (result != null) {
			try {
				Document doc = Jsoup.parse(result);
				Element cityItmElement = doc.getElementsByClass("city-itm").first();
				Element lettercityElement = cityItmElement.getElementsByClass("letter_city").first();
				Element ulElement = lettercityElement.getElementsByTag("ul").first();
				Elements liElements = ulElement.getElementsByTag("li");
				for (int i = 0; i < liElements.size() - 1; i++) {
					Element cityListElement = liElements.get(i).getElementsByClass("city_list").first();
					Elements cityElements = cityListElement.getElementsByTag("a");
					for (int j = 0; j < cityElements.size(); j++) {
						Element cityElement = cityElements.get(j);
						String url = cityElement.absUrl("href");
						synchronized (DataList.cityList) {
							if("https://nanning.anjuke.com".equals(url)) {
								DataList.cityList.add(url);
								if (DataList.cityList.size() == 1) {
									DataList.cityList.notifyAll();
								}
							}
						}
					}
				}
				System.out.println("城市搜索完成");
			}catch (Exception e) {
				System.out.println("网页被拦截了");
			}finally {
				// 标志搜索所有的城市完成
				DataList.cityFlag = false;
			}
		}else {
			// 标志搜索所有的城市完成
			DataList.cityFlag = false;
			System.out.println("城市搜索完成");
		}
	}
}
/**  网页结构
 * <html>
 * 	  <body>
 * 		<div class="content">
 * 			<div class="city-itm"> 
 * 				<div class="letter_city">
 * 					<ul>
 * 						<li>
 * 							 <div class="city_list">
 *                       		<a href="" class="hot"></a>
 *                       		<a href="" class="hot"></a>
 *                       	</div>
 *                       </li>
 *                       <li>
 * 							 <div class="city_list">
 *                       		<a href="" class="hot"></a>
 *                       		<a href="" class="hot"></a>
 *                       	</div>
 *                       </li>
 * 					</ul>
 * 				</div>
 *			</div>
 * 		</div>
 * 	 </body>
 * </html>
 */
