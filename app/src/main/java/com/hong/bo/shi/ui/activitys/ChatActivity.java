package com.hong.bo.shi.ui.activitys;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;

import com.hong.bo.shi.R;
import com.hong.bo.shi.app.App;
import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.app.RequestHeader;
import com.hong.bo.shi.base.BaseMvpActivity;
import com.hong.bo.shi.klog.KLog;
import com.hong.bo.shi.model.bean.GroupInfo;
import com.hong.bo.shi.model.bean.GroupMessage;
import com.hong.bo.shi.model.bean.UserInfo;
import com.hong.bo.shi.presenter.ChatPresenter;
import com.hong.bo.shi.presenter.contract.ChatContract;
import com.hong.bo.shi.ui.fragments.ChatBottomFragment;
import com.hong.bo.shi.utils.DateUtils;
import com.hong.bo.shi.utils.FileUtils;
import com.hong.bo.shi.utils.ImageUtils;
import com.hong.bo.shi.utils.UIHelper;
import com.hong.bo.shi.widget.CommonTitle;
import com.sh.shvideolibrary.VideoInputDialog;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static com.hong.bo.shi.app.Constants.REQUEST_CHAT_ALBUM_CODE;
import static com.hong.bo.shi.app.Constants.REQUEST_CHAT_CAMERA_CODE;
import static com.hong.bo.shi.app.Constants.REQUEST_LOCATION_CODE;
import static com.hong.bo.shi.app.Constants.REQUEST_VIDEO_CODE;

public class ChatActivity extends BaseMvpActivity<ChatContract.Presenter, ChatContract.View>
        implements ChatBottomFragment.OnBottomOperationListener, CommonTitle.OnRightClickListener,VideoInputDialog.VideoCall {
    private ChatBottomFragment mChatBottomFragment;
    private GroupInfo mGroupInfo;
    private String mPicPath;//拍照图片的路径
    private File mDestFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null) {
            mGroupInfo = getIntent().getParcelableExtra(GroupInfo.class.getSimpleName());
        }else{
            mGroupInfo = savedInstanceState.getParcelable(GroupInfo.class.getSimpleName());
            mPicPath = savedInstanceState.getString(Constants.Key.PATH);
        }
        mChatBottomFragment = mView.initEmotionMainFragment(getSupportFragmentManager());
        onNewIntent(getIntent());
        mPresenter.query(mGroupInfo.getGuid());
        mView.init(mGroupInfo);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected ChatContract.View createView() {
        return findViewByIds(R.id.chat_view);
    }

    @Override
    protected ChatContract.Presenter createPresenter() {
        return new ChatPresenter(mView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(GroupInfo.class.getSimpleName(), mGroupInfo);
        if(!TextUtils.isEmpty(mPicPath)) {
            outState.putString(Constants.Key.PATH, mPicPath);
        }
    }

    @Override
    public void onLeftClick(View view) {
        if (!mChatBottomFragment.isInterceptBackPress()) {
            super.onLeftClick(view);
        }
    }

    @Override
    public void sendText(String text) {
        sendMessage(0, text, "0");
    }

    @Override
    public void sendAudio(String filePath, long time) {
        sendMessage(3, filePath, String.valueOf(time));
    }

    @Override
    public void openCamera() {
        mPicPath = UIHelper.openCamera(this, REQUEST_CHAT_CAMERA_CODE);
    }

    @Override
    public void openAlbum() {
        UIHelper.openAlbum(this, REQUEST_CHAT_ALBUM_CODE);
    }

    @Override
    public void openVideo() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        KLog.d(dm.density);
        if(dm.density > 2) {
            //显示视频录制控件
            VideoInputDialog.show(getSupportFragmentManager(), this,
                    VideoInputDialog.Q480, createRecordDir(), this);
        }else{
            //显示视频录制控件
            VideoInputDialog.show(getSupportFragmentManager(), this,
                    VideoInputDialog.Q720, createRecordDir(), this);
        }
    }

    @Override
    public void openLocation() {
        UIHelper.openLocation(this, REQUEST_LOCATION_CODE);
    }

    @Override
    public void startRecoredAudio() {
        mView.onPause();//开始录音时停止播放语音
    }

    @Override
    public void onRightClick(View view) {
        Intent intent = new Intent(this, GroupDetailsActivity.class);
        intent.putExtra(GroupInfo.class.getSimpleName(), mGroupInfo);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == REQUEST_CHAT_CAMERA_CODE) {
            mDestFile = FileUtils.createNewPic();
            Crop.of(Uri.fromFile(new File(mPicPath)), Uri.fromFile(mDestFile)).start(this);
        } else if (requestCode == REQUEST_CHAT_ALBUM_CODE) {
            if (data != null) {
                // 得到图片的全路径
                Uri selectedImage = data.getData();
                String picturePath = ImageUtils.getImageAbsolutePath(this, selectedImage);
                mDestFile = FileUtils.createNewPic();
                Crop.of(Uri.fromFile(new File(picturePath)), Uri.fromFile(mDestFile)).start(this);
            }
        } else if (requestCode == REQUEST_VIDEO_CODE) {
            String path = data.getStringExtra(Constants.Key.PATH);
            sendMessage(2, path, "0");
        }else if(requestCode == REQUEST_LOCATION_CODE){
            String filePath = data.getStringExtra(Constants.Key.PATH);
            String lot = data.getStringExtra(Constants.Key.LOT);
            String lat = data.getStringExtra(Constants.Key.LAT);
            String address = data.getStringExtra(Constants.Key.ADDRESS);
            StringBuilder builder = new StringBuilder(32);
            try {
                builder.append(lot).append(RequestHeader.SPLIT)
                        .append(lat).append(RequestHeader.SPLIT)
                        .append(RequestHeader.encode(address));
                sendMessage(4, filePath, builder.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else if(requestCode == Crop.REQUEST_CROP){
            sendMessage(1, mDestFile.getAbsolutePath(), "0");
            mDestFile = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mView.onPause();
        mPresenter.cleraGroupInfoUnReadCount();
    }

    private void sendMessage(int msgType, String text, String time) {
        UserInfo userInfo = App.getInstance().getUserInfo();
        GroupMessage message = new GroupMessage();
        message.setFromUserAvaturl(userInfo.getAvaturl());
        message.setFromUserGuid(userInfo.getGuid());
        message.setFromUserName(userInfo.getName());
        message.setGuid(UUID.randomUUID().toString());
        message.setGroupGuid(mGroupInfo.getGuid());
        message.setStatue(2);
        if(msgType != 0){
            message.setFilePath(text);
        }
        message.setMessage(text);
        message.setMsgType(msgType);
        message.setTime(time);
        message.setMsgAttribute(1);
        message.setMsgTime(DateUtils.getFormatDate(System.currentTimeMillis()));
        message.setCreateTime(System.currentTimeMillis());
        mPresenter.sendMessage(message);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent != null){
            GroupInfo extra = intent.getParcelableExtra(GroupInfo.class.getSimpleName());
            mView.setBackTitle(getIntent().getStringExtra(Constants.Key.BACK_TITLE));
            if(extra != null && !extra.getGuid().equals(mGroupInfo.getGuid())){
                mPresenter.cleraGroupInfoUnReadCount();
                mGroupInfo = extra;
                mPresenter.query(mGroupInfo.getGuid());
                mView.init(mGroupInfo);
            }
        }
    }

    @Override
    public void videoPathCall(String path) {
        UIHelper.previewVideo(this, path);
    }

    public static String createRecordDir() {
        File dir = new File(Environment.getExternalStorageDirectory(), "video");
        if(!dir.exists()){
            dir.mkdir();
        }
        //创建文件
        File videoFile = new File(dir, generateFileName());//mp4格式
        if(!videoFile.exists()){
            try {
                videoFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return videoFile.getAbsolutePath();
    }

    /**
     * 随机生成文件名
     * @return
     */
    private static String generateFileName() {
        return System.currentTimeMillis() + ".mp4";
    }

}
