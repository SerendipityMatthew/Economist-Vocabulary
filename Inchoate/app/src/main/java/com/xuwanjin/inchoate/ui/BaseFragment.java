package com.xuwanjin.inchoate.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.xuwanjin.inchoate.InchoateApp;
import com.xuwanjin.inchoate.Utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

public abstract class BaseFragment extends Fragment {

    private static final int CALL_TIMEOUT = 10;
    private static final int CONNECT_TIMEOUT = 10;
    private static final int READ_TIMEOUT = 10;
    private static final int WRITE_TIMEOUT = 10;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutResId = getLayoutResId();
        View mRootView = inflater.inflate(layoutResId, container, false);
        initView(mRootView);
        loadData();
        return mRootView;
    }

    protected abstract void initView(View view);

    protected abstract void loadData();

    protected abstract int getLayoutResId();

    protected abstract <T> T fetchDataFromDBOrNetwork();

    protected abstract <T> T initFakeData();

    protected Gson getGsonInstance() {
        Gson gson = new Gson()
                .newBuilder()
                .setFieldNamingStrategy(new FieldNamingStrategy() {
                    @Override
                    public String translateName(Field f) {
                        String name = f.getName();
                        if (name.contains("-")) {
                            return name.replaceAll("-", "");
                        }
                        return name;
                    }
                }) // setFieldNamingPolicy 有什么区别
                .create();
        return gson;
    }

    protected String fetchJsonFromServer(String wholeUrl) {
        if (!Utils.isNetworkAvailable(getContext())) {
            return null;
        }
        /*
            在这里遇到了一个系统时间和服务器时间不一致的问题, 抛出了 Chain validation failed , 问题
            要么提示修改时间, 要么信任所有的证书
         */
        X509TrustManager X509TrustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
        OkHttpClient client = null;
        try {
            client = new OkHttpClient
                    .Builder()
                    .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                    .callTimeout(CALL_TIMEOUT, TimeUnit.SECONDS)
                    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                    .sslSocketFactory(getSSLSocketFactory(), X509TrustManager)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Request request = new Request.Builder()
                .url(wholeUrl)
                .build();
        Call call = client.newCall(request);
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response == null || !response.isSuccessful()) {
            return null;
        }
        String jsonResult = null;
        try {
            jsonResult = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonResult;
    }

    public static SSLSocketFactory getSSLSocketFactory() throws Exception {
        final SSLContext sslContext = SSLContext.getInstance("TLS");
        TrustManager[] trustManagers = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }

        };
        // 这个参数 trustManagers 是必须的? 否则就达不到时间不对也可以获取数据的目的.
        sslContext.init(null, trustManagers, new SecureRandom());
        return sslContext.getSocketFactory();
    }


    protected void navigationToFragment(int resId) {
        Utils.navigationController(InchoateApp.NAVIGATION_CONTROLLER, resId);
    }
}
