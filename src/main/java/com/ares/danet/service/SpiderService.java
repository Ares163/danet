package com.ares.danet.service;
import com.ares.danet.bean.UrlData;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liang
 * @date 2020-10-13
 */
@Service
public class SpiderService {
    @Value("${myHost}")
    private String myHost;

    private String remoteHost="https://m.booktxt.net";
    private String remotePath = "/wapbook/20832.html";
    private String remoteContentUrl = remoteHost + "/wapbook/";

    public static void main(String args[]) throws Exception {

//        CloseableHttpClient httpclient = HttpClients.createDefault();
//        HttpGet httpGet = new HttpGet("https://m.booktxt.net/wapbook/20832.html");
////        HttpGet httpGet = new HttpGet("https://m.booktxt.net/wapbook/20832_581795011.html");
//
//        CloseableHttpResponse response = httpclient.execute(httpGet);
////        response = httpclient.execute(httpGet);
//        if (response.getStatusLine().getStatusCode() == 200) {
//            String content = EntityUtils.toString(response.getEntity(), "GBK");
//            Document jsoup = Jsoup.parse(content);
//            String url = jsoup.getElementsByClass("book_last").get(0).getElementsByTag("dd").get(0).getElementsByTag("a").get(0).getElementsByAttribute("href").get(0).attributes().html();
////            jsoup.getElementById("chaptercontent").html()
//            System.out.println("xx");
//        }

        SpiderService service = new SpiderService();
        List<String> news = service.news();
        System.out.println(news);

//        System.out.println(service.getAllContent("https://m.booktxt.net/wapbook/20832_611515129.html"));

//        System.out.println(service.addIndex("https://m.booktxt.net/wapbook/20832_611515129.html", 2));
    }

    public String content(String id) {
        String url = remoteContentUrl + id + ".html";
        try {
            return getAllContent(url);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<UrlData> index() {
        List<UrlData> list = new ArrayList<>();
        List<String> strList = news();
        for(String str: strList) {
            String[] arr = str.split("~");
            UrlData urlData = new UrlData();
            urlData.setName(arr[0]);
            int startIndex = arr[1].lastIndexOf("/");
            int endIndex = arr[1].lastIndexOf(".");
            String id = arr[1].substring(startIndex + 1, endIndex);
            urlData.setUrl(myHost + id);
            list.add(urlData);
        }
        return list;
    }

    public List<String> news(){
        String base= remoteHost;
        String index = remoteHost + remotePath;
        String content = null;
        try {
            content = httpGet(index);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        Document jsoup = Jsoup.parse(content);
        List<String> newsList = new ArrayList<>();
        List<Element> elements = jsoup.getElementsByClass("book_last").get(0).getElementsByTag("dd");
        for (Element e : elements) {
            Element a = e.getElementsByTag("a").get(0).getElementsByAttribute("href").get(0);
            String url = base + a.attributes().asList().get(0).getValue();
            String name = a.html();
            newsList.add(name + "~" + url);
        }
        return newsList;
    }


    private String httpGet(String url) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() == 200) {
            String content = EntityUtils.toString(response.getEntity(), "GBK");
            return content;
        }
        return null;
    }


    public String getAllContent(String url) throws Exception {
        String ret = "";
        String content = getContent(url);
        int index = 1;
        while(true) {
            index++;
            ret += content;
            if (!content.contains("本章未完,请翻页")) {
                break;
            }
            content = getContent(addIndex(url, index));
        }
        return ret;
    }

    public String getContent(String url) throws Exception{
        String content = httpGet(url);
        Document jsoup = Jsoup.parse(content);
        String ret = jsoup.getElementById("chaptercontent").html();
        return ret;
    }


    public String addIndex(String url, Integer index) {
        Integer start = url.indexOf(".html");
        return url.substring(0, start) + "_" + index + ".html";
    }



}