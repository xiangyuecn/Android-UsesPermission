package ecomm.lib_comm.permission;

import java.util.ArrayList;

public final class Permission extends ecomm.lib_comm.permission.com_hjq_permissions.Permission {

    /**
     * 获取权限名称列表"权限名1,权限名2,权限名3"
     */
    static public String QueryNames(ArrayList<String> permissions){
        StringBuilder names=new StringBuilder();
        for (String n : permissions) {
            names.append(Permission.QueryName(n)).append(",");
        }
        if(names.length()>0){
            names.setLength(names.length()-1);
        }
        return names.toString();
    }
}