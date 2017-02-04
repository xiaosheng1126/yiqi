package com.hong.bo.shi.model.bean;

/**
 * Created by Administrator on 2016/7/28.
 */
public class Emoji {

    public String sourcePath;
    public String text;

    public Emoji(String sourcePath, String name)
    {
        this.sourcePath = sourcePath;
        this.text = name;
    }

    public Emoji(int sourceId, String name)
    {
        this.text = name;
    }

    public Emoji(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        return text.equals(((Emoji)o).text);
    }
}


