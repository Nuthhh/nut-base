package com.nut.base.core.util;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import okhttp3.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: han jianguo
 * @Date: 2019/12/13 10:56
 * @Description:
 **/
public class HttpUtil {


    private static final Log LOGGER = LogFactory.getLog(HttpUtil.class);

    private static final int MAX_TIMEOUT = 20000;
    private static final int RETRY_TIMES = 3;
    private static final int MAX_RETRY_COUNT = 5;

    public static String get(String url, Map<String, String> params) {
        return get(url, params, new HashMap<>(), true, RETRY_TIMES, MAX_TIMEOUT);
    }

    public static String get(String url, Map<String, String> params, Map<String, String> headers, boolean reTry, int reTryTimes, int timeout) {
        StringBuilder sb = new StringBuilder(url);
        if (!params.isEmpty()) {
            sb.append("?").append(Joiner.on('&').withKeyValueSeparator('=').join(params));
        }
        Call call = getClient(reTry, reTryTimes, timeout).newCall(getRequestBuilder(headers).url(sb.toString()).build());
        return execute(call);
    }

    public static String synPost(String url, Map<String, String> params) {
        return post(url, params, new HashMap<>(), true, RETRY_TIMES, MAX_TIMEOUT, true);
    }

    public static String synPost(String url, Map<String, String> params, Map<String, String> headers, boolean reTry, int reTryTimes, int timeout) {
        return post(url, params, headers, reTry, reTryTimes, timeout, true);
    }

    public static void asynPost(String url, Map<String, String> params) {
        post(url, params, new HashMap<>(), true, RETRY_TIMES, MAX_TIMEOUT, false);
    }

    public static void asynPost(String url, Map<String, String> params, Map<String, String> headers, boolean reTry, int reTryTimes, int timeout) {
        post(url, params, headers, reTry, reTryTimes, timeout, false);
    }

    private static String post(String url, Map<String, String> params, Map<String, String> headers, boolean reTry, int reTryTimes, int timeout, boolean synchronous) {
        Call call = getClient(reTry, reTryTimes, timeout).newCall(getRequestBuilder(headers).url(url).post(getFormBodyBuilder(params).build()).build());
        if (synchronous) {
            return execute(call);
        } else {
            noReturnEnqueue(call);
            return null;
        }
    }

    private static OkHttpClient getClient(boolean reTry, int reTryTimes, int timeout) {
        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = client.newBuilder();
        if (reTry) {
            reTryTimes = reTryTimes == 0 ? RETRY_TIMES : (reTryTimes > 5 ? MAX_RETRY_COUNT : reTryTimes);
            builder.addInterceptor(new RetryInterceptor(reTryTimes));
        }
        timeout = timeout <= MAX_TIMEOUT ? timeout : MAX_TIMEOUT;
        client = builder.connectTimeout((long) timeout, TimeUnit.MILLISECONDS).readTimeout((long) timeout, TimeUnit.MILLISECONDS).build();
        return client;
    }

    private static FormBody.Builder getFormBodyBuilder(Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        if (!params.isEmpty()) {
            params.forEach(builder::add);
        }
        return builder;
    }

    private static Request.Builder getRequestBuilder(Map<String, String> headers) {
        Request.Builder builder = new Request.Builder();
        headers.forEach(builder::addHeader);
        return builder;
    }

    private static String execute(Call call) {
        try {
            Response response = call.execute();
            String result = response.body().string();
            response.close();
            return result;
        } catch (Exception var4) {
            LOGGER.error("请求失败：", var4);
        }
        return "";
    }

    private static void noReturnEnqueue(Call call) {
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                FormBody formBody = (FormBody) call.request().body();
                LOGGER.error("本次网络请求失败，请求参数：" + JSON.toJSONString(getParamMap(formBody)));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() != 200) {
                    FormBody formBody = (FormBody) call.request().body();
                    LOGGER.error("本次请求失败，请求参数：" + JSON.toJSONString(getParamMap(formBody)) + ",失败返回值：" + response.body());
                }
            }

            private Map<String, String> getParamMap(FormBody formBody) {
                Map<String, String> paramsMap = new HashMap<>();
                for (int i = 0; i < formBody.size(); ++i) {
                    paramsMap.put(formBody.name(i), formBody.value(i));
                }
                return paramsMap;
            }

        });
    }

    private static class RetryInterceptor implements Interceptor {
        private int maxRetry;
        private int retryNum = 0;

        RetryInterceptor(int maxRetry) {
            this.maxRetry = maxRetry;
        }

        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response;
            for (response = chain.proceed(request); !response.isSuccessful() && this.retryNum < this.maxRetry; response = chain.proceed(request)) {
                ++this.retryNum;
            }
            return response;
        }
    }


}
