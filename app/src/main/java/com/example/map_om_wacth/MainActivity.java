package com.example.map_om_wacth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.example.map_om_wacth.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements AMapLocationListener {

    private ActivityMainBinding binding;
    private MapView mMapView = null;
    private AMap aMap;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private Marker ownMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MapsInitializer.updatePrivacyShow(this, true, true);
        MapsInitializer.updatePrivacyAgree(this, true);

        mMapView = binding.amapView;
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        // 初始化定位
        initLocation();
    }

    private void initLocation() {
        locationClient = new AMapLocationClient(getApplicationContext());
        locationOption = getDefaultOption();
        locationClient.setLocationOption(locationOption);
        locationClient.setLocationListener(this);
    }

    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mOption.setHttpTimeOut(30000);
        mOption.setNeedAddress(true);
        mOption.setOnceLocation(false);
        mOption.setOnceLocationLatest(false);
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);
        return mOption;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
            LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            Log.e("Latlng", latLng.toString() + "");
            updateMarkerLocation(latLng);
        } else {
            Log.e("MainActivity", "Location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
        }
    }

    private void updateMarkerLocation(LatLng latLng) {
        if (ownMarker != null) {
            ownMarker.remove();
        }
        ownMarker = aMap.addMarker(new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_end))
                .draggable(true));

        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        if (locationClient != null) {
            locationClient.startLocation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        if (locationClient != null) {
            locationClient.stopLocation();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (locationClient != null) {
            locationClient.onDestroy();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
}
