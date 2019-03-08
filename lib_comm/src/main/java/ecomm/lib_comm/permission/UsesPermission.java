package ecomm.lib_comm.permission;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ecomm.lib_comm.permission.handle.Handle;
import ecomm.lib_comm.permission.view.Request;
import ecomm.lib_comm.permission.view.Settings;

/**
 * 用户授权处理类，无需实现此抽象类，哪里要用哪里就直接new，简单明了
 * github: https://github.com/xiangyuecn/Android-UsesPermission
 */
public abstract class UsesPermission {
    /**
     * 传入的权限列表
     */
    protected ArrayList<String> Permissions;
    /**
     * 为没有重写{@link #onTips}时提示的默认提示
     */
    protected String DefaultTips;

    protected Activity ThisActivity;

    public Activity GetActivity(){
        return ThisActivity;
    }




    /**
     * 授权请求处理类，会自动调用授权请求，完成授权后自动回调相应on方法，如果被拒绝了权限，会根据{@link #onTips}结果来转到系统设置
     * @param defaultTips 为没有重写{@link #onTips}时的默认值
     */
    public UsesPermission(@NonNull Activity activity, @NonNull String[] permissions, String defaultTips){
        ThisActivity=activity;

        Permissions=new ArrayList<>();
        Collections.addAll(Permissions, permissions);

        DefaultTips =defaultTips;

        run();
    }

    /**
     * 授权请求处理类，会自动调用授权请求，完成授权后自动回调相应on方法。没有重写{@link #onTips}的情况下，如果永久拒绝了授权会自动提示到系统设置
     */
    public UsesPermission(@NonNull Activity activity,  @NonNull String...permissions){
        this(activity, permissions, "");
    }


    /**
     * 授权完成时回调，会在True和False之一回调后调用。
     * @param resolvePermissions 已授权的权限列表
     * @param lowerPermissions 参考{@link #onTrue}，为resolvePermissions的子集
     */
    protected void onComplete(@NonNull ArrayList<String> resolvePermissions, @NonNull ArrayList<String> lowerPermissions, @NonNull ArrayList<String> rejectFinalPermissions, @NonNull ArrayList<String> rejectPermissions, @NonNull ArrayList<String> invalidPermissions){
        //可选自行实现
    }

    /**
     * 已授权时回调，不管什么情况，True和False肯定有一个会回调。注意：跟API版本有关的方法调用，应自行判断API版本，低版本API中调用高版本权限全部会放行。
     * @param lowerPermissions 如果是在低版本API上处理不支持的高版本新权限时，会忽略此项权限的检测的检测，默许放行，此时本参数将带上此权限。
     */
    protected void onTrue(@NonNull ArrayList<String> lowerPermissions){
        //可选自行实现
    }

    /**
     * 未授权时回调，不管什么情况，True和False肯定有一个会回调
     * @param rejectFinalPermissions 永久拒绝的权限列表，为rejectPermissions的子集，空数组代表没有此项
     * @param rejectPermissions 被拒绝的权限列表，空数组代表没有此项
     * @param invalidPermissions 未在manifest里声明的权限列表，不会出现在rejectFinalPermissions中，空数组代表没有此项。
     */
    protected void onFalse(@NonNull ArrayList<String> rejectFinalPermissions, @NonNull ArrayList<String> rejectPermissions, @NonNull ArrayList<String> invalidPermissions){
        //可选自行实现
    }

    /**
     * 授权前会回调此方法，用来生成设置提示信息，也是来决定是否提示和进行授权。如果返回null，代表不进行下下一步操作；返回字符串会进行提示然后进行请求授权。注意：这个方法，会有不同权限进行多次调用；如果用户选点击了取消默认这些权限不会再调起请求（可重写{@link #onCancelTips}修改此行为）。
     *
     * 默认行为为：
     *      1. 先直接发起权限申请 viewTipsCount=0
     *          A. 普通权限（非永久拒绝）如果被拒绝（非永久拒绝），后续会安排再弹框申请一次(防误选)。
     *          B. 如果结果有被永久拒绝，这部分权限会和后面的永久权限申请一起弹框处理。
     *      2. 弹一次框处理被永久拒绝的权限(弹一次够了) viewTipsCount=1+
     *
     * @param viewTipsCount 0-n 是第几次准备弹提示框。
     *          0：申请前的引导提示，返回null代表不弹提示，直接调起授权。
     *              注意：第0次包含所有权限(不含带自定义授权请求的权限)，无法区分是不是永久拒绝的权限。
     *          1+:被永久拒绝的权限申请，或普通权限上一轮被拒。
     *              注意：为1的时候，如果权限带自定义的授权请求方式，就算返回了null，也会进行提示并调用授权请求。
     *
     * @param permissions 被拒绝的权限列表
     * @param isFinal 这个权限列表是不是永久被拒绝的权限，true是，false为未永久拒绝
     *
     * @return 返回提示信息；返回值为null不进行申请；为空字符串时自动生成合理的提示。
     *      字符串内容支持特定占位符：
     *          {Auto}:用自动生成提示内容替换
     *          {Names}:自动替换被拒绝的权限名称
     *                 如：'xx"{Names}"xx' => 'xx"权限名1,权限名2,权限名3"xx'
     */
    protected String onTips(int viewTipsCount, @NonNull ArrayList<String> permissions, boolean isFinal){
        //可选自行实现

        if(viewTipsCount==1) {
            return DefaultTips;
        }
        return null;
    }

    /**
     * 弹出了提示时，用户点击了取消时的额外提示信息。返回null彻底不再调起提示，默认就是返回null。这个回调的用法和{@link #onTips}一模一样，只是这个仅仅作用在点击了取消时。注意：重写这个方法应该慎重，最多viewCancelCount几次后就返回null，避免出现无法取消永远弹框的问题。
     *
     * @param viewCancelCount 1-n 当前这次请求是第几次取消
     */
    protected String onCancelTips(int viewCancelCount, @NonNull ArrayList<String> permissions, boolean isFinal){
        //可选自行实现

        return null;
    }

    /**
     * 授权提示弹框，重写此方法自定义弹框行为，默认使用系统AlertDialog弹框。只要求必须回调okCall，cancelCall中的任何一个，怎么显示界面随意。
     * @param tips 默认的弹框提示信息
     * @param isCancel 是否是点击的取消按钮，然后重复进行弹框的
     * @param viewTipsCount 0-n 是第几次准备弹提示框，参考{@link #onTips}
     * @param permissions 此次弹框要处理的拒绝权限列表
     * @param isFinal 这个权限列表是不是永久被拒绝的权限，true是，false为未永久拒绝
     * @param okCall 确定时回调
     * @param cancelCall 取消时回调。注：如果是viewTipsCount==0(引导提示)，这个回调==okCall
     */
    protected void onTipsDialogView(String tips, boolean isCancel, int viewTipsCount, ArrayList<String> permissions, boolean isFinal, final Runnable okCall, final Runnable cancelCall){
        AlertDialog.Builder build=new AlertDialog.Builder(ThisActivity);

        build.setTitle("权限设置提醒")
            .setMessage(tips)
            .setPositiveButton(isFinal?"设置":"好的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    okCall.run();
                }
            })
            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancelCall.run();
                }
            });
        //初次引导，这种不要显示取消按钮
        if(viewTipsCount!=0){
            build.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelCall.run();
                }
            });
        }

        build.show();
    }
























    /**
     * 生成打开系统设置的提示信息，使用{@link #onTips}返回值，如果为""会自动取rejectPermissions权限来生成：'需要"权限名1,权限名2,权限名3"权限才能进行操作，是否前往设置？'，注意：如果返回null，viewTipsCount=0时不弹提示框，!=0时代表不打开系统设置。
     *
     * @param notEmpty 如果是权限已实现的单独申请，这个设为true。非isCancel时会强制弹出提示
     */
    @Nullable
    public String BuildTips(boolean isCancel, boolean notEmpty, int countX, @NonNull ArrayList<String> permissions, boolean isFinal){
        String names=Permission.QueryNames(permissions);
        String auto="需要\""+names+"\"权限才能进行操作，是否前往设置？";

        String tips;
        if(isCancel) {
            tips = onCancelTips(countX, permissions, isFinal);
        }else {
            tips = onTips(countX, permissions, isFinal);
        }

        if(tips!=null && !tips.equals("")){
            //格式化
            return tips.replace("{Auto}",auto).replace("{Names}", names);
        }

        if(tips==null && !isCancel && notEmpty || tips!=null && tips.equals("")){
            tips = auto;
        }
        return tips;
    }


























    static private List<String> ManifestPermissions;
    private ArrayList<String> _validPermissions;
    private ArrayList<String> _invalidPermissions;
    private ArrayList<String> _lowerPermissions;
    /**
     * 进行授权请求处理，完成授权后自动回调相应on方法
     */
    private void run() {
        if (ManifestPermissions == null) {
            try {
                ManifestPermissions = Arrays.asList(ThisActivity.getPackageManager().getPackageInfo(ThisActivity.getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions);
            } catch (Exception e) {
                //NOOP
            }
        }

        //先把权限分一下，处理掉无效的
        _validPermissions = new ArrayList<>();
        _invalidPermissions = new ArrayList<>();
        _lowerPermissions = new ArrayList<>();
        for (String item : Permissions) {
            //Manifest未声明
            if (!ManifestPermissions.contains(item)) {
                _invalidPermissions.add(item);
                continue;
            }

            Permission.Item pItem = Permission.QueryItem(item);
            if (pItem != null) {
                //当前API低于权限定义的的版本，【无法申请的权限即为有权限】
                if (Build.VERSION.SDK_INT < pItem.API) {
                    _lowerPermissions.add(item);
                    continue;
                }
            }

            _validPermissions.add(item);
        }

        //******6.0以下版本直接回调结果******
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            __callback(new ArrayList<String>(), new ArrayList<String>());
            return;
        }


        _looperPermissions=new ArrayList<>(_validPermissions);
        _normalIsCall=false;

        __RunLooper(0);
    }


    private ArrayList<String> _looperPermissions;
    private boolean _normalIsCall;


    private void _queryAll(ArrayList<String> checks, HashMap<String, Handle.CheckResult> handleRequests, ArrayList<String> finalList, ArrayList<String> noGrant){
        for (String item : checks) {
            Permission.Item pItem = Permission.QueryItem(item);
            //此权限实现了检查
            if (pItem != null && pItem.Handle!=null) {
                Handle.CheckResult res=pItem.Handle.Check(this, item);
                if(!res.IsResolve){
                    //无权限
                    handleRequests.put(item, res);

                    noGrant.add(item);
                    if (res.IsFinalReject) {
                        finalList.add(item);
                    }
                }
                //实现了Handle的不用默认检查
                continue;
            }

            //默认的权限检查
            if (ContextCompat.checkSelfPermission(ThisActivity, item) != PackageManager.PERMISSION_GRANTED) {
                //傻傻分不清首次还是被永久禁止，全部先调用一次请求就知道谁裸泳了
                if(_normalIsCall && !ActivityCompat.shouldShowRequestPermissionRationale(ThisActivity, item)){
                    finalList.add(item);
                }

                noGrant.add(item);
            }
        }
    }




    //循环处理，反复调用，直到没有要处理的权限为止
    private void __RunLooper(final int baseCount){
        final HashMap<String, Handle.CheckResult> handleRequests=new HashMap<>();
        ArrayList<String> noGrant=new ArrayList<>();
        ArrayList<String> finalList=new ArrayList<>();

        //如果重复进入循环，代表经过多次请求了，立即查询最新的没毛病
        _queryAll(_looperPermissions, handleRequests, finalList, noGrant);

        //******没有需要授权的权限了******
        if(noGrant.size()==0){
            //重新检测所有权限状态
            if(_looperPermissions.size()!=_validPermissions.size()) {
                noGrant=new ArrayList<>();
                finalList=new ArrayList<>();
                _queryAll(_validPermissions, new HashMap<String, Handle.CheckResult>(), finalList, noGrant);
            }

            //******返回未授权结果******
            __callback(finalList, noGrant);
            return;
        }


        //未授权权限分组
        final ArrayList<String> handleGrant=new ArrayList<>();
        final ArrayList<String> finalGrant=new ArrayList<>();
        final ArrayList<String> normalGrant=new ArrayList<>();
        for(String item:noGrant){
            if(handleRequests.containsKey(item)){
                handleGrant.add(item);
            }else if(finalList.contains(item)){
                finalGrant.add(item);
            }else{
                normalGrant.add(item);
            }
        }

        //按照合理的顺序执行请求（Handle的顺序任意，Normal必须在Final前面）
        __execNormalGrant(baseCount, normalGrant, finalGrant, new Runnable() {
            @Override
            public void run() {
                __execFinalGrant(baseCount, finalGrant, new Runnable() {
                    @Override
                    public void run() {
                        __execHandleGrant(baseCount, handleRequests, handleGrant, new Runnable() {
                            @Override
                            public void run() {
                                __RunLooper(baseCount+1);
                            }
                        });
                    }
                });
            }
        });
    }


    private boolean prevTipsIsShow;
    private boolean currentTipsNotShow;
    /**
     * 对所有类型的请求方式调用前包裹一层提示框
     */
    private void __execTipsWrap(final boolean isCancel, final boolean notEmpty, final int viewTipsCount, final ArrayList<String> permissions, final boolean isFinal, final Runnable runTrue, final Runnable runFalse){
        boolean notShow=currentTipsNotShow;
        prevTipsIsShow=false;
        currentTipsNotShow=false;

        final Runnable clear=new Runnable() {
            @Override
            public void run() {
                //明确不要请求了，唯一的一处合法清除这些权限
                _looperPermissions.removeAll(permissions);

                runFalse.run();
            }
        };

        String tips=BuildTips(isCancel, notEmpty, viewTipsCount, permissions, isFinal);

        if(isCancel){
            if(tips==null){
                //彻底不要弹了
                clear.run();
                return;
            }
        }else if(tips==null){
            //首次无需弹出提示
            if(viewTipsCount==0){
                notShow=true;
            }else {
                //不要弹了
                clear.run();
                return;
            }
        }

        //无需弹窗
        if(notShow){
            runTrue.run();
            return;
        }

        //弹提示
        prevTipsIsShow=true;

        onTipsDialogView(tips, isCancel, viewTipsCount, permissions, isFinal, new Runnable() {
            @Override
            public void run() {
                runTrue.run();
            }
        }, new Runnable() {
            @Override
            public void run() {
                //引导取消行为为确定行为
                if(viewTipsCount==0){
                    runTrue.run();
                    return;
                }

                //重新运行即可，指定为取消
                int cancelCount=isCancel?viewTipsCount+1:1;
                __execTipsWrap(true, notEmpty, cancelCount, permissions, isFinal, runTrue, runFalse);
            }
        });
    }


    /**
     * 处理自行实现了请求处理的权限，是一条权限一条权限来进行处理的
     */
    private void __execHandleGrant(final int baseCount, final HashMap<String, Handle.CheckResult> handleRequests, final ArrayList<String> permissions, final Runnable run){
        if(permissions.size()==0){
            run.run();
            return;
        }
        final Iterator<String> itr=permissions.iterator();

        final Runnable[] Next=new Runnable[1];
        final Runnable next=new Runnable() {
            @Override
            public void run() {
                if(!itr.hasNext()){
                    run.run();
                    return;
                }

                String item=itr.next();
                Handle.CheckResult res=handleRequests.get(item);

                assert res != null;//形式主义
                __execHandleGrant_item(baseCount, res, item, Next[0]);
            }
        };

        Next[0]=next;
        next.run();
    }
    private void __execHandleGrant_item(int baseCount, Handle.CheckResult res, final String permission, final Runnable run){
        ArrayList<String> permissions=new ArrayList<>();
        permissions.add(permission);

        boolean notEmpty=baseCount==0&&res.IsFinalReject;

        __execTipsWrap(false, notEmpty, baseCount + (res.IsFinalReject?1:0), permissions, res.IsFinalReject,
        new Runnable() {
            @Override
            public void run() {
                Permission.Item pItem=Permission.QueryItem(permission);
                if(pItem==null || pItem.Handle==null){//形式主义
                    //不存在？有问题的权限强制清除掉
                    _looperPermissions.remove(permission);
                    run.run();
                    return;
                }

                pItem.Handle.Request(UsesPermission.this, permission, new Handle.RequestCallback() {
                    @Override
                    public void onResult() {
                    run.run();
                    }
                });
            }
        }, new Runnable() {
            @Override
            public void run() {
                run.run();
            }
        });
    }


    /**
     * 处理被永久拒绝了的权限，跳到app设置界面去
     */
    private void __execFinalGrant(int baseCount, final ArrayList<String> permissions, final Runnable run){
        if(permissions.size()==0){
            run.run();
            return;
        }
        __execTipsWrap(false, false, baseCount+1, permissions, true
        , new Runnable() {
            @Override
            public void run() {
                Settings.OpenSettings(ThisActivity, null, new Settings.OpenSettingsCallback(){
                    @Override
                    public void onResult() {
                        run.run();
                    }
                });
            }
        }, new Runnable() {
            @Override
            public void run() {
                run.run();
            }
        });
    }


    /**
     * 普通的权限申请方式
     */
    private void __execNormalGrant(final int baseCount, final ArrayList<String> permissions, final ArrayList<String> finalGrant, final Runnable run){
        if(permissions.size()==0){
            run.run();
            return;
        }
        __execTipsWrap(false, false, baseCount, permissions, false
        , new Runnable() {
            @Override
            public void run() {
                //******发起默认的授权请求******
                final long startTime=System.currentTimeMillis();
                Request.RequestPermissions(ThisActivity, permissions, new Request.RequestPermissionsCallBack(){
                    @Override
                    public void onResult(String[] a1, int[] a2) {
                        _normalIsCall=true;//所有权限至少请求了一次,可以明确查询到永久禁止的了

                        ArrayList<String> noGrant=new ArrayList<>();
                        for(int i=0; i<a1.length;i++){
                            String item=a1[i];
                            if(a2[i]!=PackageManager.PERMISSION_GRANTED){
                                noGrant.add(item);
                                if(!ActivityCompat.shouldShowRequestPermissionRationale(ThisActivity, item)){
                                    finalGrant.add(item);
                                }
                            }
                        }

                        //回调太快了，用户来不及操作。没权限的还是没权限，没有一点权限变动，代表全部是系统返回的==永久拒绝的。关键在于他们的数量相等，这种情况如果不处理，可能前面已经弹框了，什么都没干又立即弹框，会很怪异
                        if(noGrant.size()==permissions.size() && System.currentTimeMillis() - startTime < 600){
                            //后边finalGrant没有了，如果finalGrant里面有别的应该放到后面一起处理
                            if(noGrant.size()==finalGrant.size()) {
                                //直接转成final来处理，正常情况下此时应该是第一次调用
                                finalGrant.removeAll(noGrant);
                                if (prevTipsIsShow) {
                                    currentTipsNotShow = true;
                                }
                                __execFinalGrant(baseCount, noGrant, run);
                                return;
                            }
                        }

                        run.run();
                    }
                });
            }
        }, new Runnable() {
            @Override
            public void run() {
                run.run();
            }
        });
    }









    private void __callback(@NonNull ArrayList<String> rejectFinalPermissions,  @NonNull ArrayList<String> rejectPermissions){
        ArrayList<String> resolves=new ArrayList<>(_validPermissions);
        resolves.addAll(_lowerPermissions);

        if(rejectPermissions.size()==0 && _invalidPermissions.size()==0){
            onTrue(_lowerPermissions);
        }else{
            onFalse(rejectFinalPermissions, rejectPermissions, _invalidPermissions);

            for(String item:rejectPermissions){
                resolves.remove(item);
            }
        }

        onComplete(resolves,_lowerPermissions,rejectFinalPermissions, rejectPermissions, _invalidPermissions);
    }
}
