package com.miclan.smvc;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.jsoup.Connection;
import java.nio.charset.Charset;

@Controller
@RequestMapping(value = "/news")
public class NewsController {

	private static final Logger logger = LoggerFactory.getLogger(NewsController.class);
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.78 Safari/537.36";

	@RequestMapping(value = "/huawei", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
	public @ResponseBody String getHuawei(@RequestParam("url") String url) throws IOException {
		String indexPageUrl = "http://www.huawei.com/cn/news";
		logger.info(indexPageUrl);
		Connection conn = Jsoup.connect(indexPageUrl).timeout(30 * 1000);
		conn.header("User-Agent", USER_AGENT);
		Document doc = null;
		doc = conn.get();
		doc.charset(Charset.forName("UTF-8"));
		return doc.html();
	}
}
