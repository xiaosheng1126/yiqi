package com.hong.bo.shi.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import com.hong.bo.shi.app.App;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;

/**
 * SdCard操作工具
 */
public final class FileUtils {

    private FileUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断SDCard是否可用
     * @return
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡路径
     * @return
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 获取SD卡的剩余容量 单位byte
     * @return
     */
    public static long getSDCardFreeSize() {
        if (isSDCardEnable()) {
            StatFs stat = new StatFs(getSDCardPath());
            // 获取空闲的数据块的数量
            long availableBlocks = (long) stat.getAvailableBlocks() - 4;
            // 获取单个数据块的大小（byte）
            long freeBlocks = stat.getAvailableBlocks();
            return freeBlocks * availableBlocks;
        }
        return 0;
    }

    /**
     * 获取指定路径所在空间的剩余可用容量字节数，单位byte
     * @param filePath
     * @return 容量字节 SDCard可用空间，内部存储可用空间
     */
    public static long getFreeSize(String filePath) {
        // 如果是sd卡的下的路径，则获取sd卡可用容量
        if (filePath.startsWith(getSDCardPath())) {
            filePath = getSDCardPath();
        } else {// 如果是内部存储的路径，则获取内存存储的可用容量
            filePath = Environment.getDataDirectory().getAbsolutePath();
        }
        StatFs stat = new StatFs(filePath);
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        return stat.getBlockSize() * availableBlocks;
    }

    /**
     * 获取系统存储路径
     * @return
     */
    public static String getRootDirectoryPath() {
        return Environment.getRootDirectory().getAbsolutePath();
    }

    /**
     * 获取应用专属缓存目录
     * android 4.4及以上系统不需要申请SD卡读写权限
     * 因此也不用考虑6.0系统动态申请SD卡读写权限问题，切随应用被卸载后自动清空 不会污染用户存储空间
     * @param context 上下文
     * @param type 文件夹类型 可以为空，为空则返回API得到的一级目录
     * @return 缓存文件夹 如果没有SD卡或SD卡有问题则返回内存缓存目录，否则优先返回SD卡缓存目录
     */
    public static File getCacheDirectory(Context context, String type) {
        File appCacheDir = getExternalCacheDirectory(context, type);
        if (appCacheDir == null){
            appCacheDir = getInternalCacheDirectory(context, type);
        }
        if (appCacheDir == null){
            Log.e("getCacheDirectory","getCacheDirectory fail ,the reason is mobile phone unknown exception !");
        }else {
            if (!appCacheDir.exists()&&!appCacheDir.mkdirs()){
                Log.e("getCacheDirectory","getCacheDirectory fail ,the reason is make directory fail !");
            }
        }
        return appCacheDir;
    }

    /**
     * 获取SD卡缓存目录
     * @param context 上下文
     * @param type 文件夹类型 如果为空则返回 /storage/emulated/0/Android/data/app_package_name/cache
     *             否则返回对应类型的文件夹如Environment.DIRECTORY_PICTURES 对应的文件夹为 .../data/app_package_name/files/Pictures
     * {@link android.os.Environment#DIRECTORY_MUSIC},
     * {@link android.os.Environment#DIRECTORY_PODCASTS},
     * {@link android.os.Environment#DIRECTORY_RINGTONES},
     * {@link android.os.Environment#DIRECTORY_ALARMS},
     * {@link android.os.Environment#DIRECTORY_NOTIFICATIONS},
     * {@link android.os.Environment#DIRECTORY_PICTURES}, or
     * {@link android.os.Environment#DIRECTORY_MOVIES}.or 自定义文件夹名称
     * @return 缓存目录文件夹 或 null（无SD卡或SD卡挂载失败）
     */
    public static File getExternalCacheDirectory(Context context,String type) {
        File appCacheDir = null;
        if( Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if (TextUtils.isEmpty(type)){
                appCacheDir = context.getExternalCacheDir();
            }else {
                appCacheDir = context.getExternalFilesDir(type);
            }
            if (appCacheDir == null){// 有些手机需要通过自定义目录
                appCacheDir = new File(Environment.getExternalStorageDirectory(),
                        "Android/data/"+ context.getPackageName() +"/cache/" + type);
            }
            if (appCacheDir == null){
                Log.e("getExternalDirectory", "getExternalDirectory fail ,the reason is sdCard unknown exception !");
            }else {
                if (!appCacheDir.exists() && !appCacheDir.mkdirs()){
                    Log.e("getExternalDirectory","getExternalDirectory fail ,the reason is make directory fail !");
                }
            }
        }else {
            Log.e("getExternalDirectory","getExternalDirectory fail ,the reason is sdCard nonexistence or sdCard mount fail !");
        }
        return appCacheDir;
    }

    /**
     * 获取内存缓存目录
     * @param type 子目录，可以为空，为空直接返回一级目录
     * @return 缓存目录文件夹 或 null（创建目录文件失败）
     * 注：该方法获取的目录是能供当前应用自己使用，外部应用没有读写权限，如 系统相机应用
     */
    public static File getInternalCacheDirectory(Context context, String type) {
        File appCacheDir = null;
        if (TextUtils.isEmpty(type)){
            appCacheDir = context.getCacheDir();// /data/data/app_package_name/cache
        }else {
            appCacheDir = new File(context.getFilesDir(), type);// /data/data/app_package_name/files/type
        }
        if (!appCacheDir.exists()&&!appCacheDir.mkdirs()){
            Log.e("getInternalDirectory","getInternalDirectory fail ,the reason is make directory fail !");
        }
        return appCacheDir;
    }

    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
            total.append('\n');
        }
        return total.toString();
    }

    public static String combinePath(String path1, String path2) {
        if (TextUtils.isEmpty(path1)) {
            return path2;
        }
        if (TextUtils.isEmpty(path2)) {
            return path1;
        }
        boolean path1EndSlash = (path1.charAt(path1.length() - 1) == '/');
        boolean path2StartSlash = (path2.charAt(0) == '/');
        if ((path1EndSlash && !path2StartSlash) || (!path1EndSlash && path2StartSlash)) {
            return path1 + path2;
        }
        if (path1EndSlash && path2StartSlash) {
            return path1 + path2.substring(1);
        }
        return path1 + "/" + path2;
    }

    public static void copyFile(InputStream inputStream, File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte[] buf = new byte[20480];
        int c = inputStream.read(buf);
        while (c != -1) {
            fileOutputStream.write(buf, 0, c);
            c = inputStream.read(buf);
        }
        fileOutputStream.close();
    }

    public static Uri createTemplatePic() {
        return Uri.fromFile(new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg"));
    }

    /**
     * 获取文件的SHA1值
     *
     * @param file 目标文件
     * @return 文件的SHA1值
     */
    public static String getSHA1ByFile(File file) {
        if (file == null || !file.exists()) {
            Log.d("getSHA1ByFile", "文件不存在");
            return null;
        }
        long time = System.currentTimeMillis();
        InputStream in = null;
        String value = null;
        try {
            in = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            int numRead = 0;
            while (numRead != -1) {
                numRead = in.read(buffer);
                if (numRead > 0) digest.update(buffer, 0, numRead);
            }
            byte[] sha1Bytes = digest.digest();
            String t = new String(buffer);
            value = convertHashToString(sha1Bytes);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("getSHA1ByFile", e.toString());
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return value;
    }

    /**
     * @param hashBytes
     * @return
     */
    private static String convertHashToString(byte[] hashBytes) {
        String returnVal = "";
        for (int i = 0; i < hashBytes.length; i++) {
            returnVal += Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1);
        }
        return returnVal.toLowerCase();
    }

    public static String compressAndGenImage(String dest, int maxSize) {
        try {
            File destFile = new File(dest);
            if(destFile.length() <= maxSize * 1024 * 1024){
                return dest;
            }
            String newFileName = dest.substring(dest.lastIndexOf("/") + 1, dest.length());
            File file = new File(App.getInstance().getPictureCache(), newFileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            //scale
            int options = 100;
            Bitmap image = BitmapFactory.decodeFile(dest);
            // Store the bitmap into output stream(no compress)
            image.compress(Bitmap.CompressFormat.JPEG, options, os);
            // Compress by loop
            int size = os.toByteArray().length / 1024 / 1024;
            while (size > maxSize) {
                // Clean up os
                os.reset();
                // interval 10
                options -= 10;
                image.compress(Bitmap.CompressFormat.JPEG, options, os);
                size = os.toByteArray().length / 1024 / 1024;
            }
            // Generate compressed image file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(os.toByteArray());
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String bitmapToFile(Bitmap bitmap) throws IOException {
        File image = ImageUtils.createImageFile();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // scale
        int options = 100;
        // Store the bitmap into output stream(no compress)
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, os);
        // Generate compressed image file
        FileOutputStream fos = new FileOutputStream(image);
        fos.write(os.toByteArray());
        fos.flush();
        fos.close();
        return image.getAbsolutePath();
    }

    public static File createNewPic(){
        long time = System.currentTimeMillis();
        String fileName = String.format("%s.%s", time, "jpg");
        File file = new File(App.getInstance().getPictureCache(), fileName);
        return file;
    }

}
