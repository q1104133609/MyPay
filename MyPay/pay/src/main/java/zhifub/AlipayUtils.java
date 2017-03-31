package zhifub;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import constants.PayConstans;
import constants.PayInit;

/**
 * Created by Administrator on 2016/7/28.
 */
public class AlipayUtils {
    private static Context mContext;

    private static AlipayUtils instance = null;

    public static AlipayUtils getInstance() {
        if (instance == null) {
            instance = new AlipayUtils(PayInit.getContext());
        }
        return instance;
    }

    public AlipayUtils(Context context) {
        this.mContext = context;
    }

    // 商户PID
    public static String PARTNER = "";
    // 商户收款账号
    public static String SELLER = "";
    // 商户私钥，pkcs8格式
    public static String RSA_PRIVATE = "";
    private static final int SDK_PAY_FLAG = 1;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    if (TextUtils.equals(resultStatus, "9000")) {
                        if (mPayCompleteLinstener != null)
                            mPayCompleteLinstener.complete(PayConstans.PAY_SUCCESS, "支付成功");
                        Toast.makeText(mContext, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(mContext, "支付结果确认中", Toast.LENGTH_SHORT).show();
                        } else {
                            if (mPayCompleteLinstener != null)
                                mPayCompleteLinstener.complete(PayConstans.PAY_SUCCESS, "支付失败");
                            Toast.makeText(PayInit.getContext(), "支付失败", Toast.LENGTH_SHORT).show();

                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };


    /**
     * @param subject
     * @param body
     * @param price
     * @param notify_url
     * @param out_trade_no
     * @param partenr      商户PID
     * @param seller       商户收款账号
     * @param rsa_private  商户私钥，pkcs8格式
     */
    public AlipayUtils pay(String subject, String body, String price, String notify_url, String out_trade_no, String partenr, String seller, String rsa_private) {
        PARTNER = partenr;
        SELLER = seller;
        RSA_PRIVATE = rsa_private;

        if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE) || TextUtils.isEmpty(SELLER)) {
            new AlertDialog.Builder(mContext).setTitle("警告").setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            ((Activity) mContext).finish();
                        }
                    }).show();
        }


        String orderInfo = getOrderInfo(subject, body, price, notify_url, out_trade_no);
        toAliPay(orderInfo);
        return this;
    }


    /**
     * 支付
     *
     * @param orderInfo 打包的支付信息
     */
    public AlipayUtils pay(final String orderInfo, final Activity activity) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(activity);
                String result = alipay.pay(orderInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
        return this;

    }

    /**
     * 调动阿里支付
     *
     * @param orderInfo
     */
    private AlipayUtils toAliPay(String orderInfo) {
        String sign = sign(orderInfo);
        try {
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask((Activity) mContext);
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
        return this;
    }


    /**
     * create the order info. 创建订单信息
     */
    private String getOrderInfo(String subject, String body, String price, String notify_url, String out_trade_no) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + out_trade_no + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + notify_url + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        orderInfo += "&it_b_pay=\"30m\"";

        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        orderInfo += "&return_url=\"m.alipay.com\"";


        return orderInfo;
    }


    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    private String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

    public interface PayCompleteLinstener {
        void complete(int paySuccess, String s);
    }

    public void setPayCompleteListener(PayCompleteLinstener payCompleteListener) {
        if (payCompleteListener != null) {
            this.mPayCompleteLinstener = payCompleteListener;
        }

    }

    private PayCompleteLinstener mPayCompleteLinstener;


}
