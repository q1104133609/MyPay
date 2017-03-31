package com.huangbo.pay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.kc.R;

import constants.PayInit;
import wxapi.WxPayResult;
import wxapi.WxPayUtils;

public class MainActivity extends AppCompatActivity implements WxPayResult.WxPayListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PayInit.init(getApplicationContext(), "wx_appid");
        WxPayUtils.pay("wxc73fc095a6646171", "1381337202", "wx201702091502529f0aa990f90604723722", "1501284176", "1486623688", "DF16785D54EB11C2CFD399C81036C4F9", this);
    }

    @Override
    public void wxResult(int code, String mesge) {
        Log.e("huangbo", code + " " + mesge);
    }
}
