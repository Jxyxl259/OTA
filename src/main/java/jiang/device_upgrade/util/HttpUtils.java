package jiang.device_upgrade.util;


import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.http.HttpEntity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author:
 * @create: 2018-04-26 09:56
 */
public class HttpUtils {


    private static Logger LOG = LoggerFactory.getLogger(HttpUtils.class);


    /**
     *
     * @Description: post 提交
     * @author yuzj7@lenovo.com
     * @date 2015年5月15日 上午10:41:49
     * @param url
     * @param params
     * @return
     */
    public static String postStr(String url, Map<String, String> params){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        List<org.apache.http.NameValuePair> nvps = new ArrayList<>();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry
                        .getValue()));
            }
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            LOG.error(""+e);
        }
        String line = null;
        String str="";
        BufferedReader reader = null;
        try {
            HttpResponse response=httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
            // 显示结果
            while ((line = reader.readLine()) != null) {
                str+=line;
            }
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
        }finally{
            try{
                if(httpclient != null){
                    httpclient.close();
                }
                if(reader != null){
                    reader.close();
                }
            }catch(Exception e){
                LOG.error("", e);
            }
        }
        return str;
    }

}































