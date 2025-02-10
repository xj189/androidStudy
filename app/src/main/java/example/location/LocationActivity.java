package example.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import android.view.View;
import com.style.base.BaseTitleBarActivity;
import com.style.framework.R;
import com.style.framework.databinding.LocationActivityBinding;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.jetbrains.annotations.Nullable;

public class LocationActivity extends BaseTitleBarActivity {

    private LocationActivityBinding bd;
    private LocationManager lm;
    private MyLocationListener gpsListener;
    private MyLocationListener networkListner;
    private MyGnssStatusCallback mGnssStatusCallback;

    @Override
    protected void onCreate(@Nullable Bundle arg0) {
        super.onCreate(arg0);
        bd = LocationActivityBinding.inflate(getLayoutInflater());
        setContentView(bd.getRoot());
        bd.btnStartLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocationPermission();
            }
        });
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);

    }

    @Override
    public void onDestroy() {
        if (mGnssStatusCallback != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                lm.unregisterGnssStatusCallback(mGnssStatusCallback);
            }
        }
        if (networkListner != null) {
            lm.removeUpdates(networkListner);
        }
        if (gpsListener != null) {
            lm.removeUpdates(gpsListener);
        }
        super.onDestroy();
    }

    private void initLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mGnssStatusCallback = new MyGnssStatusCallback();
            lm.registerGnssStatusCallback(mGnssStatusCallback);
        }
        networkListner = new MyLocationListener(LocationManager.NETWORK_PROVIDER);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 10, networkListner);
        gpsListener = new MyLocationListener(LocationManager.GPS_PROVIDER);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, gpsListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private class MyGnssStatusCallback extends GnssStatus.Callback {
        @Override
        public void onStarted() {
            logE(getTAG(), "onStarted");
        }

        @Override
        public void onStopped() {
            logE(getTAG(), "onStopped");
        }

        @Override
        public void onFirstFix(int ttffMillis) {
            logE(getTAG(), "onFirstFix");
        }

        @Override
        public void onSatelliteStatusChanged(GnssStatus status) {
            logE(getTAG(), "onSatelliteStatusChanged SatelliteCount " + status.getSatelliteCount());
        }
    }

    private class MyLocationListener implements LocationListener {

        private String provider;

        public MyLocationListener(String provider) {
            this.provider = provider;
        }

        /**
         * 当位置改变的时候
         */
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                float accuracy = location.getAccuracy();
                double latitude = location.getLatitude(); //纬度
                double longitude = location.getLongitude(); //经度
                logE("onLocationChanged", "latitude:" + latitude + ",longitude:" + longitude);
                refreshLocation(location);
            }
        }

        /**
         * 某一个位置提供者的状态发生改变的时候调用的方法
         */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            logE(this.provider, "onStatusChanged " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            logE(this.provider, "onProviderEnabled " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            logE(this.provider, "onProviderDisabled " + provider);
        }

    }

    private void refreshLocation(Location location) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bd.tvData.setText(location.toString());
            }
        });
    }

    private void checkLocationProvider() {
        boolean isNetwork = isNetworkEnabled();
        boolean isGps = isGpsEnabled();

        if (!isGps) {
            showToast("开启gps以便获取定位");
            openGPSSetting();
        } else {
            initLocation();
        }
    }

    private boolean isNetworkEnabled() {
        boolean isOpen;
        isOpen = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return isOpen;
    }

    private int GPS_REQUEST_CODE = 1;

    private boolean isGpsEnabled() {
        boolean isOpen;
        isOpen = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isOpen;
    }

    private void openGPSSetting() {
        //跳转到手机原生设置页面
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, GPS_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_REQUEST_CODE) {
            if (isGpsEnabled())
                initLocation();
        }
    }

    @SuppressLint("CheckResult")
    private void checkLocationPermission() {
        new RxPermissions(this)
                .request(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        checkLocationProvider();
                    } else {
                        showToast("请打开位置信息权限");
                    }
                });
    }

}
