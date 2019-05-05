package ecomm.lib_comm.permission.test;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;

import ecomm.lib_comm.permission.Permission;
import ecomm.lib_comm.permission.UsesPermission;

/**
 *  github: https://github.com/xiangyuecn/Android-UsesPermission
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("权限测试");

        final TextView text= findViewById(R.id.textView);
        text.setText("点那几个按钮进行测试");

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                run(1, "默认方式未：直接弹出授权请求，用户如果点了拒绝（非永久），后续还会弹一次提示，防止误点。如果有被永久拒绝的权限，会弹提示，转到系统设置。");
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                run(2, "不管什么时候都会弹");
            }
        });
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                run(3, "相对月默认方式，如果用户点了拒绝（非永久），本方式不会再次弹提示。");
            }
        });
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                run(4, "包含了两个没有在Manifest中声明的权限，其他和默认相同。");
            }
        });
        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                run(5, "不弹任何提示，直接发起授权请求，如果是被永久禁止的权限，将不会进行处理（不含带自定义授权请求的权限）");
            }
        });
        findViewById(R.id.button12).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                run(12, "模拟不确定Activity存不存在时进行权限处理，比如在后台服务中调用，实际结果为仅仅检测权限是否已授权而已。但可能有Activity时，会尝试调起授权请求用户界面。");
            }
        });

        findViewById(R.id.button11).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setText("shouldShowRequestPermissionRationale权限提示模式测试：先重置(装)app，然后点击这个按钮查看结果，然后点默认按钮对相机权限进行控制，然后再点这个按钮，对比检测到的结果。我们需要对比初次安装时的权限状态，非永久性的拒绝一次相机状态，和永久禁用相机权限时的状态；留意是否出现不在询问选项。另外可手动去设置定位权限，对比一下结果");

                String item=Permission.CAMERA;
                text.append("\n\n【"+Permission.QueryName(item)+"】:");
                text.append("\n是否授权："+(ContextCompat.checkSelfPermission(MainActivity.this, item) == PackageManager.PERMISSION_GRANTED));
                text.append("\n需要提示："+ ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, item));

                item=Permission.ACCESS_FINE_LOCATION;
                text.append("\n\n【"+Permission.QueryName(item)+"】:");
                text.append("\n是否授权："+(ContextCompat.checkSelfPermission(MainActivity.this, item) == PackageManager.PERMISSION_GRANTED));
                text.append("\n需要提示："+ ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, item));
            }
        });
    }

















    public void run(final int idx, String desc){
        final TextView text= findViewById(R.id.textView);

        ArrayList<String> permissions=new ArrayList<>();
        Collections.addAll(permissions
                , Manifest.permission.INTERNET
                , Manifest.permission.VIBRATE
                , Permission.CAMERA
                , Permission.RECORD_AUDIO
                , Permission.REQUEST_INSTALL_PACKAGES
                , Permission.SYSTEM_ALERT_WINDOW
        );

        if(idx==4){
            permissions.set(0,Manifest.permission.READ_SMS);
            permissions.set(1,"abcdef");
        }else if(idx==5){
            //这两个特殊的 不包括在可以静默处理的权限内 不参与省的弹框
            permissions.remove(Permission.REQUEST_INSTALL_PACKAGES);
            permissions.remove(Permission.SYSTEM_ALERT_WINDOW);
        }


        text.setText("");
        text.append(System.currentTimeMillis()+" 按钮"+idx);
        text.append("\n【说明】"+desc);
        text.append("\n\n【申请权限】："+join(permissions));





        String[] arr=permissions.toArray(new String[0]);
        Activity activity=MainActivity.this;
        Context context=MainActivity.this;
        if(idx==12){
            //模拟后台服务，此时没有Activity，仅仅检测权限，不会调起授权请求
            context=activity.getApplicationContext();
            activity=null;
        }
        new UsesPermission(activity, context, arr){
            @Override
            protected String onCancelTips(int viewCancelCount, @NonNull ArrayList<String> permissions, boolean isFinal) {
                if(idx==2){
                    if(viewCancelCount<=3){
                        return "永远弹，剩余"+(3-viewCancelCount+1)+"次取消。{Auto}";
                    }
                    return null;
                }

                return super.onCancelTips(viewCancelCount, permissions, isFinal);
            }


            @Override
            protected String onTips(int viewTipsCount, @NonNull ArrayList<String> permissions, boolean isFinal) {
                if(idx==2){
                    return "永远弹"+(viewTipsCount==0?"（这是引导）":"")+"，权限：[{Names}]";
                }
                if(idx==3){
                    if(viewTipsCount==1 && isFinal){
                        return "";
                    }else{
                        return null;
                    }
                }
                if(idx==5){
                    //静默不弹任何提示，需要引导才能进行权限设置的除外(如安装)
                    return null;
                }

                return super.onTips(viewTipsCount, permissions, isFinal);
            }









            @Override
            protected void onTrue(@NonNull ArrayList<String> lowerPermissions) {
                text.append("\n\n【onTrue】"
                        +"\n 低版本权限: "+join(lowerPermissions)
                );
            }

            @Override
            protected void onFalse(@NonNull ArrayList<String> rejectFinalPermissions,@NonNull ArrayList<String> rejectPermissions, @NonNull ArrayList<String> invalidPermissions) {
                text.append("\n\n【onFalse】"
                        +"\n 拒绝: "+join(rejectPermissions)
                        +"\n 永久拒绝: "+join(rejectFinalPermissions)
                        +"\n 无效: "+join(invalidPermissions)
                );
            }

            @Override
            protected void onComplete(@NonNull ArrayList<String> resolvePermissions, @NonNull ArrayList<String> lowerPermissions, @NonNull ArrayList<String> rejectFinalPermissions, @NonNull ArrayList<String> rejectPermissions, @NonNull ArrayList<String> invalidPermissions) {
                text.append("\n\n【onComplete】"
                        +"\n已授权: "+join(resolvePermissions)
                        +"\n低版本权限: "+join(lowerPermissions)
                        +"\n拒绝: "+join(rejectPermissions)
                        +"\n永久拒绝: "+join(rejectFinalPermissions)
                        +"\n无效: "+join(invalidPermissions)
                );
                text.append("\n\n\n"+System.currentTimeMillis());
            }
        };
    }















    public String join(ArrayList<String> ss){
        StringBuilder str=new StringBuilder();
        for(String s:ss){
            str.append(Permission.QueryName(s)).append(",");
        }
        if(str.length()>0) {
            str.setLength(str.length() - 1);
        }else{
            str.append("-");
        }
        return str.toString();
    }
}
