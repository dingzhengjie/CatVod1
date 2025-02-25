package com.github.catvod.spider;

import android.os.Build;
import android.text.TextUtils;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.Misc;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;


/**
 * Author: @SDL
 */
public class Kunyu777 extends Spider {
    private static final String siteUrl = "http://api.kunyu77.com";

    private String uAgent = "Dalvik/2.1.0 (Linux; U; Android " + Build.VERSION.RELEASE + "; " + Build.MODEL + " Build/" + Build.ID + ")";
    private HashMap<String, String> getHeaders(String url,String key) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("user-agent", uAgent);
        String TK=Misc.MD5(url);
        headers.put("TK",TK);
        headers.put("t",key);
        return headers;
    }


    @Override
    public String homeContent(boolean filter) {
        try {
            String t = String.valueOf(System.currentTimeMillis()/ 1000);
            String url = siteUrl + "/api.php/provide/filter";
            String urlQ = "/api.php/provide/filter";
            String content = OkHttpUtil.string(url, getHeaders(urlQ,t));
            JSONObject jsonObject = new JSONObject(decryptResponse(content)).getJSONObject("data");
            Iterator<String> keys = jsonObject.keys();
            JSONArray classes = new JSONArray();
            JSONObject filterConfig = new JSONObject();
            JSONArray extendsAll = null;
            while (keys.hasNext()) {
                String typeId = keys.next();
                String typeName = jsonObject.getJSONArray(typeId).getJSONObject(0).getString("cat");
                JSONObject newCls = new JSONObject();
                newCls.put("type_id", typeId);
                newCls.put("type_name", typeName);
                classes.put(newCls);
                try {
                    if (extendsAll == null) {
                        t = String.valueOf(System.currentTimeMillis()/ 1000);
                        String filterUrl = siteUrl + "/api.php/provide/searchFilter";
                        String filterUrlQ = "/api.php/provide/searchFilter";
                        String filterUrls = filterUrl+"?type_id=0&pagenum=1&pagesize=1";
                        String filterContent = OkHttpUtil.string(filterUrls, getHeaders(filterUrlQ,t));
                        JSONObject filterObj = new JSONObject(filterContent).getJSONObject("data").getJSONObject("conditions");
                        extendsAll = new JSONArray();
                        // 年份
                        JSONObject newTypeExtend = new JSONObject();
                        newTypeExtend.put("key", "year");
                        newTypeExtend.put("name", "年份");
                        JSONArray newTypeExtendKV = new JSONArray();
                        JSONObject kv = new JSONObject();
                        kv.put("n", "全部");
                        kv.put("v", "");
                        newTypeExtendKV.put(kv);
                        kv = new JSONObject();
                        kv.put("n", "2022");
                        kv.put("v", "2022");
                        newTypeExtendKV.put(kv);
                        kv = new JSONObject();
                        kv.put("n", "2021");
                        kv.put("v", "2021");
                        newTypeExtendKV.put(kv);
                        JSONArray years = filterObj.getJSONArray("y");
                        for (int j = 0; j < years.length(); j++) {
                            JSONObject child = years.getJSONObject(j);
                            kv = new JSONObject();
                            kv.put("n", child.getString("name"));
                            kv.put("v", child.getString("value"));
                            newTypeExtendKV.put(kv);
                        }
                        newTypeExtend.put("value", newTypeExtendKV);
                        extendsAll.put(newTypeExtend);
                        // 地区
                        newTypeExtend = new JSONObject();
                        newTypeExtend.put("key", "area");
                        newTypeExtend.put("name", "地区");
                        newTypeExtendKV = new JSONArray();
                        kv = new JSONObject();
                        kv.put("n", "全部");
                        kv.put("v", "");
                        newTypeExtendKV.put(kv);
                        JSONArray areas = filterObj.getJSONArray("a");
                        for (int j = 0; j < areas.length(); j++) {
                            JSONObject child = areas.getJSONObject(j);
                            kv = new JSONObject();
                            kv.put("n", child.getString("name"));
                            kv.put("v", child.getString("value"));
                            newTypeExtendKV.put(kv);
                        }
                        newTypeExtend.put("value", newTypeExtendKV);
                        extendsAll.put(newTypeExtend);
                        // 类型
                        newTypeExtend = new JSONObject();
                        newTypeExtend.put("key", "category");
                        newTypeExtend.put("name", "类型");
                        newTypeExtendKV = new JSONArray();
                        kv = new JSONObject();
                        kv.put("n", "全部");
                        kv.put("v", "");
                        newTypeExtendKV.put(kv);
                        JSONArray scat = filterObj.getJSONArray("scat");
                        for (int j = 0; j < scat.length(); j++) {
                            JSONObject child = scat.getJSONObject(j);
                            kv = new JSONObject();
                            kv.put("n", child.getString("name"));
                            kv.put("v", child.getString("value"));
                            newTypeExtendKV.put(kv);
                        }
                        newTypeExtend.put("value", newTypeExtendKV);
                        extendsAll.put(newTypeExtend);
                    }
                    if (extendsAll != null && extendsAll.length() > 0) {
                        filterConfig.put(typeId, extendsAll);
                    }
                } catch (Exception e) {
                    SpiderDebug.log(e);
                }
            }
            JSONObject result = new JSONObject();
            result.put("class", classes);
            if (filter) {
                result.put("filters", filterConfig);
            }
            return result.toString();
        } catch (Throwable th) {

        }
        return "";
    }

    @Override
    public String homeVideoContent() {
        try {
            JSONArray videos = new JSONArray();
            try {
                String t = String.valueOf(System.currentTimeMillis()/ 1000);
                String url = siteUrl + "/api.php/provide/homeBlock";
                String urls = url+"?type_id=0&pcode=010110002&version=2.0.4&devid=4ac3fe96a6133de96904b8d3c8cfe16d&package=com.sevenVideo.app.android&sys=android&sysver=7.1.2&brand=realme&model=RMX1931&sj="+t;
                String urlm ="/api.php/provide/homeBlockrealme4ac3fe96a6133de96904b8d3c8cfe16dRMX1931com.sevenVideo.app.android010110002"+t+"android7.1.22.0.4"+t+"XSpeUFjJ";
                String content = OkHttpUtil.string(urls, getHeaders(urlm,t));
                JSONObject jsonObject = new JSONObject(decryptResponse(content));
                JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("blocks");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject blockObj = jsonArray.getJSONObject(i);
                    String blockName = blockObj.getString("block_name");
                    if (!blockName.startsWith("热播")) {
                        continue;
                    }
                    JSONArray contents = blockObj.getJSONArray("contents");
                    for (int j = 0; j < contents.length() && j < 3; j++) {
                        JSONObject vObj = contents.getJSONObject(j);
                        JSONObject v = new JSONObject();
                        v.put("vod_id", vObj.getString("id"));
                        v.put("vod_name", vObj.getString("title"));
                        v.put("vod_pic", vObj.getString("videoCover"));
                        v.put("vod_remarks", vObj.getString("msg"));
                        videos.put(v);
                    }
                }
            } catch (Exception e) {

            }
            JSONObject result = new JSONObject();
            result.put("list", videos);
            return result.toString();
        } catch (Throwable th) {

        }
        return "";
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        try {
            String t = String.valueOf(System.currentTimeMillis()/ 1000);
            String url = siteUrl + "/api.php/provide/searchFilter?type_id=" + tid + "&pagenum=" + pg + "&pagesize=24";
            Set<String> keys = extend.keySet();
            for (String key : keys) {
                String val = extend.get(key).trim();
                if (val.length() == 0)
                    continue;
                url += "&" + key + "=" + URLEncoder.encode(val);
            }
            String content = OkHttpUtil.string(url, getHeaders(url,t));
            JSONObject dataObject = new JSONObject(decryptResponse(content)).getJSONObject("data");
            JSONArray jsonArray = dataObject.getJSONArray("result");
            JSONArray videos = new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject vObj = jsonArray.getJSONObject(i);
                JSONObject v = new JSONObject();
                v.put("vod_id", vObj.getString("id"));
                v.put("vod_name", vObj.getString("title"));
                v.put("vod_pic", vObj.getString("videoCover"));
                v.put("vod_remarks", vObj.getString("msg"));
                videos.put(v);
            }
            JSONObject result = new JSONObject();
            int limit = 24;
            int page = Integer.parseInt(dataObject.getString("page"));
            int total = dataObject.getInt("total");
            int pageCount = dataObject.getInt("pagesize");
            result.put("page", page);
            result.put("pagecount", pageCount);
            result.put("limit", limit);
            result.put("total", total);
            result.put("list", videos);
            return result.toString();
        } catch (Throwable th) {

        }
        return "";
    }

    @Override
    public String detailContent(List<String> ids) {
        try {
            String t = String.valueOf(System.currentTimeMillis()/ 1000);
            String url = siteUrl + "/api.php/provide/videoDetail";
            String urlx=url+"?ids=" + ids.get(0)+"&pcode=010110002&version=2.0.4&devid=4ac3fe96a6133de96904b8d3c8cfe16d&package=com.sevenVideo.app.android&sys=android&sysver=7.1.2&brand=realme&model=RMX1931&sj="+t;
            String urlp = "/api.php/provide/videoDetailrealme4ac3fe96a6133de96904b8d3c8cfe16d"+ids.get(0)+"RMX1931com.sevenVideo.app.android010110002"+t+"android7.1.22.0.4"+t+"XSpeUFjJ";
            String content = OkHttpUtil.string(urlx, getHeaders(urlp,t));
            JSONObject dataObject = new JSONObject(decryptResponse(content));
            JSONObject vObj = dataObject.getJSONObject("data");
            JSONObject result = new JSONObject();
            JSONArray list = new JSONArray();
            JSONObject vodAtom = new JSONObject();
            String title = vObj.getString("videoName");
            vodAtom.put("vod_id", vObj.getString("id"));
            vodAtom.put("vod_name", title);
            vodAtom.put("vod_pic", vObj.getString("videoCover"));
            vodAtom.put("type_name", vObj.getString("subCategory"));
            vodAtom.put("vod_year", vObj.getString("year"));
            vodAtom.put("vod_area", vObj.getString("area"));
            vodAtom.put("vod_remarks", vObj.getString("msg"));
            vodAtom.put("vod_actor", vObj.getString("actor"));
            vodAtom.put("vod_director", vObj.getString("director"));
            vodAtom.put("vod_content", vObj.getString("brief").trim());
            t = String.valueOf(System.currentTimeMillis()/ 1000) ;
            url = siteUrl + "/api.php/provide/videoPlaylist";
            urlx =url+"?ids=" + ids.get(0)+"&pcode=010110002&version=2.0.4&devid=4ac3fe96a6133de96904b8d3c8cfe16d&package=com.sevenVideo.app.android&sys=android&sysver=7.1.2&brand=realme&model=RMX1931&sj="+t;
            urlp = "/api.php/provide/videoPlaylistrealme4ac3fe96a6133de96904b8d3c8cfe16d"+ids.get(0)+"RMX1931com.sevenVideo.app.android010110002"+t+"android7.1.22.0.4"+t+"XSpeUFjJ";
            content = OkHttpUtil.string(urlx, getHeaders(urlp,t));
            JSONArray episodes = new JSONObject(content).getJSONObject("data").getJSONArray("episodes");
            LinkedHashMap<String, ArrayList<String>> playlist = new LinkedHashMap<>();
            for (int i = 0; i < episodes.length(); i++) {
                JSONArray playurls = episodes.getJSONObject(i).getJSONArray("playurls");
                for (int j = 0; j < playurls.length(); j++) {
                    JSONObject purl = playurls.getJSONObject(j);
                    String from = purl.getString("playfrom");
                    ArrayList<String> urls = playlist.get(from);
                    if (urls == null) {
                        urls = new ArrayList<>();
                        playlist.put(from, urls);
                    }
                    String name = purl.getString("title").replace(title, "").trim();
                    if (name.isEmpty()) {
                        name = (i + 1) + "";
                    }
                    String pid = purl.getString("playurl");
                    urls.add(name + "$" + pid);
                }
            }
            String vod_play_from = TextUtils.join("$$$", playlist.keySet());
            StringBuilder sb = new StringBuilder();
            Iterator<ArrayList<String>> iter = playlist.values().iterator();
            short fromSize = (short) playlist.size();
            while (iter.hasNext()) {
                fromSize--;
                ArrayList<String> urls = iter.next();
                sb.append(TextUtils.join("#", urls));
                if (fromSize > 0)
                    sb.append("$$$");
            }
            vodAtom.put("vod_play_from", vod_play_from);
            vodAtom.put("vod_play_url", sb.toString());
            list.put(vodAtom);
            result.put("list", list);
            return result.toString();
        } catch (Throwable th) {

        }
        return "";
    }

    @Override
    public String playerContent(String flag, String id, List<String> list) {
        JSONObject result = new JSONObject();

        try {

            String t = String.valueOf(System.currentTimeMillis()/ 1000);
            String url = siteUrl + "/api.php/provide/parserDirectUrl";
            String urls=url+"?url=" + id+"&retryNum=0&pcode=010110002&version=2.0.4&devid=4ac3fe96a6133de96904b8d3c8cfe16d&package=com.sevenVideo.app.android&sys=android&sysver=7.1.2&brand=realme&model=RMX1931&sj="+t;
            String urlm = "/api.php/provide/parserUrlrealme4ac3fe96a6133de96904b8d3c8cfe16dRMX1931com.sevenVideo.app.android010110002"+id+"android7.1.2"+id+"2.0.4"+t+"XSpeUFjJ";
            String content = OkHttpUtil.string(urls, getHeaders(urlm,t));
            JSONObject dataObj = new JSONObject(decryptResponse(content)).getJSONObject("data");
            //JSONObject playHeader = dataObj.optJSONObject("playHeader");
            String jxUrl = dataObj.getString("url");
            HashMap hashMap = new HashMap();
            hashMap.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36 Edg/107.0.1418.52");
            //String contents = OkHttpUtil.string(decryptResponse(jxUrl),hashMap);
            JSONObject tt = new JSONObject(OkHttpUtil.string(decryptResponse(jxUrl),hashMap));


            result.put("parse", 0);
            result.put("playUrl", "");
            result.put("url", tt.getString("url"));
        } catch (Exception e) {
            SpiderDebug.log(e);
        }

        return result.toString();
    }

    @Override
    public String searchContent(String key, boolean quick) {
        try {
            String t = String.valueOf(System.currentTimeMillis()/ 1000);
            String url = siteUrl + "/api.php/provide/searchVideo";
            String urls=url+"?pcode=010110002&version=2.0.4&devid=4ac3fe96a6133de96904b8d3c8cfe16d&package=com.sevenVideo.app.android&sys=android&sysver=7.1.2&brand=realme&model=RMX1931&sj="+t+"&searchName="+ key+"&pg=1";

            String urlm = "/api.php/provide/searchVideorealme4ac3fe96a6133de96904b8d3c8cfe16dRMX1931com.sevenVideo.app.android0101100021"+key+t+"android7.1.202.0.4"+t+"XSpeUFjJ";
            HashMap hashMap = new HashMap();
            hashMap.put("User-Agent", "okhttp/3.12.0");
            String Ks=Misc.MD5(urlm);
            hashMap.put("TK",Ks);
            hashMap.put("t",t);
            String content = OkHttpUtil.string(urls, hashMap);
            JSONObject dataObject = new JSONObject(decryptResponse(content));
            JSONArray jsonArray = dataObject.getJSONArray("data");
            JSONArray videos = new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject vObj = jsonArray.getJSONObject(i);
                JSONObject v = new JSONObject();
                String title = vObj.getString("videoName");
                if (!title.contains(key))
                    continue;
                v.put("vod_id", vObj.getString("id"));
                v.put("vod_name", title);
                v.put("vod_pic", vObj.getString("videoCover"));
                v.put("vod_remarks", vObj.getString("msg"));
                videos.put(v);
            }
            JSONObject result = new JSONObject();
            result.put("list", videos);
            return result.toString();
        } catch (Throwable th) {

        }
        return "";
    }

    protected String decryptResponse(String src) {
        return src;
    }

}

