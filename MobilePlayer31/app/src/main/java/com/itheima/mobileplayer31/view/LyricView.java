package com.itheima.mobileplayer31.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.itheima.mobileplayer31.R;
import com.itheima.mobileplayer31.bean.LyricBean;
import com.itheima.mobileplayer31.util.LyricLoader;
import com.itheima.mobileplayer31.util.LyricParser;

import java.util.ArrayList;

/**
 * Created by ThinkPad on 2016/11/28.
 */

public class LyricView extends View {

    private Paint paint;
    private int viewH;
    private int viewW;
    private ArrayList<LyricBean> lyricBeens = new ArrayList<LyricBean>();
    private int centerLine;
    private int lineHeight;
    private float smallSize;
    private float bigSize;
    private int halfWhite;
    private int green;
    private int progress;
    private int duration;
    private boolean stop = false;
    private float startY;
    private int offsetY;
    private OnProgressChangeListener onProgressChangeListener;

    public LyricView(Context context) {
        super(context);
        initView();
    }

    public LyricView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LyricView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewW = w;
        viewH = h;
    }

    private void initView() {
        //抗锯齿
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextAlign(Paint.Align.CENTER);//绘制文本  文本坐标在x方向是以居中的位置进行确定
        green = getResources().getColor(R.color.green);
        halfWhite = getResources().getColor(R.color.halfwhite);
        bigSize = getResources().getDimension(R.dimen.bigSize);
        smallSize = getResources().getDimension(R.dimen.smallSize);
        lineHeight = getResources().getDimensionPixelSize(R.dimen.lineHeight);
        paint.setTextSize(bigSize);
        paint.setColor(green);

//        lyricBeens = new ArrayList<LyricBean>();
//        for (int i = 0; i < 30; i++) {
//            lyricBeens.add(new LyricBean(i*2000,"正在播放第"+i+"行歌词"));
//        }
//        centerLine = 10;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        drawSingleLine(canvas);
        drawMultipleLine(canvas);
    }
    //绘制多行歌词
    private void drawMultipleLine(Canvas canvas) {
        if(!stop) {
//        行可用时间：
            int lineTime;
//        居中行为最后一行：
            if (centerLine == lyricBeens.size() - 1) {
//        行可用时间=总时长-最后一行开始时间
                lineTime = duration - lyricBeens.get(centerLine).getStartTime();
            } else {
//        居中行为其他行：
//        行可用时间=下一行开始时间-居中行开始时间
                lineTime = lyricBeens.get(centerLine + 1).getStartTime() - lyricBeens.get(centerLine).getStartTime();
            }
//        偏移时间=progress-居中行开始时间
            int offsetTime = progress - lyricBeens.get(centerLine).getStartTime();
//        偏移百分比=偏移时间/行可用时间
            float offsetPercent = offsetTime / (float) lineTime;
//        偏移y=偏移时间百分比*行高
            offsetY = (int) (offsetPercent * lineHeight);
        }

        String text = lyricBeens.get(centerLine).getContent();
        Rect bounds = new Rect();
        paint.getTextBounds(text,0,text.length(),bounds);
        int centerH = bounds.height();
        //        Y = viewH/2+textH/2
//        居中行y=centerY-偏移y
        int centerY = viewH/2+centerH/2-offsetY;
        for (int i = 0; i < lyricBeens.size(); i++) {
            if(i==centerLine){
                paint.setTextSize(bigSize);
                paint.setColor(green);
            }else {
                paint.setTextSize(smallSize);
                paint.setColor(halfWhite);
            }
//        x=viewW/2
//        y=centery+(当前行行号-居中行行号)*行高
            int curY = centerY+(i-centerLine)*lineHeight;
            if(curY<0||curY>viewH+lineHeight) continue;
            canvas.drawText(lyricBeens.get(i).getContent(),viewW/2,curY,paint);
        }
    }

    private void drawSingleLine(Canvas canvas) {
        String text = "正在加载歌词...";
        Rect bounds = new Rect();
        paint.getTextBounds(text,0,text.length(),bounds);
        int textW = bounds.width();
        int textH = bounds.height();
//        x=viewW/2-textW/2
//        int x = viewW/2-textW/2;
//        Y = viewH/2+textH/2
        int y = viewH/2+textH/2;
        canvas.drawText(text,viewW/2,y,paint);
    }

    public void playLyric(int progress,int duration){
        if(stop) return;
        this.duration = duration;
        this.progress = progress;
//        progress
//        先判断居中行是否为最后一行：
//        progress>=最后一行开始时间
//        居中行=最后一行
        if(progress>=lyricBeens.get(lyricBeens.size()-1).getStartTime()){
            centerLine = lyricBeens.size()-1;
        }else {
//        遍历集合：
            for (int i = 0; i < lyricBeens.size() - 1; i++) {
//        progress>=当前行开始时间&&progress<下一行开始时间
//        居中行=当前行
                int curTime = lyricBeens.get(i).getStartTime();
                int nextTime = lyricBeens.get(i+1).getStartTime();
                if(progress>=curTime&&progress<nextTime){
                    centerLine = i;
                    break;
                }
            }
        }
        //绘制多行文本
        invalidate();
    }

    //设置歌词文件
    public void setFile(String display_name){
        this.lyricBeens.clear();
        this.lyricBeens.addAll(LyricParser.parseLyric(LyricLoader.loadLyricFile(display_name)));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startY = event.getY();
                stop = true;
                break;
            case MotionEvent.ACTION_MOVE:
                float endY = event.getY();
                float offY = startY-endY;
                //手指移动超出一个行高 重新确定居中行
                if(Math.abs(offY)>lineHeight){
                    int offsetLine = (int) (offY / lineHeight);
                    centerLine = centerLine+offsetLine;
                    //保证居中行合法
                    if(centerLine<0){
                        centerLine=0;
                    }else if(centerLine>lyricBeens.size()-1){
                        centerLine = lyricBeens.size()-1;
                    }
                    //调节进度
                    if(onProgressChangeListener!=null){
                        onProgressChangeListener.onProgressChange(lyricBeens.get(centerLine).getStartTime());
                    }
                    startY = endY;
                }
                offsetY = (int) offY%lineHeight;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                stop = false;
                break;
        }
        return true;
    }
    public interface OnProgressChangeListener{
        void onProgressChange(int progress);
    }
    public void setOnProgressChangeListener(OnProgressChangeListener onProgressChangeListener){
        this.onProgressChangeListener = onProgressChangeListener;
    }
}
