package com.osec.fido2test.global;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.omes.fido2test.R;

import com.osec.fido2sdk.parameter.PublicKeyCredentialType;
import com.osec.fido2test.MyApplication;
import com.osec.fido2test.entity.LocalMessage;
import com.osec.fido2test.entity.LocalRegInfo;
import com.osec.fido2test.utils.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.osec.fido2test.utils.SpUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 本地模拟报文的管理类
 */
public class LocalMessageManager {
    private static final String TAG = "LocalMessageManager";

    private static volatile LocalMessageManager mInstance = null;
    private List<LocalMessage> mMessageList = null;

    public static LocalMessageManager getInstance() {
        if (mInstance == null) {
            synchronized (LocalMessageManager.class) {
                if (mInstance == null) {
                    mInstance = new LocalMessageManager();
                }
            }
        }
        return mInstance;
    }

    private LocalMessageManager() {
        Context context = MyApplication.getContext();
        InputStream in = context.getResources().openRawResource(R.raw.requests);
        InputStreamReader reader = new InputStreamReader(in);
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            Type type = new TypeToken<List<LocalMessage>>() {
            }.getType();
            mMessageList = new Gson().fromJson(sb.toString(), type);
        } catch (IOException e) {
            Log.e(TAG, "LocalMessageManager " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<LocalMessage> getMessageList() {
        return mMessageList;
    }

    /**
     * 获取本地认证的报文，在报文中添加注册时的credentialId
     */
    public String getAuthMessage(LocalMessage message, String credentialId) {
        HashMap<String, Object> credentialDescriptor = new HashMap<>();
        credentialDescriptor.put("type", PublicKeyCredentialType.PUBLIC_KEY);

        if (null == credentialId) {
            LocalRegInfo info = getRegInfo(message.title);
            if (info == null) {
                return JsonUtil.toJson(message.requestMessage);
            }
            credentialDescriptor.put("id", info.credentialId);
        } else {
            credentialDescriptor.put("id", credentialId);
        }

        Map messageMap = (Map) message.requestMessage;
        List<HashMap<String, Object>> allowCredentials = new ArrayList<>();
        allowCredentials.add(credentialDescriptor);
        messageMap.put("allowCredentials", allowCredentials);
        return JsonUtil.toJson(messageMap);
    }

    public void saveRegInfo(String key, LocalRegInfo info) {
        Log.d(TAG, "saveLocalRegInfo key=" + key + "," + info);
        SpUtils.putString(key, JsonUtil.toJson(info));
    }

    private LocalRegInfo getRegInfo(String key) {
        Log.d(TAG, "getLocalRegInfo key=" + key);
        String regInfoStr = SpUtils.getString(key);
        Log.d(TAG, "regInfoStr=" + regInfoStr);
        if (TextUtils.isEmpty(regInfoStr)) {
            return null;
        }
        return JsonUtil.fromJson(regInfoStr, LocalRegInfo.class);
    }
}