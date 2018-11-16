package com.demo.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.demo.mapper.HouseMapper; 
import com.demo.model.HousePriceRecord;
 

public class ExcisionUtil {
	/**
	 * 切割出来包含楼盘的elements,包含页数
	 * @param result
	 * @return
	 */
	public static Map<String,Object> roughExcisionAPage(String result){
		HashMap<String, Object> map = new HashMap<String, Object>();
		if(result != null) {
			Document doc = Jsoup.parse(result);
			try {
				Element elementRoot = doc.getElementById("container");
				Element listElement = elementRoot.getElementsByClass("list-contents").first();
				//Element element2 = elements.first();
				Element keyListElement = listElement.getElementsByClass("key-list").first();
				Elements houseElements = keyListElement.getElementsByClass("item-mod");
				Element listPageElement = listElement.getElementsByClass("list-page").first();
				Elements pageElement = listPageElement.getElementsByTag("a");
				map.put("list", houseElements);
				map.put("page", pageElement.size()+1);
			}catch(Exception e) {
				
			}
		}
		return map;
	}
	/**
	 * 粗略切割，不包含页数
	 * @param result
	 * @return
	 */
	public static Elements roughExcisionNPage(String result){
		Elements houseElements = null;
		if(result != null) {
			Document doc = Jsoup.parse(result);
			try {
				Element elementRoot = doc.getElementById("container");
				Element listElement = elementRoot.getElementsByClass("list-contents").first();
				Element keyListElement = listElement.getElementsByClass("key-list").first();
				houseElements = keyListElement.getElementsByClass("item-mod");
			}catch(Exception e) {
				
			}
		}
		return houseElements;
	}
	/**
	 * 获取房子的信息
	 * @param elements
	 * @return
	 */
	public static LinkedList<HousePriceRecord> exactExcision(Elements elements){
		LinkedList<HousePriceRecord> resultList = new LinkedList<HousePriceRecord>();
		for(int i = 0;i<elements.size();i++) {
			String name = null;
			String address = null;
			String state = null;
			String describe = null;
			String price = null;
			Element element = elements.get(i);
			Element infoElement = element.getElementsByClass("infos").first();
			
			try {
				Element nameElement = infoElement.getElementsByClass("lp-name").first();
				Element h3Element = nameElement.getElementsByTag("h3").first().getAllElements().first();
				name = h3Element.text();
			}catch(Exception e) {
			}
			
			try {
				Element addressElement = infoElement.getElementsByClass("address").first();
				Element spanElement = addressElement.getElementsByTag("span").first();
				address = spanElement.text();
			}catch(Exception e) {
			}
			
			Element stateElement = null;
			Element describeElement = null;
			try {
				Element tagswrapElement = infoElement.getElementsByClass("tags-wrap").first();
				Element tagpanelElement = tagswrapElement.getElementsByClass("tag-panel").first();
				Elements sdElements = tagpanelElement.getElementsByTag("i");
				if(sdElements.size() == 1) {
					describeElement = sdElements.first();
				}
				if(sdElements.size() == 2) {
					stateElement = sdElements.first();
					describeElement = sdElements.get(1);
				}
				if(stateElement != null) {
					state = stateElement.text();
				}
				if(describeElement != null) {
					describe = describeElement.text();
				}
			}catch(Exception e) {
			}
			
			try {
				Element favorposElement = element.getElementsByClass("favor-pos").first();
				Element pElement = favorposElement.getElementsByTag("p").first();
				price = pElement.text();
			}catch(Exception e) {
			}
			
			
			String adddress = address.substring(2, address.length()); 
			String  arrys = adddress.substring(0,1);
			String areaName = "";
			if(arrys.equals("西")) {
				areaName = adddress.substring(0, 3);
			}else {
				areaName = adddress.substring(0, 2);
			} 
									
			if(name != null) {
				HousePriceRecord house = new HousePriceRecord(name, address, state, describe, price,areaName);
				resultList.add(house);
			}	
		}
		return resultList;
	}

}


/** 网页结构
 * <html>
 * 	<body>
 * 		<div id="container">
 * 			<div class="list-contents">
 * 				<div class="list-results">
 * 					<div class="key-list">
 * 						<div class="item-mod"></div>
 * 					</div>
 * 					<div class="list-page">
 * 						<div class="pagination">
 * 							<a></a>
 * 						</div>
 * 					</div>
 * 				</div>
 * 			</div>
 * 		</div>
 * 	</body>
 * </html>
 */

/**
 * <div class="item-mod" data-link="" data-soj=""  rel="">
 * 	<a class="pic"><image></a>
 * 	<div class="info">
 * 		<a class"lp-name">
 * 			<h3><span class="items-name>融创白象街</span></h3>
 * 		</a>
 * 		<a class="address">
 *			<span>[&nbsp;渝中&nbsp;解放碑&nbsp;]&nbsp;凯旋路16号</span>
 *		</a>
 *		<a class="tags-wrap>
 *          <div class="tag-panel">
 *				<i class="status-icon onsale">在售</i>
 *              <i class="status-icon wuyetp">住宅</i>
 *          </div>
 *      </a>
 *	</div>
 *	<a class="favor-pos">
 *      <p class="price">最低<span>1000</span>万元/套起</p>
 *  </a>
 * </div>
 *
 */
