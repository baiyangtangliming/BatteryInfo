package com.liming.batteryinfo.utils;

import android.app.Activity;
import android.app.Fragment;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AnnotateUtils {


    private static Fragment fragment;

    public static void injectViews(Activity activity) {
        Class<? extends Activity> object = activity.getClass(); // 获取activity的Class
        Field[] fields = object.getDeclaredFields(); // 通过Class获取activity的所有字段
        for (Field field : fields) { // 遍历所有字段
            // 获取字段的注解，如果没有ViewInject注解，则返回null
            ViewInject viewInject = field.getAnnotation(ViewInject.class);
            if (viewInject != null) {
                int viewId = viewInject.value(); // 获取字段注解的参数，这就是我们传进去控件Id
                if (viewId != -1) {
                    try {
                        // 获取类中的findViewById方法，参数为int
                        Method method = object.getMethod("findViewById", int.class);
                        // 执行该方法，返回一个Object类型的View实例
                        Object resView = method.invoke(activity, viewId);
                        field.setAccessible(true);
                        // 把字段的值设置为该View的实例
                        field.set(activity, resView);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 绑定view
     * @param view
     */
    public static void bindView(View view){

        if (fragment == null){
            return;
        }

        Class<? extends Fragment> object = fragment.getClass(); // 获取activity的Class
        Field[] fields = object.getDeclaredFields(); // 通过Class获取activity的所有字段
        for (Field field : fields) { // 遍历所有字段
            // 获取字段的注解，如果没有ViewInject注解，则返回null
            ViewInject viewInject = field.getAnnotation(ViewInject.class);
            if (viewInject != null) {
                int viewId = viewInject.value(); // 获取字段注解的参数，这就是我们传进去控件Id
                if (viewId != -1) {
                    try {
                        // 获取类中的findViewById方法，参数为int
                        //Method method = object.getMethod("findViewById", int.class);
                        View resView = view.findViewById(viewId);
                        // 执行该方法，返回一个Object类型的View实例
                        //Object resView = method.invoke(activity, viewId);
                        field.setAccessible(true);
                        // 把字段的值设置为该View的实例
                        field.set(fragment, resView);
                    }  catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void injectViews(Fragment f) {
        fragment = f;
    }
}