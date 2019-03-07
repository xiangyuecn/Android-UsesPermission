package ecomm.lib_comm.permission.test;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;

import ecomm.lib_comm.permission.Permission;
import ecomm.lib_comm.permission.UsesPermission;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("权限测试");

        final TextView text= findViewById(R.id.textView);
        text.setText("点上面按钮测试");

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                run(1);
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                run(2);
            }
        });
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                run(3);
            }
        });
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                run(4);
            }
        });
        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                run(5);
            }
        });
    }

















    public void run(final int idx){
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
            permissions.set(1,Manifest.permission.READ_SMS);
            permissions.set(1,"abcdef");
        }else if(idx==5){
            //这两个特殊的 不包括在可以静默处理的权限内 不参与省的弹框
            permissions.remove(Permission.REQUEST_INSTALL_PACKAGES);
            permissions.remove(Permission.SYSTEM_ALERT_WINDOW);
        }


        text.setText("");
        text.append(System.currentTimeMillis()+" 按钮"+idx);
        text.append("\n\n申请的权限："+join(permissions));





        String[] arr=permissions.toArray(new String[0]);
        new UsesPermission(MainActivity.this, arr){
            @Override
            protected String onCancelTips(int viewCancelCount, @NonNull ArrayList<String> rejectFinalPermissions, @NonNull ArrayList<String> rejectPermissions) {
                if(idx==2){
                    if(viewCancelCount<=3){
                        return "永远弹，剩余"+(3-viewCancelCount+1)+"次取消。{Auto}";
                    }
                    return null;
                }

                return super.onCancelTips(viewCancelCount, rejectFinalPermissions, rejectPermissions);
            }

            @Override
            protected String onTips(int viewTipsCount, @NonNull ArrayList<String> rejectFinalPermissions, @NonNull ArrayList<String> rejectPermissions) {
                String tips=super.onTips(viewTipsCount, rejectFinalPermissions, rejectPermissions);

                if(idx==2){
                    return "永远弹"+(viewTipsCount==0?"（这是第一次引导，没取消按钮）":"")+"，权限：[{Names}]";
                }
                if(idx==3){
                    if(viewTipsCount==1){
                        return "被拒绝了，弹一次：[{Names}]";
                    }
                }
                if(idx==5){
                    //静默不弹任何提示，需要引导才能进行权限设置的除外(如安装)
                    return null;
                }

                return tips;
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
