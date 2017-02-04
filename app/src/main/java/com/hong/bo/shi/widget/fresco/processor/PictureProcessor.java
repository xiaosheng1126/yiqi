package com.hong.bo.shi.widget.fresco.processor;

import android.content.Context;
import android.graphics.Bitmap;

import com.facebook.imagepipeline.request.BaseRepeatedPostProcessor;

import java.util.LinkedList;

/**
 * fresco图片处理
 * Created by guchenkai on 2015/11/17.
 */
public class PictureProcessor extends BaseRepeatedPostProcessor {
    private Context mContext;
    private int mPosition;
    private ProcessorInterface mProcessorInterface;
    private LinkedList<ProcessorInterface> processorList = new LinkedList<>();

    public PictureProcessor(Context context) {
        mContext = context;
    }

    public PictureProcessor addProcessor(ProcessorInterface processorInterface) {
        processorList.add(processorInterface);
        return this;
    }

    public PictureProcessor addProcessor(ProcessorInterface processorInterface, int position) {
        mProcessorInterface = processorInterface;
        mPosition = position;
        return this;
    }

    @Override
    public void process(Bitmap bitmap) {
        for (ProcessorInterface processor : processorList) {
            processor.process(mContext, bitmap);
        }
        if (null != mProcessorInterface)
            mProcessorInterface.process(mContext, bitmap, mPosition);
    }
}
