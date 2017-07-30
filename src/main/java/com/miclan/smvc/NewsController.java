package com.miclan.smvc;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping(value = "/news")
public class NewsController {

	private static final Logger logger = LoggerFactory.getLogger(NewsController.class);
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.78 Safari/537.36";

	@RequestMapping(value = "/huawei", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
	public @ResponseBody String getHuawei() throws IOException {
		String indexPageUrl = "http://www.huawei.com/cn/news";
		Connection connIndex = Jsoup.connect(indexPageUrl).timeout(30 * 1000);
		connIndex.header("User-Agent", USER_AGENT);
		JSONArray jaDocs = new JSONArray();
		Set<String> set = new HashSet<String>();
		for (Element link : connIndex.get().select("a[href]")) {
			// http://www.huawei.com/cn/news/2017/7/Huawei-Cloud-trustcloud-ecloud
			String url = link.absUrl("href");
			if (url.matches("http[s]{0,1}://www\\.huawei\\.com/cn/news/[\\d]{4}/[\\d]{1,2}/[^/]+")) {
				if (set.contains(url)) {
					continue;
				} else {
					set.add(url);
				}
				logger.info("STARTING " + url);
				Document doc = Jsoup.connect(url).timeout(30 * 1000).get();
				try {
					String title = doc.select("[class='row detail-page']").first().select("h1").text();
					String html = doc.select("[class='text-indent']").first().html();
					String text = doc.select("[class='text-indent']").first().text();
					JSONObject joDoc = new JSONObject();
					joDoc.put("url", url);
					joDoc.put("title", title);
					joDoc.put("html", html);
					joDoc.put("text", text);
					jaDocs.add(joDoc);
					logger.info("SUCCESS");
				} catch (Exception e) {
					logger.warn("FAILED");
				}
			}
		}
		return jaDocs.toJSONString();
	}

	@RequestMapping(value = "/sina", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
	public @ResponseBody String getSina() throws IOException {
		String indexPageUrl = "http://tech.sina.com.cn/news/";
		Connection connIndex = Jsoup.connect(indexPageUrl).timeout(30 * 1000);
		connIndex.header("User-Agent", USER_AGENT);
		JSONArray jaDocs = new JSONArray();
		Set<String> set = new HashSet<String>();
		for (Element link : connIndex.get().select("a[href]")) {
			// http://tech.sina.com.cn/i/2017-07-30/doc-ifyinvwu3572832.shtml
			// http://tech.sina.com.cn/mobile/n/n/2017-07-29/doc-ifyinvwu3289511.shtml
			String url = link.absUrl("href");
			if (url.matches(
					"http[s]{0,1}://tech\\.sina\\.com\\.cn/[A-Za-z/]+/[\\d]{4}\\-[\\d]{2}\\-[\\d]{2}/[^/]+\\.shtml")) {
				if (set.contains(url)) {
					continue;
				} else {
					set.add(url);
				}
				logger.info("STARTING " + url);
				Document doc = Jsoup.connect(url).timeout(30 * 1000).get();
				try {
					String title = doc.select("[id='main_title']").first().text();
					String html = doc.select("[id='artibody']").first().html();
					String text = doc.select("[id='artibody']").first().text();
					JSONObject joDoc = new JSONObject();
					joDoc.put("url", url);
					joDoc.put("title", title);
					joDoc.put("html", html);
					joDoc.put("text", text);
					jaDocs.add(joDoc);
					logger.info("SUCCESS");
				} catch (Exception e) {
					String title = doc.select("[id='artibodyTitle']").first().text();
					String html = doc.select("[id='artibody']").first().html();
					String text = doc.select("[id='artibody']").first().text();
					JSONObject joDoc = new JSONObject();
					joDoc.put("url", url);
					joDoc.put("title", title);
					joDoc.put("html", html);
					joDoc.put("text", text);
					jaDocs.add(joDoc);
					logger.info("SUCCESS");
				} finally {
					logger.info("FAILED");
				}
			}
		}
		return jaDocs.toJSONString();
	}

	@RequestMapping(value = "/qq", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
	public @ResponseBody String getQq() throws IOException {
		String indexPageUrl = "http://tech.qq.com/";
		Connection connIndex = Jsoup.connect(indexPageUrl).timeout(30 * 1000);
		connIndex.header("User-Agent", USER_AGENT);
		JSONArray jaDocs = new JSONArray();
		Set<String> set = new HashSet<String>();
		for (Element link : connIndex.get().select("a[href]")) {
			// http://tech.qq.com/a/20170727/034864.htm
			String url = link.absUrl("href");
			if (url.matches("http[s]{0,1}://[a-z\\.]+\\.qq\\.com/[A-Za-z/]+/[\\d]{8}/[^/]+\\.htm")) {
				if (set.contains(url)) {
					continue;
				} else {
					set.add(url);
				}
				logger.info("STARTING " + url);
				Document doc = Jsoup.connect(url).timeout(30 * 1000).get();
				try {
					String title = doc.select("[class='qq_article']").first().select("h1").first().text();
					String html = doc.select("[class='qq_article']").first().select("[class='bd']").first().html();
					String text = doc.select("[class='qq_article']").first().select("[class='bd']").first().text();
					JSONObject joDoc = new JSONObject();
					joDoc.put("url", url);
					joDoc.put("title", title);
					joDoc.put("html", html);
					joDoc.put("text", text);
					jaDocs.add(joDoc);
					logger.info("SUCCESS");
				} catch (Exception e) {
					logger.warn("FAILED");
				}
			}
		}
		return jaDocs.toJSONString();
	}
}
