package ecomm.lib_comm.permission.view;

import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 *  github: https://github.com/xiangyuecn/Android-UsesPermission
 */
public class Request extends Fragment {
    public interface RequestPermissionsCallBack{
       void onResult(String[] permissions, int[] grantResults);
    }

    /**
     * 开始权限授权请求
     */
    static public void RequestPermissions(Activity activity, ArrayList<String> permissions, RequestPermissionsCallBack callback){
        Request fragment = new Request();
        fragment.ThisActivity=activity;
        fragment.Permissions=permissions;
        fragment.Callback=callback;

        //参考https://github.com/getActivity/XXPermissions XXPermissions类实现
        activity.getFragmentManager().beginTransaction().add(fragment, activity.getClass().getName()).commit();
    }


    private Activity ThisActivity;
    private ArrayList<String> Permissions;
    private RequestPermissionsCallBack Callback;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(Permissions.toArray(new String[0]), 101);
        }else{
            throw new RuntimeException("授权请求不应该在低于23的API下调用");//不可能出现的情况
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==101) {
            ThisActivity.getFragmentManager().beginTransaction().remove(this).commit();
            Callback.onResult(permissions, grantResults);

            ThisActivity=null;
            Callback=null;
        }
    }
}
