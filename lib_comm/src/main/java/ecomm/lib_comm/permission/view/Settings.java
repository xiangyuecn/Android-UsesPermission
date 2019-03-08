package ecomm.lib_comm.permission.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ecomm.lib_comm.permission.com_hjq_permissions.PermissionSettingPage;

/**
 *  github: https://github.com/xiangyuecn/Android-UsesPermission
 */
public class Settings extends Fragment {
    public interface OpenSettingsCallback{
        void onResult();
    }

    static public void OpenSettings(@NonNull Activity activity, Intent jumpTo, @NonNull OpenSettingsCallback callback){
        final Settings fragment = new Settings();
        fragment.ThisActivity=activity;
        fragment.JumpTo=jumpTo;
        fragment.Callback=callback;

        activity.getFragmentManager().beginTransaction().add(fragment, activity.getClass().getName()).commit();
    }

    private Activity ThisActivity;
    @Nullable
    private Intent JumpTo;
    private OpenSettingsCallback Callback;


    private void end(){
        if(ThisActivity==null){//可能是重启了
            return;
        }
        pageIsView=0;
        ThisActivity.getFragmentManager().beginTransaction().remove(this).commit();

        Callback.onResult();

        ThisActivity=null;
        Callback=null;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //可能是重启了，关闭权限会导致此问题 https://www.jianshu.com/p/cb68ca511776
        if(ThisActivity==null){
            return;
        }

        Intent intent=JumpTo;
        if(intent==null){
            intent=PermissionSettingPage.build(ThisActivity);
        }
        try {
            startActivityForResult(intent, 102);
        } catch (Exception ignored) {
            //不管什么情况，什么设置，出问题了，统统跳到系统设置
            intent = PermissionSettingPage.google(ThisActivity);
            startActivityForResult(intent, 102);
        }
        pageIsView=1;
    }


    int pageIsView=0;

    @Override
    public void onPause() {
        super.onPause();

        pageIsView=2;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(pageIsView>=2){
            end();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=102){
            return;
        }
        pageIsView=3;

        //参考com.hjq.permissions.PermissionFragment ： 需要延迟执行，不然有些华为机型授权了但是获取不到权限
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Settings.this.end();
            }
        }, 500);
    }
}
