package com.hong.bo.shi.ui.fragments;

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
import com.hong.bo.shi.base.BaseActivity;
import com.hong.bo.shi.base.BaseFragment;
import com.hong.bo.shi.model.bean.GroupMessage;
import com.hong.bo.shi.utils.DensityUtils;

import java.io.File;

import me.relex.photodraweeview.OnViewTapListener;
import me.relex.photodraweeview.PhotoDraweeView;

/**
 * Created by andy on 16/9/14.
 */

public class PictrueBrowseFragment extends BaseFragment implements OnViewTapListener {

    private PhotoDraweeView mPhotoDraweeView;
    private GroupMessage mInfo;

    @Override
    public void initView() {
        Bundle bundle = getArguments();
        mInfo = bundle.getParcelable(GroupMessage.class.getSimpleName());
        mPhotoDraweeView = (PhotoDraweeView) findViewById(R.id.photoView);
        mPhotoDraweeView.setOnViewTapListener(this);
        mPhotoDraweeView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        Uri uri = null;
        if(mInfo.getStatue() == 0){
            uri = Uri.parse(mInfo.getTime());
        }else{
            uri = Uri.fromFile(new File(mInfo.getFilePath()));
        }
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.
                newBuilderWithSource(uri)
                .setAutoRotateEnabled(true)
                .setLocalThumbnailPreviewsEnabled(true)
                .setResizeOptions(new ResizeOptions(DensityUtils.dp2px(getContext(), 200),
                        DensityUtils.dp2px(getContext(), 200)));
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
    public int getLayoutId() {
        return R.layout.activity_picture_preview;
    }

    @Override
    public void onViewTap(View view, float x, float y) {
        ((BaseActivity)getContext()).finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPhotoDraweeView = null;
    }
}
