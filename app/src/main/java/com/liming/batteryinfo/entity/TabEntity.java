package com.liming.batteryinfo.entity;

import android.app.Fragment;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TabEntity {
    private Fragment view;
    private LinearLayout tabLayout;
    private ImageView image;
    private TextView text;

    private int selectImage;
    private int notSelectImage;

    private int selectColor;

    public TabEntity(Fragment view, LinearLayout tabLayout, ImageView image, TextView text) {
        this.view = view;
        this.tabLayout = tabLayout;
        this.image = image;
        this.text = text;
    }

    public TabEntity(Fragment view, LinearLayout tabLayout, ImageView image, TextView text, int selectImage, int notSelectImage, int selectColor) {
        this.view = view;
        this.tabLayout = tabLayout;
        this.image = image;
        this.text = text;
        this.selectImage = selectImage;
        this.notSelectImage = notSelectImage;
        this.selectColor = selectColor;
    }

    public int getSelectImage() {
        return selectImage;
    }

    public void setSelectImage(int selectImage) {
        this.selectImage = selectImage;
    }

    public int getNotSelectImage() {
        return notSelectImage;
    }

    public void setNotSelectImage(int notSelectImage) {
        this.notSelectImage = notSelectImage;
    }

    public int getSelectColor() {
        return selectColor;
    }

    public void setSelectColor(int selectColor) {
        this.selectColor = selectColor;
    }

    public LinearLayout getTabLayout() {
        return tabLayout;
    }

    public void setTabLayout(LinearLayout tabLayout) {
        this.tabLayout = tabLayout;
    }

    public Fragment getView() {
        return view;
    }

    public void setView(Fragment view) {
        this.view = view;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    public TextView getText() {
        return text;
    }

    public void setText(TextView text) {
        this.text = text;
    }
}
