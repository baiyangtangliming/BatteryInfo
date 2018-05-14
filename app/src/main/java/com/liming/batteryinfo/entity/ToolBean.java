package com.liming.batteryinfo.entity;

/**
 * Created by tlm on 2018/5/13.
 */

public class ToolBean {
    private String itemTool;
    private String itemTip;
    private String itemNum;

    public String getItemTool() {
        return itemTool;
    }

    public void setItemTool(String itemTool) {
        this.itemTool = itemTool;
    }

    public String getItemTip() {
        return itemTip;
    }

    public void setItemTip(String itemTip) {
        this.itemTip = itemTip;
    }

    public String getItemNum() {
        return itemNum;
    }

    public void setItemNum(String itemNum) {
        this.itemNum = itemNum;
    }

    public ToolBean(String itemTool, String itemTip, String itemNum) {
        this.itemTool = itemTool;
        this.itemTip = itemTip;
        this.itemNum = itemNum;
    }
}
