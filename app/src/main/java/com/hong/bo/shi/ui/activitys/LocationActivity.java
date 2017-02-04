package com.hong.bo.shi.ui.activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.hong.bo.shi.R;
import com.hong.bo.shi.app.App;
import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.base.BaseActivity;
import com.hong.bo.shi.ui.adapter.LocationAdapter;
import com.hong.bo.shi.widget.CommonTitle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class LocationActivity extends BaseActivity implements LocationSource, AMapLocationListener, AMap.OnMapClickListener
        , LocationAdapter.OnSelectListener, CommonTitle.OnRightClickListener {

    private MapView mMapView;
    private AMap mAMap;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private CommonTitle mCommonTitle;
    private LocationAdapter mAdapter;
    private AMapLocation mAMapLocation;
    private boolean isFirstSuccess = true;
    private boolean isFirstFailed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCommonTitle = findViewByIds(R.id.commonTitle);
        mCommonTitle.setOnLeftClickListener(this);
        mCommonTitle.setOnRightClickListener(this);
        mCommonTitle.setLeftView("会话");
        mCommonTitle.setTitle("位置");
        mCommonTitle.setRightView("发送");
        mCommonTitle.getRightTextView().setEnabled(false);
        mCommonTitle.getRightTextView().setTextColor(Color.parseColor("#959595"));
        RecyclerView recyclerView = findViewByIds(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new LocationAdapter();
        mAdapter.setOnSelectListener(this);
        recyclerView.setAdapter(mAdapter);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map_view);
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
        return R.layout.activity_location;
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        mAMap.setOnMapClickListener(this);
        mAMap.setLocationSource(this);// 设置定位监听
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示
        mAMap.setMyLocationStyle(new MyLocationStyle().radiusFillColor(Color.argb(0, 0, 0, 0)).
                strokeColor(Color.argb(0, 0, 0, 0)));
        mAMap.setMyLocationEnabled(true);//设置为true表示显示定位层并可触发定位，
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

    /**
     * 激活定位
     * 在aMap.setLocationSource(this)中包含两个回调，activate(OnLocationChangedListener)和deactivate()。
     * 在activate()中设置定位初始化及启动定位，在deactivate()中写停止定位的相关调用。
     */
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

    /**
     * 停止定位
     */
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
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                if(isFirstSuccess) {
                    mListener.onLocationChanged(aMapLocation);//显示系统小蓝点
                    mAMapLocation = aMapLocation;
                    isFirstSuccess = false;
                    query(aMapLocation.getLongitude(), aMapLocation.getLatitude());
                }
            } else {
                if(isFirstFailed) {
                    String errText = "定位失败," + aMapLocation.getErrorCode() + " : " + aMapLocation.getErrorInfo();
                    Toast.makeText(this, errText, Toast.LENGTH_SHORT).show();
                    Log.e("AmapErr", errText);
                    isFirstFailed = false;
                }
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    private void query(double lon, double lat) {
        //构造 PoiSearch.Query 对象，通过 PoiSearch.Query(String query, String ctgr, String city) 设置搜索条件
        final PoiSearch.Query query = new PoiSearch.Query("",
                "汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施", "");
        //keyWord表示搜索字符串，
        //第二个参数表示POI搜索类型，二者选填其一，
        //POI搜索类型共分为以下20种：汽车服务|汽车销售|
        //汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|
        //住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|
//金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施
//cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
        query.setPageSize(100);// 设置每页最多返回多少条poiitem
        query.setPageNum(1);//设置查询页码
        //构造 PoiSearch 对象，并设置监听。
        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult result, int rCode) {
                dismiss();// 隐藏对话框
                if (rCode == 1000) {
                    if (result != null && result.getQuery() != null) {// 搜索poi的结果
                        if (result.getQuery().equals(query)) {//是否是同一次
                            // 取得搜索到的poiitems有多少页
                            List<PoiItem> poiItems = result.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                            mAdapter.setData(poiItems);
                            mCommonTitle.getRightTextView().setEnabled(true);
                            mCommonTitle.getRightTextView().setTextColor(Color.parseColor("#ffffff"));
                        }
                    }
                }
            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int i) {
            }
        });
        //设置周边搜索的中心点以及半径
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(lon, lat), 5000));
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onChanged(PoiItem item) {
//        mAMap.clear();//清理之前的图标
        mAMapLocation.setLongitude(item.getLatLonPoint().getLongitude());
        mAMapLocation.setLatitude(item.getLatLonPoint().getLatitude());
        mListener.onLocationChanged(mAMapLocation);//显示系统小蓝点
    }

    @Override
    public void onRightClick(View view) {
        try {
            mMapView.setDrawingCacheEnabled(true);
            Bitmap bitmap = mMapView.getDrawingCache();
            String pictureCache = App.getInstance().getPictureCache();
            File file = new File(pictureCache, UUID.randomUUID() + ".jpg");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fOut = null;
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
            fOut.flush();
            Intent intent = new Intent();
            PoiItem selectData = mAdapter.getSelectData();
            intent.putExtra(Constants.Key.PATH, file.getAbsolutePath());
            intent.putExtra(Constants.Key.LOT, String.valueOf(selectData.getLatLonPoint().getLongitude()));
            intent.putExtra(Constants.Key.LAT, String.valueOf(selectData.getLatLonPoint().getLatitude()));
            intent.putExtra(Constants.Key.ADDRESS, selectData.getCityName() + selectData.getAdName() + selectData.getSnippet());
            setResult(RESULT_OK, intent);
            finish();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            mMapView.setDrawingCacheEnabled(false);
        }
    }
}
