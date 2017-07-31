package com.miclan.smvc;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping(value = "/news")
public class NewsController {

	private static final Logger logger = LoggerFactory.getLogger(NewsController.class);
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.78 Safari/537.36";
	private static final String KEY_FOR_QUERY = "06FC99B42D4707B201978C830F060EEA";
	private static final String KEY_FOR_ADMIN = "42E0F87885ED972027A96FEDEC0E4719";

	@RequestMapping(value = "/huawei", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
	public @ResponseBody String getHuawei(@RequestParam(value = "url", required = false) String paramUrl)
			throws IOException {
		String indexPageUrl = "http://www.huawei.com/cn/news";
		if (paramUrl != null) {
			// http://www.huawei.com/cn/news?d=ws&hasCount=25
			// 25,50,75,...
			indexPageUrl = paramUrl;
		}
		Connection connIndex = Jsoup.connect(indexPageUrl).timeout(30 * 1000);
		connIndex.header("User-Agent", USER_AGENT);
		JSONArray jaDocs = new JSONArray();
		Set<String> set = new HashSet<String>();
		for (Element link : connIndex.get().select("a[href]")) {
			// http://www.huawei.com/cn/news/2017/7/Huawei-Cloud-trustcloud-ecloud
			String url = link.absUrl("href");
			if (url.matches("http[s]{0,1}://www\\.huawei\\.com/cn/news/[\\d]{4}/[\\d]{1,2}/[^/]+")) {
				if (set.contains(url)) {
					logger.info("SKIP-A " + url);
					continue;
				} else if (existsInAzure(url)) {
					logger.info("SKIP-B " + url);
					continue;
				} else {
					set.add(url);
				}
				logger.info("STARTING " + url);

				try {
					Document doc = Jsoup.connect(url).timeout(30 * 1000).get();
					String title = doc.select("[class='row detail-page']").first().select("h1").text();
					// String html =
					// doc.select("[class='text-indent']").first().html();
					String text = doc.select("[class='text-indent']").first().text();
					JSONObject joDoc = new JSONObject();
					joDoc.put("@search.action", "upload");
					joDoc.put("id", Integer.toString(url.hashCode()));
					joDoc.put("url", url);
					joDoc.put("title", title);
					// joDoc.put("html", html);
					joDoc.put("text", text);
					jaDocs.add(joDoc);
					logger.info("SUCCESS");
				} catch (Exception e) {
					logger.warn("FAILED");
				}
			}
		}
		return indexToAzureSearch(jaDocs);
	}

	@RequestMapping(value = "/sina", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
	public @ResponseBody String getSina(@RequestParam(value = "url", required = false) String paramUrl)
			throws IOException {
		String indexPageUrl = "http://tech.sina.com.cn/news/";
		if (paramUrl != null) {
			indexPageUrl = paramUrl;
		}
		Connection connIndex = Jsoup.connect(indexPageUrl).timeout(30 * 1000);
		connIndex.header("User-Agent", USER_AGENT);
		JSONArray jaDocs = new JSONArray();
		Set<String> set = new HashSet<String>();
		for (Element link : connIndex.get().select("a[href]")) {
			// http://tech.sina.com.cn/i/2017-07-30/doc-ifyinvwu3572832.shtml
			// http://tech.sina.com.cn/mobile/n/n/2017-07-29/doc-ifyinvwu3289511.shtml
			String url = link.absUrl("href");
			if (url.matches(
					"http[s]{0,1}://[a-z\\.]+\\.sina\\.com\\.cn/[A-Za-z/]+/[\\d]{4}\\-[\\d]{2}\\-[\\d]{2}/[^/]+\\.shtml")) {
				if (set.contains(url)) {
					logger.info("SKIP-A " + url);
					continue;
				} else if (existsInAzure(url)) {
					logger.info("SKIP-B " + url);
					continue;
				} else {
					set.add(url);
				}
				logger.info("STARTING " + url);

				try {
					Document doc = Jsoup.connect(url).timeout(30 * 1000).get();
					String title = doc.select("[id='main_title']").first().text();
					// String html =
					// doc.select("[id='artibody']").first().html();
					String text = doc.select("[id='artibody']").first().text();
					JSONObject joDoc = new JSONObject();
					joDoc.put("@search.action", "upload");
					joDoc.put("id", Integer.toString(url.hashCode()));
					joDoc.put("url", url);
					joDoc.put("title", title);
					// joDoc.put("html", html);
					joDoc.put("text", text);
					jaDocs.add(joDoc);
					logger.info("SUCCESS");
				} catch (Exception e) {
					try {
						Document doc = Jsoup.connect(url).timeout(30 * 1000).get();
						String title = doc.select("[id='artibodyTitle']").first().text();
						// String html =
						// doc.select("[id='artibody']").first().html();
						String text = doc.select("[id='artibody']").first().text();
						JSONObject joDoc = new JSONObject();
						joDoc.put("@search.action", "upload");
						joDoc.put("id", Integer.toString(url.hashCode()));
						joDoc.put("url", url);
						joDoc.put("title", title);
						// joDoc.put("html", html);
						joDoc.put("text", text);
						jaDocs.add(joDoc);
						logger.info("SUCCESS");
					} catch (Exception e1) {
						logger.warn("FAILED");
					}
				}
			}
		}
		return indexToAzureSearch(jaDocs);
	}

	@RequestMapping(value = "/qq", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
	public @ResponseBody String getQq(@RequestParam(value = "url", required = false) String paramUrl)
			throws IOException {
		String indexPageUrl = "http://tech.qq.com/";
		if (paramUrl != null) {
			indexPageUrl = paramUrl;
		}
		Connection connIndex = Jsoup.connect(indexPageUrl).timeout(30 * 1000);
		connIndex.header("User-Agent", USER_AGENT);
		JSONArray jaDocs = new JSONArray();
		Set<String> set = new HashSet<String>();
		for (Element link : connIndex.get().select("a[href]")) {
			// http://tech.qq.com/a/20170727/034864.htm
			String url = link.absUrl("href");
			if (url.matches("http[s]{0,1}://[a-z\\.]+\\.qq\\.com/[A-Za-z/]+/[\\d]{8}/[^/]+\\.htm")) {
				if (set.contains(url)) {
					logger.info("SKIP-A " + url);
					continue;
				} else if (existsInAzure(url)) {
					logger.info("SKIP-B " + url);
					continue;
				} else {
					set.add(url);
				}
				logger.info("STARTING " + url);
				try {
					Document doc = Jsoup.connect(url).timeout(30 * 1000).get();
					String title = doc.select("[class='qq_article']").first().select("h1").first().text();
					// String html =
					// doc.select("[class='qq_article']").first().select("[class='bd']").first().html();
					String text = doc.select("[class='qq_article']").first().select("[class='bd']").first().text();
					JSONObject joDoc = new JSONObject();
					joDoc.put("@search.action", "upload");
					joDoc.put("id", Integer.toString(url.hashCode()));
					joDoc.put("url", url);
					joDoc.put("title", title);
					// joDoc.put("html", html);
					joDoc.put("text", text);
					jaDocs.add(joDoc);
					logger.info("SUCCESS");
				} catch (Exception e) {
					logger.warn("FAILED");
				}
			}
		}
		return indexToAzureSearch(jaDocs);
	}

	private String indexToAzureSearch(JSONArray jaDocs) throws ClientProtocolException, IOException {

		// https://lanchao-search.search.windows.net/indexes/web-news/docs/index?api-version=2016-09-01
		// Content-Type application/json
		// api-key 42E0F87885ED972027A96FEDEC0E4719
		// {
		// "value": [
		// {
		// "@search.action": "upload",
		// "id": "test2",
		// "url":"test2 url",
		// "title":"test2 标题",
		// "text":"test2 文本"
		// }
		// ]
		// }
		if (jaDocs.size() == 0) {
			return "No NEW docs created.";
		}
		JSONObject joAzure = new JSONObject();
		joAzure.put("value", jaDocs);

		logger.info(joAzure.toJSONString());
		Response response = Request
				.Post("https://lanchao-search.search.windows.net/indexes/web-news/docs/index?api-version=2016-09-01")
				.addHeader("Content-Type", "application/json").addHeader("api-key", KEY_FOR_ADMIN)
				.bodyString(joAzure.toJSONString(), ContentType.APPLICATION_JSON).execute();

		return response.returnContent().asString();
	}

	private boolean existsInAzure(String url) {
		// https://lanchao-search.search.windows.net/indexes/web-news/docs/1925407681?api-version=2016-09-01
		try {
			Response response = Request
					.Get("https://lanchao-search.search.windows.net/indexes/web-news/docs/" + url.hashCode()
							+ "?api-version=2016-09-01")
					.addHeader("Content-Type", "application/json").addHeader("api-key", KEY_FOR_QUERY).execute();
			if (200 == response.returnResponse().getStatusLine().getStatusCode()) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			return false;
		}
	}
}
