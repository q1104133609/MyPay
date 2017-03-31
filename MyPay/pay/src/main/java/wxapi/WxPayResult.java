package wxapi;

/**
 * Created by huangb on 2017/3/28.
 */

public class WxPayResult {
    private static WxPayResult instans;

    public static WxPayResult getInstans() {
        if (instans == null) {
            instans = new WxPayResult();
        }
        return instans;
    }

    public interface WxPayListener {
        void wxResult(int code, String mesge);
    }

    public static WxPayListener mWxPayListener;

    public void setListener(WxPayListener listener) {
        if (listener != null) {
            mWxPayListener = listener;
        }
    }
}
