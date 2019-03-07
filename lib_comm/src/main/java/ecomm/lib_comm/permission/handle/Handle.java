package ecomm.lib_comm.permission.handle;

import ecomm.lib_comm.permission.UsesPermission;

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
        Resolve(false, true, true, false),
        /**
         * 授权被拒绝，后续请求授权时采用默认的授权设置方式
         */
        RejectDefault(false, false, true, false),
        /**
         * 授权被永久拒绝，后续请求授权时采用默认的授权设置方式
         */
        FinalRejectDefault(true, false, true, false),
        /**
         * 授权被拒绝，后续请求授权时采用权限自己的单独处理方式
         */
        RejectHandle(false,false,false,true),
        /**
         * 授权被永久拒绝，后续请求授权时采用权限自己的单独处理方式
         */
        FinalRejectHandle(true,false,false,true);


        public final boolean IsFinalReject;
        public final boolean CheckIsResolve;
        public final boolean RequestIsDefault;
        public final boolean RequestIsHandle;
        CheckResult(boolean isFinalReject, boolean checkIsResolve, boolean requestIsDefault, boolean requestIsHandle){
            IsFinalReject=isFinalReject;
            CheckIsResolve=checkIsResolve;
            RequestIsDefault=requestIsDefault;
            RequestIsHandle=requestIsHandle;
        }
    }
}
