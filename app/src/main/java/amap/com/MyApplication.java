package amap.com;

import android.app.Application;
import android.telephony.TelephonyManager;

import amap.com.baidu.TraceHelper;

/**
 * Created by chuanzhangjiang on 17-10-13.
 *
 */

public class MyApplication extends Application {

    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        TraceHelper.getInstance().init(this);
    }

    public static MyApplication getInstance() {
        return instance;
    }

    /**
     * 获取设备IMEI码
     *
     */
    public String getImei() {
        String imei;
        try {
            imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
        } catch (Exception e) {
            imei = "myTrace";
        }
        return imei;
    }
}
