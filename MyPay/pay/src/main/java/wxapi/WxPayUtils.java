package wxapi;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import constants.PayInit;

/**
 * Created by Administrator on 2016/8/13.
 */
public class WxPayUtils {
    /**
     * 微信支付
     */

    public static void pay(String appid, String partnerid, String prepayid, String noncestr, String timestamp, String sign, WxPayResult.WxPayListener wxPayListener) {
        WxPayResult.getInstans().setListener(wxPayListener);
        IWXAPI api = null;
            api = WXAPIFactory.createWXAPI(PayInit.getContext(), PayInit.mWxAppId);
        try {
            PayReq req = new PayReq();
            req.appId = appid;
            req.partnerId = partnerid;
            req.prepayId = prepayid;
            req.nonceStr = noncestr;
            req.timeStamp = timestamp;
            req.packageValue = "Sign=WXPay";
            req.sign = sign;
            req.extData = "app data";
            Toast.makeText(PayInit.getContext(), "正常调起支付", Toast.LENGTH_SHORT).show();
            api.registerApp(appid);
            api.sendReq(req);
        } catch (Exception e) {
            Toast.makeText(PayInit.getContext(), "异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

}
