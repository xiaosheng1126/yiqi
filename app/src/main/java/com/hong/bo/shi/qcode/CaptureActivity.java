package com.hong.bo.shi.qcode;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.hong.bo.shi.R;
import com.hong.bo.shi.base.BaseActivity;
import com.hong.bo.shi.klog.KLog;
import com.hong.bo.shi.utils.DensityUtils;
import com.hong.bo.shi.utils.ImageUtils;
import com.hong.bo.shi.utils.UIHelper;
import com.hong.bo.shi.widget.CommonTitle;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 二维码识别
 * Created by riven_chris on 2015/11/4.
 */
public class CaptureActivity extends BaseActivity {

    private final int ALBUM_REQCODE = 2;//相册选择
    private CameraPreview mPreview;
    private Camera mCamera;
    private Handler autoFocusHandler;
    private CameraManager mCameraManager;
    private FrameLayout scanPreview;
    private RelativeLayout scanContainer;
    private ImageView scanCropView;
    private ImageView scan_line;

    private Rect mCropRect = null;
    private String picturePath;
    private boolean previewing = true;
    private byte[] mResultByte;
    private String mResult;
    private int time = 0;

    private Camera.PreviewCallback previewCb;
    private HandlerThread mHandlerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonTitle commonTitle = findViewByIds(R.id.commonTitle);
        commonTitle.setLeftView("业务");
        commonTitle.setOnLeftClickListener(this);
        commonTitle.setTitle("扫一扫");
        initViews();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_capture;
    }

    private void initViews() {
        scanPreview = (FrameLayout) findViewById(R.id.capture_preview);
        scanCropView = (ImageView) findViewById(R.id.capture_crop_view);
        scan_line = (ImageView) findViewById(R.id.scan_line);
        scanContainer = (RelativeLayout) findViewById(R.id.capture_container);
        mHandlerThread = new HandlerThread("calculation");
        mHandlerThread.start();
        Looper looper = mHandlerThread.getLooper();
        final Handler handler = new Handler(looper);
        previewCb = new Camera.PreviewCallback() {
            public void onPreviewFrame(byte[] data, Camera camera) {
                mResultByte = data;
                handler.post(doResult);
            }
        };
        autoFocusHandler = new Handler();
        mCameraManager = new CameraManager(this);
        try {
            mCameraManager.openDriver();
            mCamera = mCameraManager.getCamera();
            mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
            scanPreview.addView(mPreview);
            initAnimation();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "相机打开失败,请打开相机", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initAnimation() {
        int height = DensityUtils.dp2px(this, 150);
        ObjectAnimator rotationX = ObjectAnimator
                .ofFloat(scan_line, "translationY", -height, height);
        rotationX.setRepeatCount(-1);
        rotationX.setRepeatMode(ValueAnimator.RESTART);
        rotationX.setDuration(1200);
        rotationX.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPreview == null) {
            initViews();
        }
    }

    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraManager.closeDriver();
        scan_line.clearAnimation();
        mHandlerThread = null;
        mResultByte = null;
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ALBUM_REQCODE://相册选择
                    Uri selectedImage = data.getData();
                    picturePath = ImageUtils.getImageAbsolutePath(CaptureActivity.this, selectedImage);
                    handler.post(doLocalResult);
                    break;
            }
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
            scanPreview.removeView(mPreview);
            mPreview = null;
        }
    }

    private void stopPreview() {
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
                previewing = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化截取的矩形区域
     */
    private void initCrop() {
        int cameraWidth = mCameraManager.getCameraResolution().y;
        int cameraHeight = mCameraManager.getCameraResolution().x;

        /** 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        scanCropView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1] - getStatusBarHeight();

        int cropWidth = scanCropView.getWidth();
        int cropHeight = scanCropView.getHeight();

        /** 获取布局容器的宽高 */
        int containerWidth = scanContainer.getWidth();
        int containerHeight = scanContainer.getHeight();

        /** 计算最终截取的矩形的左上角顶点x坐标 */
        int x = cropLeft * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的左上角顶点y坐标 */
        int y = cropTop * cameraHeight / containerHeight;

        /** 计算最终截取的矩形的宽度 */
        int width = cropWidth * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的高度 */
        int height = cropHeight * cameraHeight / containerHeight;

        /** 生成最终的截取的矩形 */
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 处理结果
     *
     * @param scanResult 扫描结果
     */
    private void processor(final String scanResult) {
        KLog.i(scanResult);
        UIHelper.showScanResult(this, scanResult);
        finish();
    }

    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height, Rect rect) {
        if (rect == null) {
            return null;
        }
        // Go ahead and assume it's YUV rather than die.
        PlanarYUVLuminanceSource source = null;

        try {
            source = new PlanarYUVLuminanceSource(data, width / 2, height / 2, rect.left / 2, rect.top / 2,
                    rect.width() / 2, rect.height() / 2, false);
        } catch (Exception e) {
        }

        return source;
    }

    class BitmapLuminanceSource extends LuminanceSource {

        private byte bitmapPixels[];

        protected BitmapLuminanceSource(Bitmap bitmap) {
            super(bitmap.getWidth(), bitmap.getHeight());

            // 取得该图片的像素数组内容
            int[] data = new int[bitmap.getWidth() * bitmap.getHeight()];
            this.bitmapPixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
            bitmap.getPixels(data, 0, getWidth(), 0, 0, getWidth(), getHeight());

            // 将int数组转换为byte数组，取像素值中蓝色值部分作为辨析内容
            for (int i = 0; i < data.length; i++) {
                this.bitmapPixels[i] = (byte) data[i];
            }
        }

        @Override
        public byte[] getMatrix() {
            // 返回生成好的像素数据
            return bitmapPixels;
        }

        @Override
        public byte[] getRow(int y, byte[] row) {
            // 得到像素数据
            System.arraycopy(bitmapPixels, y * getWidth(), row, 0, getWidth());
            return row;
        }
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength),
                Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 扫描二维码后加载过程如超过10秒则停止加载，跳出提示语“网络不给力，稍后重试”2s后消失
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time++;
            Message message = handler.obtainMessage();
            message.arg1 = time;
            handler.sendMessage(message);
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 >= 10) {
//                ToastUtils.showToastShort(mContext, "网络不给力，稍后重试");
                time = 0;
                handler.removeCallbacks(runnable);
            } else
                handler.postDelayed(runnable, 1000);
        }
    };

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    //2016/8/18  不用ZBarDecoder改成用ZXing库
    private Runnable doResult = new Runnable() {
        public void run() {
            MultiFormatReader multiFormatReader = new MultiFormatReader();
            try {
                Camera.Size size = mCamera.getParameters().getPreviewSize();
                // 处理异常
                if (mResultByte == null || mResultByte.length == -1) return;
                // 这里需要将获取的data翻转一下，因为相机默认拿的的横屏的数据
                byte[] rotatedData = new byte[mResultByte.length / 4];
                for (int y = 0; y < size.height / 2; y++) {
                    for (int x = 0; x < size.width / 2; x++)
                        rotatedData[x * size.height / 2 + size.height / 2 - y - 1] = mResultByte[2 * x + 2 * y * size.width];
                }
                // 宽高也要调整
                int tmp = size.width;
                size.width = size.height;
                size.height = tmp;

                // 可以解析的编码类型
                List<BarcodeFormat> decodeFormats = new ArrayList<BarcodeFormat>();
                decodeFormats.add(BarcodeFormat.QR_CODE);
                // 解码的参数
                Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>(2);
                hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
                // 设置继续的字符编码格式为UTF8
                hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
                // 设置解析配置参数
                multiFormatReader.setHints(hints);
                // 开始对图像资源解码
                Result rawResult = null;

                initCrop();
                PlanarYUVLuminanceSource source =
                        buildLuminanceSource(rotatedData, size.width, size.height, mCropRect);

                if (source != null) {
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                    rawResult = multiFormatReader.decodeWithState(bitmap);
                    mResult = rawResult.getText();
                }
                if (!TextUtils.isEmpty(mResult)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            processor(mResult);//处理结果
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                multiFormatReader.reset();
            }
        }
    };

    //2016/8/18  不用ZBarDecoder改成用ZXing库
    private Runnable doLocalResult = new Runnable() {
        public void run() {
            MultiFormatReader multiFormatReader = new MultiFormatReader();
            // 可以解析的编码类型
            List<BarcodeFormat> decodeFormats = new ArrayList<BarcodeFormat>();
            decodeFormats.add(BarcodeFormat.QR_CODE);
            // 解码的参数
            Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>(2);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
            // 设置继续的字符编码格式为UTF8
            hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
            // 设置解析配置参数
            multiFormatReader.setHints(hints);
            // 开始对图像资源解码
            Result rawResult = null;
            //加载图片时动态获取采样率，防止OOM
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(picturePath, opts);
            opts.inSampleSize = computeSampleSize(opts, -1, 512 * 512);
            opts.inJustDecodeBounds = false;
            try {
                rawResult = multiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(
                        new BitmapLuminanceSource(BitmapFactory.decodeFile(picturePath, opts)))));
                mResult = rawResult.getText();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                multiFormatReader.reset();
            }
            if (!TextUtils.isEmpty(mResult)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       processor(mResult);//处理结果
                    }
                });
            }
        }
    };

    // Mimic continuous auto-focusing
    private Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };
}
