package com.style.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import androidx.core.content.FileProvider;

import com.style.config.FileDirConfig;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * private void getMetricsWithSize(DisplayMetrics outMetrics, CompatibilityInfo compatInfo,
 * Configuration configuration, int width, int height) {
 * outMetrics.densityDpi = outMetrics.noncompatDensityDpi = logicalDensityDpi;
 * outMetrics.density = outMetrics.noncompatDensity =
 * logicalDensityDpi * DisplayMetrics.DENSITY_DEFAULT_SCALE;
 * outMetrics.scaledDensity = outMetrics.noncompatScaledDensity = outMetrics.density;
 * outMetrics.xdpi = outMetrics.noncompatXdpi = physicalXDpi;
 * outMetrics.ydpi = outMetrics.noncompatYdpi = physicalYDpi;
 * <p>
 * final Rect appBounds = configuration != null
 * ? configuration.windowConfiguration.getAppBounds() : null;
 * width = appBounds != null ? appBounds.width() : width;
 * height = appBounds != null ? appBounds.height() : height;
 * <p>
 * outMetrics.noncompatWidthPixels  = outMetrics.widthPixels = width;
 * outMetrics.noncompatHeightPixels = outMetrics.heightPixels = height;
 * <p>
 * if (!compatInfo.equals(CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO)) {
 * compatInfo.applyToDisplayMetrics(outMetrics);
 * }
 * }
 * Created by xiajun on 2017/1/9.
 * 设备信息工具类
 */

public class DeviceInfoUtil {
    private static final String TAG = "DeviceInfoUtil";

    public static int dp2px(Context context, float dpValue) {
        float pxValue = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
        return (int) (pxValue + 0.5f);//0.5f是为了四舍五入，但有时候四舍五入不一定就好
    }

    public static float sp2px(Context context, int sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static Display getDisplay(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay();
    }

    /**
     * dp：也是dip(device independent pixels)设备独立像素，在不同的设备显示效果不同，dp可以自适应屏幕的密度。
     * Density Independent Pixels的缩写，以160dpi为基准。
     * 在160dpi设备上1dp=1px，在240dpi设备上1dp=1.5px,以此类推
     * PPI和DPI的区别：
     * 理论上对于屏幕而言，点就是像素，像素就是点，ppi和dpi应该没有区别才对，但是对于图里的屏幕，已经计算过ppi=293，跑分软件却显示dpi=320。
     * 为什么dpi和ppi会不同？其实这是人为规定的结果，估计是谷歌只考虑了屏幕分辨率没有考虑屏幕尺寸设计的。
     * 在开发中使用的dot也就是dpi中的d，如果有一个640d*360d的东西，显示在上述1280*720的屏幕上，严格点对点显示，将正好占据1/4个屏幕，
     * 但是在640*360的屏幕上就是占满了整块屏幕。
     * 生活中的屏幕分辨率五花八门，点对点显示肯定是行不通的，所以需要按比例显示。先规定基准dpi为160（安卓早期谷歌规定的基准值），
     * 还是严格按照定义，1280*720的5寸屏幕ppi=dpi=293，假设有一条80d的线段，
     * 那么在这块屏幕上实际点数应该是293/160*80=146.5？？？出现了半个像素的情况！！！这让屏幕左右为男，显示也不对，不显示也不对。
     * 为了避免这种问题，谷歌又规定了几种标准dpi分别为240、320等等，和160dpi的比例分别为1.5、2，293与320最为相近（没有研究具体什么是最为相近），
     * 所以规定此屏幕dpi为320。这样只要确保开发中使用的大小即点数必须为偶数，这样再乘以比例就不会出现半个点的情况。
     *
     * @param context
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        getDisplay(context).getMetrics(metric);
        int width = metric.widthPixels;      // 屏幕宽度（像素）
        int height = metric.heightPixels;    // 屏幕高度（像素）
        float density = metric.density;      // densityDpi/160的比值 sharpR3:4
        int densityDpi = metric.densityDpi;  // 屏幕密度The screen density expressed as dots-per-inchDPI(根据屏幕分辨率认为规定的一个值)(160像素/英寸) sharpR3:640
        float scaledDensity = metric.scaledDensity;  // 系统设置里面的字体大小缩放值
        return metric;
    }

    public static int getStatusHeight(Context context) {
        int statusBarHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, context.getResources().getDisplayMetrics());
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        Log.i(TAG, "状态栏-高度:" + statusBarHeight);
        return statusBarHeight;
    }

    @SuppressLint("MissingPermission")
    public static String getIMEI(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String id = tm.getDeviceId();
            if (id != null) {
                return tm.getDeviceId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void openEditSms(Context context, String phone) {
        Uri smsToUri = Uri.parse("smsto:" + phone);
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.putExtra("sms_body", "我最近成为了脉连的用户，可以随时查找与别人的连系，让交友变得简单可信。交朋友、找关系好用极了，推荐给你试试（下载链接:www.yimxl.com）。");
        context.startActivity(intent);
    }

    public static void notifyUpdateGallary(Context context, File photoFile) {
        /*Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, authority, photoFile);
        } else {
            uri = Uri.fromFile(photoFile);
        }
        intent.setData(uri);
        context.sendBroadcast(intent);*/
        MediaScannerConnection.scanFile(context, new String[]{photoFile.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                Log.e("onScanCompleted", path);
                Log.e("onScanCompleted", "" + uri);

            }
        });
    }

    public static int[] getViewLocationOnScreen(View view) {
        int[] locations = new int[2];
        view.getLocationOnScreen(locations);
        int x = locations[0];// 获取组件当前位置的横坐标
        int y = locations[1];// 获取组件当前位置的纵坐标
        return locations;
    }

    /**
     * 需要先判断SD卡权限，再判断是否可用
     *
     * @return
     */
    public static boolean isSDcardWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean isSDcardReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.getHostAddress().isEmpty()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("WifiIpAddress==", ex.toString());
        }
        return null;
    }
}
