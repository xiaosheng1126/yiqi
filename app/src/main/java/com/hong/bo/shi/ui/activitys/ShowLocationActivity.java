package com.hong.bo.shi.ui.activitys;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.hong.bo.shi.R;
import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.base.BaseActivity;
import com.hong.bo.shi.dialog.PutaoDialog;
import com.hong.bo.shi.klog.KLog;
import com.hong.bo.shi.utils.SystemUtils;
import com.hong.bo.shi.widget.CommonTitle;

import java.net.URISyntaxException;

public class ShowLocationActivity extends BaseActivity implements LocationSource, AMapLocationListener, AMap.OnMapClickListener, CommonTitle.OnRightClickListener {

    private MapView mMapView;
    private AMap mAMap;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private CommonTitle mCommonTitle;
    private String mTitle;
    private String mLot, mLat;
    private boolean isFirstSuccess = true;
    private boolean isFirstFailed = false;
    private AMapLocation mAMapLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitle = getIntent().getStringExtra(Constants.Key.TITLE);
        mLot = getIntent().getStringExtra(Constants.Key.LOT);
        mLat = getIntent().getStringExtra(Constants.Key.LAT);
        mCommonTitle = findViewByIds(R.id.commonTitle);
        mCommonTitle.setOnLeftClickListener(this);
        mCommonTitle.setLeftView("会话");
        mCommonTitle.setTitle("位置");
        mCommonTitle.setRightView("导航");
        mCommonTitle.setOnRightClickListener(this);
        //获取地图控件引用
        mMapView = findViewByIds(R.id.map_view);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mMapView.onCreate(savedInstanceState);
        //初始化地图变量
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }
        mAMap.moveCamera(CameraUpdateFactory.zoomBy(6));
        setUpMap();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_show_location;
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        mAMap.setOnMapClickListener(this);
        mAMap.setLocationSource(this);// 设置定位监听
        mAMap.getUiSettings().setMyLocationButtonEnabled(false);//设置默认定位按钮是否显示
        mAMap.setMyLocationStyle(new MyLocationStyle().radiusFillColor(Color.argb(0, 0, 0, 0)).
                strokeColor(Color.argb(0, 0, 0, 0)));
//        mAMap.setMyLocationEnabled(true);//设置为true表示显示定位层并可触发定位，
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(
                //15是缩放比例，0是倾斜度，30显示比例
                new CameraPosition(new LatLng(Double.valueOf(mLat), Double.valueOf(mLot)), 15, 0, 30));//这是地理位置，就是经纬度。
        mAMap.moveCamera(update);//定位的方法
        drawMarkers();

    }

    public void drawMarkers() {
        Marker marker = mAMap.addMarker(new MarkerOptions()
                .position(new LatLng(Double.valueOf(mLat), Double.valueOf(mLot)))
                .title(mTitle)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .draggable(true));
        marker.showInfoWindow();//设置默认显示一个infowinfow
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState
        // (outState)，实现地图生命周期管理
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                if (isFirstSuccess) {
                    mListener.onLocationChanged(aMapLocation);//显示系统小蓝点
                    mAMapLocation = aMapLocation;
                    isFirstSuccess = true;
                    mAMapLocation.setLongitude(Double.valueOf(mLot));
                    mAMapLocation.setLatitude(Double.valueOf(mLat));
                    mListener.onLocationChanged(aMapLocation);//显示系统小蓝点
                }
            } else {
                if (isFirstFailed) {
                    String errText = "定位失败," + aMapLocation.getErrorCode() + " : " + aMapLocation.getErrorInfo();
                    Toast.makeText(this, errText, Toast.LENGTH_SHORT).show();
                    isFirstFailed = true;
                    Log.e("AmapErr", errText);
                }
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            //初始化定位
            mlocationClient = new AMapLocationClient(this);
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();//启动定位
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onRightClick(View view) {
        PutaoDialog.Builder builder = new PutaoDialog.Builder(this);
        builder.setCancelItem(new PutaoDialog.Item().setTextRes(R.string.putao_cancel));
//        //存在百度地图
//        if (SystemUtils.isAvilible(this, "com.baidu.BaiduMap")) {
        builder.addItem(new PutaoDialog.Item().setTextRes(R.string.map_baidu).setId(R.id.map_baidu));
//        }
//        //存在搞得地图
//        if (SystemUtils.isAvilible(this, "com.autonavi.minimap")) {
        builder.addItem(new PutaoDialog.Item().setTextRes(R.string.map_gaode).setId(R.id.map_gaode));
//        }
        builder.setListener(new PutaoDialog.OnDialogItemClickListener() {
            @Override
            public boolean onItemClick(PutaoDialog.Item item) {
                if (R.id.map_baidu == item.getId()) {
                    gotoBaidu();
                } else if (R.id.map_gaode == item.getId()) {
                    gotoGaode();
                }
                return true;
            }
        });
        builder.build().show();
    }

    private void gotoBaidu() {
//        intent = Intent.getIntent("intent://map/direction?origin=latlng:34.264642646862,108.95108518068|name:我家&destination=大雁塔&mode=driving®ion=西安&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
        if (SystemUtils.isAvilible(this, "com.baidu.BaiduMap")) {
            try {
                Intent intent = Intent.getIntent("intent://map/direction?" +
                        //"origin=latlng:"+"34.264642646862,108.95108518068&"+//起点此处不传值默认选择当前位置
                        "destination=latlng:" + mLot + "," + mLat + "|name:我的目的地" +        //终点
                        "&mode=driving&" +          //导航路线方式
                        "region=北京" +           //
                        "&src=慧医#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                startActivity(intent); //启动调用
            } catch (URISyntaxException e) {
                e.printStackTrace();
                KLog.e("intent", e.getMessage());
            }
        } else {
            //market为路径，id为包名
            //显示手机上所有的market商店
            Toast.makeText(this, "您尚未安装百度地图", Toast.LENGTH_LONG).show();
            Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    private void gotoGaode() {
        if (SystemUtils.isAvilible(this, "com.autonavi.minimap")) {
            try {
                Intent intent = Intent.getIntent("androidamap://navi?sourceApplication=慧医&poiname=我的目的地&lat=" +
                        mLat + "&lon=" + mLot + "&dev=0");
                startActivity(intent);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                KLog.e("intent", e.getMessage());
            }
        } else {
            Toast.makeText(this, "您尚未安装高德地图", Toast.LENGTH_LONG).show();
            Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }
}
