package constants;

import android.content.Context;

/**
 * Created by huangb on 2017/3/28.
 */

public class PayInit {
    private static Context context;
    public static String mWxAppId;

    private PayInit() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化
     * @param context context
     * @param wxappid 微信appid
     */
    public static void init(Context context, String wxappid) {
        PayInit.context = context.getApplicationContext();
        mWxAppId = wxappid;
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        if (context != null) return context;
        throw new NullPointerException("u should init first");
    }
}
