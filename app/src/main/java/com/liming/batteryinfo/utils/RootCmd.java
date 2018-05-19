package com.liming.batteryinfo.utils;

/**
 * Created by tlm on 2018/5/13.
 */


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Android运行linux命令
 */
public final class RootCmd {
    private static boolean mHaveRoot = false;
    /**
     *   判断机器Android是否已经root，即是否获取root权限
     */
    public static boolean haveRoot() {
        if (!mHaveRoot) {
            int ret = execRootCmdSilent("echo test"); // 通过执行测试命令来检测
            if (ret != -1) {
                mHaveRoot = true;
            } else {
            }
        } else {
        }
        return mHaveRoot;
    }

    /**
     * 执行命令并且输出结果
     */
    public static String execRootCmd(String cmd) {
        String result = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;

        try {
            Process p = Runtime.getRuntime().exec("su");// 经过Root处理的android系统即有su命令
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            String line = null;
            while ((line = dis.readLine()) != null) {
                result += line;
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 执行命令但不关注结果输出
     */
    public static int execRootCmdSilent(String cmd) {
        int result = -1;
        DataOutputStream dos = null;

        try {
            Process p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            p.waitFor();
            result = p.exitValue();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
    /**
     * 获取属性
     *
     * @param propName 属性名称
     * @return
     */
    public static String getProp(String propName) {
        String content=getProp(propName, false);
        return content.isEmpty()?getProp(propName,true):content;
    }
    /**
     * 获取属性
     *
     * @param propName 属性名称
     * @return
     */
    public static String getProp(String propName, Boolean root) {
        if (!new File(propName).exists()) {
            return "";
        }
        try {
            Process p = Runtime.getRuntime().exec(root ? "su" : "sh");
            DataOutputStream out = new DataOutputStream(p.getOutputStream());
            out.write(("cat " + propName).getBytes("UTF-8"));
            out.writeBytes("\n");
            out.writeBytes("exit\n");
            out.writeBytes("exit\n");
            out.flush();

            InputStream inputstream = p.getInputStream();
            InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
            BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedreader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            out.flush();
            out.close();
            bufferedreader.close();
            inputstream.close();
            inputstreamreader.close();
            if (p.waitFor() != 0) {
                p.destroy();
                return "";
            } else  {
                p.destroy();
                return stringBuilder.toString().trim();
            }
        } catch (Exception e) {
        }
        return "";
    }
}
