package com.demo.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.demo.utils.DataList;
import com.demo.utils.HttpUtils;

public class DistrictSearchThread extends Thread {
	public void run() {
		ExecutorService pool = Executors.newFixedThreadPool(5);
		boolean flag = true;
		while (flag) {
			String url = null;
			synchronized (DataList.cityList) {
				try {
					url = DataList.cityList.getFirst();
					DataList.cityList.removeFirst();
				} catch (Exception e) {
				}
				if (url == null) {
					if (DataList.cityFlag == false) {
						flag = false;
					} else {
						try {
							DataList.cityList.wait(1000);// 防止最后一直等待，没有唤醒他
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			pool.execute(new SearchUtil(url));
		}
		pool.shutdown();
		while (true) {
			if (pool.isTerminated()) {
				DataList.districtFlag = false;
				System.out.println("地区搜索完成");
				break;
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	class SearchUtil extends Thread {
		private String url;

		public SearchUtil(String url) {
			super();
			this.url = url;
		}

		@Override
		public void run() {
			String result = null;
			// 当线程是刚唤醒的那str是null
			if (url != null) {
				result = HttpUtils.CreatHttpGet(url);
				if (result != null) {
					Document doc = Jsoup.parse(result);
					try {
						Element contentElement = doc.getElementById("content_Rd1");
						Element detailsfloat_lElement = contentElement.getElementsByClass("details float_l").get(1);
						Element areasElement = detailsfloat_lElement.getElementsByClass("areas").first();
						Elements aElements = areasElement.getElementsByTag("a");
						for (int i = 0; i < aElements.size(); i++) {
							Element cityElement = aElements.get(i);
							String districtUrl = cityElement.absUrl("href");
							synchronized (DataList.districtList) {
								DataList.districtList.add(districtUrl);
								if (DataList.districtList.size() == 1) {
									DataList.districtList.notifyAll();
								}
							}
						}
					} catch (Exception e) {
						 System.out.println(Thread.currentThread().getName() + "=====区域:" + url + "无相关数据");
					}
				}
			}
		}
	}
}


/**  网页结构
 * <html>
 * 	<body>
 * 		<div id="content">
 * 			<div class="left-cont fl">
 * 				<div class="buy-house tab-contents" id="content_Rd1">
 * 					<div class="clearfix">
 * 						<div class="details float_l"></div>
 * 						<div class="details float_l">
 * 							<div class="areas">
 * 								<a> </a>
 * 							</div>
 * 							<div class="prices"></div>
 * 						</div>
 * 					</div>
 * 				</div>
 * 			</div>
 * 		</div>
 * 	</body>
 * </html>
 * 
 */