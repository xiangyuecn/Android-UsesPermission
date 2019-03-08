package ecomm.lib_comm.permission.com_hjq_permissions;

import android.os.Build;
import android.support.annotation.Nullable;

import java.util.HashMap;

import ecomm.lib_comm.permission.handle.AlertWindowHandle;
import ecomm.lib_comm.permission.handle.Handle;
import ecomm.lib_comm.permission.handle.InstallPackagesHandle;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : 权限请求实体类
 *
 *    use github: https://github.com/xiangyuecn/Android-UsesPermission
 */
public class Permission {
    static public class Item{
        public final String Name;
        public final String Permission;
        public final int API;

        @Nullable
        public final Handle Handle;

        public Item(String permission, String name, int api, @Nullable Handle handle){
            Permission=permission;
            Name=name;
            API=api==0?Build.VERSION_CODES.M:api;
            Handle=handle;
        }
    }
    static public HashMap<String, Item> Names=new HashMap<>();
    static public String Reg(String permission, String name, int api, Handle handle){
        Item item=new Item(permission, name, api, handle);

        Names.put(permission, item);
        return permission;
    }
    static public String Reg(String permission, String name, int api){
        return Reg(permission, name, api, null);
    }
    static public String Reg(String permission, String name){
        return Reg(permission, name, 0);
    }

    /**
     * 查询权限配置信息
     */
    @Nullable
    static public Item QueryItem(String permission){
        return Names.get(permission);
    }
    /**
     * 查询权限字符串对应的友好中文名称
     */
    static public String QueryName(String permission){
        Item item=QueryItem(permission);
        String val;
        if(item==null){
            val=permission.replace("android.permission.","");
        }else{
            val=item.Name;
        }
        return val;
    }



    public static final String REQUEST_INSTALL_PACKAGES = Reg("android.permission.REQUEST_INSTALL_PACKAGES", "安装应用", Build.VERSION_CODES.O, new InstallPackagesHandle()); // 8.0及以上应用安装权限

    public static final String SYSTEM_ALERT_WINDOW = Reg("android.permission.SYSTEM_ALERT_WINDOW", "悬浮窗",0, new AlertWindowHandle()); // 6.0及以上悬浮窗权限

    public static final String READ_CALENDAR =  Reg("android.permission.READ_CALENDAR", "日历"); // 读取日程提醒
    public static final String WRITE_CALENDAR = Reg("android.permission.WRITE_CALENDAR", "日历"); // 写入日程提醒

    public static final String CAMERA = Reg("android.permission.CAMERA", "相机"); // 拍照权限

    public static final String READ_CONTACTS = Reg("android.permission.READ_CONTACTS","读取联系人"); // 读取联系人
    public static final String WRITE_CONTACTS = Reg("android.permission.WRITE_CONTACTS", "修改联系人"); // 写入联系人
    public static final String GET_ACCOUNTS = Reg("android.permission.GET_ACCOUNTS", "访问手机账户"); // 访问账户列表

    public static final String ACCESS_FINE_LOCATION = Reg("android.permission.ACCESS_FINE_LOCATION", "定位"); // 获取精确位置
    public static final String ACCESS_COARSE_LOCATION = Reg("android.permission.ACCESS_COARSE_LOCATION", "定位"); // 获取粗略位置

    public static final String RECORD_AUDIO = Reg("android.permission.RECORD_AUDIO", "录音"); // 录音权限

    public static final String READ_PHONE_STATE = Reg("android.permission.READ_PHONE_STATE", "获取手机信息"); // 读取电话状态
    public static final String CALL_PHONE = Reg("android.permission.CALL_PHONE", "拨打电话"); // 拨打电话
    public static final String READ_CALL_LOG = Reg("android.permission.READ_CALL_LOG","读取通话记录"); // 读取通话记录
    public static final String WRITE_CALL_LOG = Reg("android.permission.WRITE_CALL_LOG", "修改通话记录"); // 写入通话记录
    public static final String ADD_VOICEMAIL = Reg("com.android.voicemail.permission.ADD_VOICEMAIL", "添加语音邮件"); // 添加语音邮件
    public static final String USE_SIP = Reg("android.permission.USE_SIP", "使用SIP视频"); // 使用SIP视频
    public static final String PROCESS_OUTGOING_CALLS = Reg("android.permission.PROCESS_OUTGOING_CALLS", "处理拨出电话"); // 处理拨出电话
    public static final String ANSWER_PHONE_CALLS = Reg("android.permission.ANSWER_PHONE_CALLS", "处理呼入电话", Build.VERSION_CODES.O);// 8.0危险权限：允许您的应用通过编程方式接听呼入电话。要在您的应用中处理呼入电话，您可以使用 acceptRingingCall() 函数
    public static final String READ_PHONE_NUMBERS = Reg("android.permission.READ_PHONE_NUMBERS", "读取手机号码", Build.VERSION_CODES.O);// 8.0危险权限：权限允许您的应用读取设备中存储的电话号码

    public static final String BODY_SENSORS = Reg("android.permission.BODY_SENSORS", "传感器"); // 传感器

    public static final String SEND_SMS = Reg("android.permission.SEND_SMS", "发送短信"); // 发送短信
    public static final String RECEIVE_SMS = Reg("android.permission.RECEIVE_SMS", "接收短信"); // 接收短信
    public static final String READ_SMS = Reg("android.permission.READ_SMS", "读取短信"); // 读取短信
    public static final String RECEIVE_WAP_PUSH = Reg("android.permission.RECEIVE_WAP_PUSH", "接收WAP PUSH信息"); // 接收WAP PUSH信息
    public static final String RECEIVE_MMS = Reg("android.permission.RECEIVE_MMS", "接收彩信"); // 接收彩信

    public static final String READ_EXTERNAL_STORAGE = Reg("android.permission.READ_EXTERNAL_STORAGE", "读写手机存储"); // 读取外部存储
    public static final String WRITE_EXTERNAL_STORAGE = Reg("android.permission.WRITE_EXTERNAL_STORAGE", "读写手机存储"); // 写入外部存储

    public static final class Group {

        // 日历
        public static final String[] CALENDAR = new String[]{
                Permission.READ_CALENDAR,
                Permission.WRITE_CALENDAR};

        // 联系人
        public static final String[] CONTACTS = new String[]{
                Permission.READ_CONTACTS,
                Permission.WRITE_CONTACTS,
                Permission.GET_ACCOUNTS};

        // 位置
        public static final String[] LOCATION = new String[]{
                Permission.ACCESS_FINE_LOCATION,
                Permission.ACCESS_COARSE_LOCATION};

        // 存储
        public static final String[] STORAGE = new String[]{
                Permission.READ_EXTERNAL_STORAGE,
                Permission.WRITE_EXTERNAL_STORAGE};
    }
}