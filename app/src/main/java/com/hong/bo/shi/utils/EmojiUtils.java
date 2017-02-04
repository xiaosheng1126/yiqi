package com.hong.bo.shi.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.hong.bo.shi.model.bean.Emoji;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/7/28.
 */
public class EmojiUtils {

    public static List<Emoji> emojis;
    private static File emojiDirectoryPath;
    private static final String fileName = "img.zip";

    public static void init(Context context) {
        if (emojis == null || emojis.size() <= 0) {
            emojis = loadTabsAndEmojis(context);
        }
    }

    private static List<Emoji> loadTabsAndEmojis(Context context) {
        emojiDirectoryPath = context.getApplicationContext().getFilesDir();
        File file = new File(emojiDirectoryPath, fileName);
        try {
            FileUtils.copyFile(context.getApplicationContext().getAssets().open(fileName), file);
        } catch (IOException e) {
        }
        File directory = new File(emojiDirectoryPath, "img");
        if (directory.exists()) {
            deleteFile(directory);
        }
        if (!directory.exists()) {
            ZipFileUtils.unzip(new File(emojiDirectoryPath, fileName), directory);
        }
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File tabFile : directory.listFiles()) {
                if(tabFile.getAbsolutePath().endsWith("img")) {
                    return generateEmojis(tabFile.getAbsolutePath());
                }
            }
        }
        return null;
    }

    public static List<Emoji> generateEmojis(String filePath) {
        List<Emoji> list = new ArrayList<>();
        try {
            File json = new File(new File(filePath), "config.json");
            String jsonString = FileUtils.convertInputStreamToString(new FileInputStream(json));
            JSONArray emojiArray = new JSONArray(jsonString);
            for (int i = 0; i < emojiArray.length(); i++) {
                JSONObject emoji = emojiArray.optJSONObject(i);
                Emoji e = new Emoji(FileUtils.combinePath(filePath, emoji.optString("Motion")), emoji.optString("Text"));
                list.add(e);
            }
        }
        catch (IOException e)
        {
        }
        catch (JSONException e) {
        }
        return list;
    }
    private static void deleteFile(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
            return;
        }
        for (File file1 : file.listFiles()) {
            deleteFile(file1);
        }
        file.delete();
    }

    public static Emoji getEmojiByTextInEmojiGroups(String text) {
        int indexOf1 = emojis.indexOf(new Emoji(text));
        if (indexOf1 == -1) {
            return null;
        }
        return emojis.get(indexOf1);
    }

    public static SpannableString getEmotionContent(Context context, int size, String source) {
        SpannableString spannableString = new SpannableString(source);
//        String regexEmotion = "\\[([\u4e00-\u9fa5\\w])+\\]";
//        String regexEmotion = "\\[emoji\\]([\\s\\S]*?)\\[\\/emoji\\]";
        String regexEmotion = "\\<img([\\s\\S]*?)\\/\\>";
//        String regexEmotion = "<img.*src\\s*=\"\\s*(.*?)[^>]*?\"\\/>";
//        String regexEmotion = "\\[([\\s\\S]*?)\\]";
        Pattern patternEmotion = Pattern.compile(regexEmotion);
        Matcher matcherEmotion = patternEmotion.matcher(spannableString);
        while (matcherEmotion.find()) {
            // 获取匹配到的具体字符
            String key = matcherEmotion.group();
            // 匹配字符串的开始位置
            int start = matcherEmotion.start();
            // 利用表情名字获取到对应的图片
            Emoji emoji = EmojiUtils.getEmojiByTextInEmojiGroups(key);
            if (emoji != null) {
                // 压缩表情图片
                Bitmap bitmap = BitmapFactory.decodeFile(emoji.sourcePath);
                Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
                ImageSpan span = new ImageSpan(context, scaleBitmap);
                spannableString.setSpan(span, start, start + key.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }
}
