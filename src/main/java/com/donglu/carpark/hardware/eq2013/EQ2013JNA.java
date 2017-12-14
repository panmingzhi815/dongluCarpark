package com.donglu.carpark.hardware.eq2013;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Created by panmingzhi on 2016/11/21 0021.
 */ //EQ2008动态库接口定义
public interface EQ2013JNA extends StdCallLibrary {
    // 1、节目操作函数组===================================================
    // 添加节目
    int User_AddProgram(int CardNum, boolean bWaitToEnd, int iPlayTime);

    // 删除所有节目
    boolean User_DelAllProgram(int CardNum);

    // 添加图文区
    int User_AddBmpZone(int CardNum, User_Bmp pBmp, int iProgramIndex);

    boolean User_AddBmp(int CardNum, int iBmpPartNum, WinNT.HANDLE hBitmap,
                        User_MoveSet pMoveSet, int iProgramIndex);

    boolean User_AddBmpFile(int CardNum, int iBmpPartNum,
                            String strFileName, User_MoveSet pMoveSet, int iProgramIndex);

    // 添加文本区
    int User_AddText(int CardNum, User_Text pText, int iProgramIndex);

    // 添加RTF区
    int User_AddRTF(int CardNum, User_RTF pRTF, int iProgramIndex);

    // 添加单行文本区
    int User_AddSingleText(int CardNum, User_SingleText pSingleText,
                           int iProgramIndex);

    // 添加时间区
    int User_AddTime(int CardNum, User_DateTime pDateTime, int iProgramIndex);

    // 添加计时区
    int User_AddTimeCount(int CardNum, User_Timer pTimeCount,
                          int iProgramIndex);

    // 添加温度区
    int User_AddTemperature(int CardNum, User_Temperature pTemperature,
                            int iProgramIndex);

    // 发送数据
    boolean User_SendToScreen(int CardNum);

    // 2、实时更新函数组=================================================
    // 实时发送数据建立连接
    boolean User_RealtimeConnect(int CardNum);

    // 实时发送图片句柄
    boolean User_RealtimeSendData(int CardNum, int x, int y, int iWidth,
                                  int iHeight, WinNT.HANDLE hBitmap);

    // 实时发送图片文件
    boolean User_RealtimeSendBmpData(int CardNum, int x, int y, int iWidth,
                                     int iHeight, String strFileName);

    // 实时发送文本
    boolean User_RealtimeSendText(int CardNum, int x, int y, int iWidth,
                                  int iHeight, String strText, User_FontSet pFontInfo);

    // 实时发送断开连接
    boolean User_RealtimeDisConnect(int CardNum);

    // 实时发送清屏
    boolean User_RealtimeScreenClear(int CardNum);

    // 3、显示屏控制函数组==============================================
    // 开屏
    boolean User_OpenScreen(int CardNum);

    // 关屏
    boolean User_CloseScreen(int CardNum);

    // 校正时间
    boolean User_AdjustTime(int CardNum);

    // 亮度调节
    boolean User_SetScreenLight(int CardNum, int iLightDegreen);

    // 重新加载配置文件
    void User_ReloadIniFile(String strEQ2008_Dll_Set_Path);

    /**
     * 节目区域参数
     */
    public class User_PartInfo extends Structure {

        public int iX; // 窗口的起点X
        public int iY; // 窗口的起点Y
        public int iWidth; // 窗体的宽度
        public int iHeight; // 窗体的高度
        public int iFrameMode; // 边框的样式
        public int FrameColor; // 边框颜色
    }

    /**
     * 字体参数
     */
    public class User_FontSet extends Structure {

        public String strFontName; // 字体的名称
        public int iFontSize; // 字体的大小
        public boolean bFontBold; // 字体是否加粗
        public boolean bFontItaic; // 字体是否是斜体
        public boolean bFontUnderline; // 字体是否带下划线
        public int colorFont; // 字体的颜色
        public int iAlignStyle; // 对齐方式
        // 0－ 左对齐
        // 1－居中
        // 2－右对齐
        public int iVAlignerStyle; // 上下对齐方式
        // 0-顶对齐
        // 1-上下居中
        // 2-底对齐
        public int iRowSpace; // 行间距
    }

    /**
     * 计时窗口
     */
    public class User_Timer extends Structure {

        public User_PartInfo PartInfo = new User_PartInfo(); // 分区信息
        public int BkColor; // 背景颜色
        public User_FontSet FontInfo = new User_FontSet(); // 字体设置
        public int ReachTimeYear; // 到达年
        public int ReachTimeMonth; // 到达月
        public int ReachTimeDay; // 到达日
        public int ReachTimeHour; // 到达时
        public int ReachTimeMinute; // 到达分
        public int ReachTimeSecond; // 到达秒
        public int bDay; // 是否显示天 0－不显示 1－显示
        public int bHour; // 是否显示小时
        public int bMin; // 是否显示分钟
        public int bSec; // 是否显示秒
        public int bMulOrSingleLine; // 单行还是多行
        public String chTitle; // 添加显示文字
    }

    /**
     * 温度窗口
     */
    public class User_Temperature extends Structure {

        public User_PartInfo PartInfo = new User_PartInfo(); // 分区信息
        public int BkColor; // 背景颜色
        public User_FontSet FontInfo = new User_FontSet(); // 字体设置
        public String chTitle; // 标题
        public int DisplayType; // 显示格式：0－度 1－C
    }

    /**
     * 日期时间窗口
     */
    public class User_DateTime extends Structure {

        public User_PartInfo PartInfo = new User_PartInfo(); // 分区信息
        public int BkColor; // 背景颜色
        public User_FontSet FontInfo = new User_FontSet(); // 字体设置
        public int iDisplayType; // 显示风格
        public String chTitle; // 添加显示文字
        public int bYearDisType; // 年份位数0 －4；1－2位
        public int bMulOrSingleLine; // 单行还是多行
        public int bYear; // 是否显示年
        public int bMouth; // 是否显示月
        public int bDay; // 是否显示日
        public int bWeek; // 是否显示星期
        public int bHour; // 是否显示小时
        public int bMin; // 是否显示分钟
        public int bSec; // 是否显示秒钟
    }

    /**
     * 图文框
     */
    public class User_Bmp extends Structure {
        public User_PartInfo PartInfo = new User_PartInfo(); // 分区信息
    }

    /**
     * 单行文本框
     */
    public class User_SingleText extends Structure {

        public String chContent; // 显示内容
        public User_PartInfo PartInfo = new User_PartInfo(); // 分区信息
        public int BkColor; // 背景颜色
        public User_FontSet FontInfo = new User_FontSet(); // 字体设置
        public User_MoveSet MoveSet = new User_MoveSet(); // 动作方式设置

    }

    /**
     * 文本框
     */
    public class User_Text extends Structure {

        public String chContent; // 显示内容
        public User_PartInfo PartInfo = new User_PartInfo(); // 分区信息
        public int BkColor; // 背景颜色
        public User_FontSet FontInfo = new User_FontSet(); // 字体设置
        public User_MoveSet MoveSet = new User_MoveSet(); // 动作方式设置

    }

    /**
     * RTF文件
     */
    public class User_RTF extends Structure {

        public String strFileName; // RTF文件名
        public User_PartInfo PartInfo = new User_PartInfo(); // 分区信息
        public User_MoveSet MoveSet = new User_MoveSet(); // 动作方式设置

    }

    /**
     * 动画方式设置
     */
    public class User_MoveSet extends Structure {

        public int iActionType; // 节目变换方式
        public int iActionSpeed; // 节目的播放速度
        public boolean bClear; // 是否需要清除背景
        public int iHoldTime; // 在屏幕上停留的时间
        public int iClearSpeed; // 清除显示屏的速度
        public int iClearActionType; // 节目清除的变换方式
        public int iFrameTime; // 每帧时间

    }

    boolean User_RealtimeSendData(int m_iCardNum, int x, int y, int iWidth,
                                  int iHeight, Pointer pointer);
}
