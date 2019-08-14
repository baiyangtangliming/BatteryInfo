package com.liming.batteryinfo.ui;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.liming.batteryinfo.R;
import com.liming.batteryinfo.utils.AnnotateUtils;

import java.util.ArrayList;
import java.util.List;

public class BaseFragment extends Fragment {


    /**
     * 保存在手机里面的文件名
     */
    private static final String FILE_NAME = "share_date";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnnotateUtils.injectViews(this);
    }

    /**
     *播放动画
     * @param view
     */
    public ValueAnimator startAnimation(final View view) {

        Log.d("==========>", "startAnimation: "+view.getId());


        Resources resources = getResources();

        final List<Integer> colorList = new ArrayList<>();
        colorList.add(resources.getColor(R.color.app_color_theme_1));
        colorList.add(resources.getColor(R.color.app_color_theme_2));
        colorList.add(resources.getColor(R.color.app_color_theme_3));
        colorList.add(resources.getColor(R.color.app_color_theme_4));
        colorList.add(resources.getColor(R.color.app_color_theme_5));
        colorList.add(resources.getColor(R.color.app_color_theme_6));
        colorList.add(resources.getColor(R.color.app_color_theme_7));
        colorList.add(resources.getColor(R.color.app_color_theme_8));
        colorList.add(resources.getColor(R.color.app_color_theme_9));


        //创建动画,这里的关键就是使用ArgbEvaluator, 后面2个参数就是 开始的颜色,和结束的颜色.
         ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(),colorList.get(0) , colorList.get(1));

        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setBackgroundColor((int) animation.getAnimatedValue());//设置一下, 就可以看到效果.
                //Log.d("startAnimation："+System.currentTimeMillis(),"颜色===》"+animation.getAnimatedValue());
            }

        });
        colorAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ValueAnimator valueAnimator = (ValueAnimator) animator;
                colorList.add(colorList.get(0));
                colorList.remove(0);
                valueAnimator.setIntValues(colorList.get(0),colorList.get(1));
                valueAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        colorAnimator.setDuration(3000);
        colorAnimator.start();

        return colorAnimator;
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     * @param key
     * @param object
     */
    public void setParam(String key, Object object){
        String type = object == null ? "" : object.getClass().getSimpleName();
        SharedPreferences sp = getActivity().getBaseContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if("String".equals(type)){
            editor.putString(key, (String)object);
        }
        else if("Integer".equals(type)){
            editor.putInt(key, (Integer)object);
        }
        else if("Boolean".equals(type)){
            editor.putBoolean(key, (Boolean)object);
        }
        else if("Float".equals(type)){
            editor.putFloat(key, (Float)object);
        }
        else if("Long".equals(type)){
            editor.putLong(key, (Long)object);
        }

        editor.commit();
    }


    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     * @param
     * @param key
     * @param defaultObject
     * @return
     */
    public Object getParam(String key, Object defaultObject){
        String type = defaultObject.getClass().getSimpleName();
        SharedPreferences sp = getActivity().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

        if("String".equals(type)){
            return sp.getString(key, (String)defaultObject);
        }
        else if("Integer".equals(type)){
            return sp.getInt(key, (Integer)defaultObject);
        }
        else if("Boolean".equals(type)){
            return sp.getBoolean(key, (Boolean)defaultObject);
        }
        else if("Float".equals(type)){
            return sp.getFloat(key, (Float)defaultObject);
        }
        else if("Long".equals(type)){
            return sp.getLong(key, (Long)defaultObject);
        }

        return null;
    }
    /**
     * 清除所有数据
     * @param
     */
    public void clearAll() {
        SharedPreferences sp = getActivity().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear().commit();
    }

    /**
     * 清除指定数据
     */
    public void clear(String key) {
        SharedPreferences sp = getActivity().getBaseContext().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }


}
