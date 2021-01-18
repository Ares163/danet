package com.ares.danet.service;
import com.ares.danet.bean.UrlData;
import org.apache.commons.lang3.StringUtils;
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

    @Value("${myHost1}")
    private String myHost1;

    private String remoteHost="https://m.booktxt.net";
    private String remotePath = "/wapbook/20832.html";
    private String remoteContentUrl = remoteHost + "/wapbook/";

    private String host1 = "https://wap.2txt.cc";

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
        List<String> news1 = service.news1();
        System.out.println(news1);
//        String c = service.getAllContent("https://wap.2txt.cc/99/99533/71519131.html");
//        System.out.println(c);

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

    public String content1(String id) {
        String url = host1 +"/" + id + ".html";
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

    public List<UrlData> index1() {
        List<UrlData> list = new ArrayList<>();
        List<String> strList = news1();
        for(String str: strList) {
            String[] arr = str.split("~");
            UrlData urlData = new UrlData();
            urlData.setName(arr[0]);
            int startIndex = arr[1].indexOf("/99");
            int endIndex = arr[1].lastIndexOf(".");
            String id = arr[1].substring(startIndex + 1, endIndex);
            urlData.setUrl(myHost1 + id);
            list.add(urlData);
        }
        return list;
    }

    public List<String> news1() {
        String content = null;
        List<String> newsList = new ArrayList<>();
        try {
            content = httpGet("https://wap.2txt.cc/book/99533.html");
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        Document jsoup = Jsoup.parse(content);
        List<Element> elements = jsoup.getElementsByClass("zxjz fk").get(0).getElementsByTag("ul").get(0).getElementsByTag("a");
        for (Element e : elements) {
            Element a = e.getElementsByTag("a").get(0).getElementsByAttribute("href").get(0);
            String url =  host1 + a.attributes().asList().get(0).getValue();
            String name = a.html();
            newsList.add(name + "~" + url);
        }
        return newsList;
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
        String content = null;
        if (url.contains(remoteHost)) {
            content = getContent(url);
        } else {
            content = getContent1(url);
        }
        int index = 1;
        while(true) {
            index++;
            ret += content;
            if (url.contains(remoteHost)) {
                if (!content.contains("本章未完,请翻页")) {
                    break;
                }
                content = getContent(addIndex(url, index));
            } else {
                if (StringUtils.isBlank(content)) {
                    break;
                }
                content = getContent1(addIndex(url, index));
            }
        }
        return ret;
    }

    public String getContent(String url) throws Exception{
        String content = httpGet(url);
        System.out.println(url);
        Document jsoup = Jsoup.parse(content);
        String ret = jsoup.getElementById("chaptercontent").html();
        return ret;
    }

    public String getContent1(String url) throws Exception{
        String content = httpGet(url);
        Document jsoup = Jsoup.parse(content);
        String ret = jsoup.getElementById("nr").html();
        int index = ret.lastIndexOf("p style=\"color");
        if (index < 10) {
            return "";
        }
        return ret.substring(0, index);
    }

    public String addIndex(String url, Integer index) {
        Integer start = url.indexOf(".html");
        return url.substring(0, start) + "_" + index + ".html";
    }



}