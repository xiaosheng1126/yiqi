package com.hong.bo.shi.ui.activitys;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.hong.bo.shi.R;
import com.hong.bo.shi.app.App;
import com.hong.bo.shi.app.RequestHeader;
import com.hong.bo.shi.app.RequestUrl;
import com.hong.bo.shi.app.SubscriptionUtils;
import com.hong.bo.shi.base.BaseActivity;
import com.hong.bo.shi.dialog.PutaoDialog;
import com.hong.bo.shi.model.bean.UserInfo;
import com.hong.bo.shi.model.db.RealmHelper;
import com.hong.bo.shi.model.http.RetrofitHelper;
import com.hong.bo.shi.utils.DensityUtils;
import com.hong.bo.shi.utils.FileUtils;
import com.hong.bo.shi.utils.ImageUtils;
import com.hong.bo.shi.utils.ParseUtils;
import com.hong.bo.shi.utils.UIHelper;
import com.hong.bo.shi.widget.CommonTitle;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;

import me.relex.photodraweeview.OnViewTapListener;
import me.relex.photodraweeview.PhotoDraweeView;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

import static com.hong.bo.shi.app.Constants.REQUEST_EDIT_PHOTO_ALBUM_CODE;
import static com.hong.bo.shi.app.Constants.REQUEST_EDIT_PHOTO_CAMERA_CODE;

public class EditUserPhotoActivity extends BaseActivity implements PutaoDialog.OnDialogItemClickListener, OnViewTapListener,CommonTitle.OnRightClickListener{

    private String mPicPath;
    private File mDestFile = null;
    private PhotoDraweeView mPhotoDraweeView;
    private CommonTitle mCommonTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initComplete();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_user_photo;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismiss();
        unSubscribe();
        mCommonTitle = null;
        mPhotoDraweeView = null;
    }

    private void initView() {
        mCommonTitle = findViewByIds(R.id.commonTitle);
        mCommonTitle.setTitle("头像");
        mCommonTitle.setLeftView("详细资料");
        mCommonTitle.setRightView(R.mipmap.btn_more_w_nor);
        mCommonTitle.setOnRightClickListener(this);
        mCommonTitle.setOnLeftClickListener(this);
        mPhotoDraweeView = findViewByIds(R.id.photoView);
        mPhotoDraweeView.setOnViewTapListener(this);
    }

    protected void initComplete() {
        UserInfo userInfo = App.getInstance().getUserInfo();
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.
                newBuilderWithSource(Uri.parse(userInfo.getAvaturl()))
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

    @Override
    public void onRightClick(View view) {
        PutaoDialog.selectPic(this, this);
    }

    @Override
    public boolean onItemClick(PutaoDialog.Item item) {
        if (item.getId() == R.id.open_camera) {
            mPicPath = UIHelper.openCamera(this, REQUEST_EDIT_PHOTO_CAMERA_CODE);
        } else if (item.getId() == R.id.open_album) {
            UIHelper.openAlbum(this, REQUEST_EDIT_PHOTO_ALBUM_CODE);
        }
        return true;
    }

    @Override
    public void onLeftClick(View view) {
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == REQUEST_EDIT_PHOTO_CAMERA_CODE) {
            mDestFile = FileUtils.createNewPic();
            Crop.of(Uri.fromFile(new File(mPicPath)), Uri.fromFile(mDestFile)).asSquare().start(this);
        } else if (requestCode == REQUEST_EDIT_PHOTO_ALBUM_CODE) {
            if (data != null) {
                // 得到图片的全路径
                Uri selectedImage = data.getData();
                String picturePath = ImageUtils.getImageAbsolutePath(this, selectedImage);
                mDestFile = FileUtils.createNewPic();
                Crop.of(Uri.fromFile(new File(picturePath)), Uri.fromFile(mDestFile)).asSquare().start(this);
            }
        }else if(requestCode == Crop.REQUEST_CROP){
            upload(mDestFile.getAbsolutePath());
            mDestFile = null;
        }
    }

    public void upload(final String path) {
        showLoading();
        addSubscribe(SubscriptionUtils.getS(new Func1<String, String>() {
            @Override
            public String call(String s) {
                return request(path);
            }
        }, new Action1<String>() {
            @Override
            public void call(String s) {
                if (TextUtils.isEmpty(s)) {
                    Toast.makeText(App.getInstance(), "用户头像修改失败", Toast.LENGTH_SHORT).show();
                } else {
                    UserInfo userInfo = App.getInstance().getUserInfo();
                    userInfo.setAvaturl(s);
                    RealmHelper.updateUserInfo(userInfo);
                    initComplete();
                    Toast.makeText(App.getInstance(), "用户头像修改成功", Toast.LENGTH_SHORT).show();
                }
                dismiss();
            }
        }));
    }

    private String request(String path) {
        try {
            String string = RetrofitHelper.edit(RequestUrl.getType1022Url(),
                    RequestHeader.getEditUserPhotoHeader(), path);
            if (ParseUtils.isError(string)) {
                return null;
            }
            return string;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private CompositeSubscription mCompositeSubscription;

    private void unSubscribe() {
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
            mCompositeSubscription = null;
        }
    }

    private void addSubscribe(Subscription subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }
}
