package amap.com.baidu;

import android.app.Application;
import android.widget.Toast;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.api.entity.LocRequest;
import com.baidu.trace.api.track.HistoryTrackRequest;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.track.SupplementMode;
import com.baidu.trace.model.CoordType;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.ProcessOption;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.TransportMode;

import amap.com.MyApplication;

/**
 * Created by chuanzhangjiang on 17-10-13.
 *
 */

public class TraceHelper {

    private Application mContext;

    private LBSTraceClient mClient;

    private Trace mTrace;

    private LocRequest mLocRequest;

    private String entityName;

    private static volatile TraceHelper mInstance;

    private static final long serviceId = 151320;

    private TraceHelper() {
    }

    public static TraceHelper getInstance() {
        if (mInstance == null) {
            synchronized (TraceHelper.class) {
                if (mInstance == null) {
                    mInstance = new TraceHelper();
                }
            }
        }
        return mInstance;
    }

    public TraceHelper init(Application context) {
        mContext = context;
//        SDKInitializer.initialize(context);
//        SDKInitializer.setCoordType(com.baidu.mapapi.CoordType.GCJ02);
        mClient = new LBSTraceClient(context);
        mClient.setInterval(2, 10);//设置定位周期和打包周期
        entityName = MyApplication.getInstance().getImei();
        mTrace = new Trace(serviceId, entityName);
        mLocRequest = new LocRequest(serviceId);
        return this;
    }

    private OnTraceListener traceListener;

    public void startUpTrace() {
        traceListener = new OnTraceListener() {
            @Override
            public void onBindServiceCallback(int i, String s) {

            }

            @Override
            public void onStartTraceCallback(int i, String s) {
                mClient.startGather(this);
            }

            @Override
            public void onStopTraceCallback(int i, String s) {

            }

            @Override
            public void onStartGatherCallback(int i, String s) {
                Toast.makeText(mContext, "百度位置实时上传开始: " + s, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStopGatherCallback(int i, String s) {
                Toast.makeText(mContext, "百度位置实时上传结束: " + s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPushCallback(byte b, PushMessage pushMessage) {

            }

            @Override
            public void onInitBOSCallback(int i, String s) {

            }
        };
        mClient.startTrace(mTrace, traceListener);
    }

    public void stopUpTrace() {
        mClient.stopTrace(mTrace, traceListener);
    }

    public void queryHistoryTrace(long startTime, long endTime, OnTrackListener listener) {
        queryHistoryTrace(startTime, endTime, listener, true);
    }

    public void queryHistoryTrace(long startTime, long endTime, OnTrackListener listener, boolean grasp) {
        final HistoryTrackRequest request = new HistoryTrackRequest(1, serviceId, entityName);
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        final ProcessOption option = new ProcessOption();
        option.setRadiusThreshold(80);
        option.setTransportMode(TransportMode.riding);
        option.setNeedDenoise(true);
        option.setNeedMapMatch(true);
        option.setNeedVacuate(true);
        request.setProcessOption(option);
        request.setSupplementMode(SupplementMode.riding);
        request.setProcessed(grasp);
        request.setCoordTypeOutput(CoordType.gcj02);
        mClient.queryHistoryTrack(request, listener);
    }
}