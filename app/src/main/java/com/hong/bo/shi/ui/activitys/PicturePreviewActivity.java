package com.hong.bo.shi.ui.activitys;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.hong.bo.shi.R;
import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.base.BaseActivity;
import com.hong.bo.shi.utils.DensityUtils;

import me.relex.photodraweeview.OnViewTapListener;
import me.relex.photodraweeview.PhotoDraweeView;

/**
 * 展示用户头像的原图
 */
public class PicturePreviewActivity extends BaseActivity implements OnViewTapListener {

    private PhotoDraweeView mPhotoDraweeView;
    protected String mImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null) {
            mImageUrl = getIntent().getStringExtra(Constants.Key.URL);
        }else{
            mImageUrl = savedInstanceState.getString(Constants.Key.URL);
        }
        initView();
        initComplete();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_picture_preview;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.Key.URL, mImageUrl);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPhotoDraweeView = null;
        mImageUrl = null;
    }

    private void initView() {
        mPhotoDraweeView = (PhotoDraweeView) findViewById(R.id.photoView);
        mPhotoDraweeView.setOnViewTapListener(this);
    }

    protected void initComplete() {
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.
                newBuilderWithSource(Uri.parse(mImageUrl))
                .setAutoRotateEnabled(true)
                .setLocalThumbnailPreviewsEnabled(true)
                .setResizeOptions(new ResizeOptions(DensityUtils.dp2px(this, 200), DensityUtils.dp2px(this, 200)));
        PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
        controller.setImageRequest(imageRequestBuilder.build());
        controller.setOldController(mPhotoDraweeView.getController());
        controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                if (imageInfo == null || mPhotoDraweeView == null) {
                    return;
                }
                mPhotoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
            }
        });
        mPhotoDraweeView.setController(controller.build());
    }

    @Override
    public void onViewTap(View view, float x, float y) {
        finish();
    }


}
