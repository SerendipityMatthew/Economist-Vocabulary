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

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class BaseFragment extends Fragment {
    private View mRootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutResId = getLayoutResId();
        mRootView = inflater.inflate(layoutResId, container, false);
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
        if (!Utils.isNetworkAvailable(getContext())){
            return null;
        }
        OkHttpClient client = new OkHttpClient();
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

    protected void navigationToFragment(int resId) {
        Utils.navigationController(InchoateApp.NAVIGATION_CONTROLLER, resId);
    }
}
