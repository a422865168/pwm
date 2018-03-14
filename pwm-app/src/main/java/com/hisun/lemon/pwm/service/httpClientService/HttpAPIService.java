package com.hisun.lemon.pwm.service.httpClientService;


import java.util.Map;

import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gexin.fastjson.JSONObject;


@Component
public class HttpAPIService {
    private static final Logger logger = LoggerFactory.getLogger(HttpAPIService.class);

    @Autowired
    private CloseableHttpClient httpClient;

    @Autowired
    private RequestConfig config;

    public HttpResult doPostAsJson(String url, String jsonData, Map<String,String> headerMap) throws Exception {
        logger.info("###api post to:"+url);
        logger.info("###api post head:"+ JSONObject.toJSONString(headerMap));
        logger.info("###api post body:"+jsonData);
        long start = System.currentTimeMillis();

        // 声明http请求
        HttpPost httpPost = new HttpPost(url);

        // 加入配置信息
        httpPost.setConfig(config);
        StringEntity stringentity = new StringEntity(jsonData,
                ContentType.create("application/json", "UTF-8"));

        httpPost.setEntity(stringentity);

        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            httpPost.setHeader(entry.getKey().toString(), entry.getValue().toString());
        }
        // String uuid=UUID.randomUUID().toString();

        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        long end = System.currentTimeMillis();
        // logger.info("sendUUID="+uuid);
        logger.info("===>Response Header:");

        for(Header hearder: httpResponse.getAllHeaders()){
            System.out.println(hearder.getName()+"="+hearder.getValue());
        }
        logger.info("####api post耗时："+(end-start)+"毫秒,httpCode="+httpResponse.getStatusLine().getStatusCode());

        return new HttpResult(httpResponse.getStatusLine().getStatusCode(), EntityUtils.toString(
                httpResponse.getEntity(), "UTF-8"));

    }

}