package com.blindingdark.geektrans;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.blindingdark.geektrans.tools.MyToast;
import com.blindingdark.geektrans.trans.Translator;
import com.blindingdark.geektrans.trans.baidu.Baidu;
import com.blindingdark.geektrans.trans.baidu.BaiduSettingsString;
import com.blindingdark.geektrans.trans.baidu.bean.BaiduSettings;
import com.blindingdark.geektrans.trans.youdao.Youdao;
import com.blindingdark.geektrans.trans.youdao.YoudaoSettingsString;
import com.blindingdark.geektrans.trans.youdao.bean.YoudaoSettings;

public class TransActivity extends Activity {


    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public void setToastTime(String stringToastTime) {
        this.toastTime = (int) Float.parseFloat(stringToastTime) * 1000;
    }

    int toastTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        processIntent(getIntent());
    }

    void trans(String req) {

        String nowTransEngine = preferences.getString(StringMainSettings.nowTransEngine, StringMainSettings.youdaoTransEngine);

        if (nowTransEngine.equals(StringMainSettings.youdaoTransEngine)) {
            String key = preferences.getString(YoudaoSettingsString.youdaoKey, "");
            String keyfrom = preferences.getString(YoudaoSettingsString.youdaoKeyfrom, "");
            String divLine = preferences.getString(YoudaoSettingsString.divisionLine, YoudaoSettingsString.defaultDivLine);
            String stringToastTime = preferences.getString(YoudaoSettingsString.youdaoToastTime,YoudaoSettingsString.youdaoDefToastTime);
            this.setToastTime(stringToastTime);

            Translator.trans(req, new Youdao(new YoudaoSettings(key, keyfrom, divLine)), myHandler);

        }

        if (nowTransEngine.equals(StringMainSettings.baiduTransEngine)) {
            String BDAPPID = preferences.getString(BaiduSettingsString.baiduAppId, "");
            String BDKey = preferences.getString(BaiduSettingsString.baiduKey, "");
            String BDFrom = preferences.getString(BaiduSettingsString.baiduFrom, "auto");
            String BDTo = preferences.getString(BaiduSettingsString.baiduTo, "zh");
            String stringToastTime = preferences.getString(BaiduSettingsString.baiduToastTime,BaiduSettingsString.defBaiduToastTime);
            this.setToastTime(stringToastTime);

            Translator.trans(req, new Baidu(new BaiduSettings(BDAPPID, BDKey, BDFrom, BDTo)), myHandler);
        }


    }

    private void processIntent(Intent intent) {
        String req = intent.getStringExtra("req");
        this.trans(req);
        finish();
    }

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String result = (String) msg.obj;
                    if (TextUtils.isEmpty(result)) {
                        MyToast.makeText(getApplicationContext(), "网络异常", toastTime).show();
                    } else {
                        MyToast.makeText(getApplicationContext(), result, toastTime).show();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
