package ecomm.lib_comm.permission.handle;

import ecomm.lib_comm.permission.UsesPermission;

/**
 *  github: https://github.com/xiangyuecn/Android-UsesPermission
 */
public abstract class Handle {
    public interface RequestCallback{
        void onResult();
    }

    /**
     * 检查是否有权限，并且同时决定下一步申请授权如何操作
     */
    public abstract CheckResult Check(UsesPermission obj, String permission);

    /**
     * 进行权限申请，如果被永久拒绝的权限，应该走提示设置，然后转到设置
     */
    public abstract void Request(UsesPermission obj, String permission, RequestCallback callback);



    public enum CheckResult {
        /**
         * 已授权
         */
        Resolve(true, false),
        /**
         * 授权被拒绝
         */
        Reject(false, false),
        /**
         * 授权被永久拒绝
         */
        FinalReject(false, true);


        public final boolean IsFinalReject;
        public final boolean IsResolve;
        CheckResult(boolean isResolve, boolean isFinalReject){
            IsResolve=isResolve;
            IsFinalReject=isFinalReject;
        }
    }
}
