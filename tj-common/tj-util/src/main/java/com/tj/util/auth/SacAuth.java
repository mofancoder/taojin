package com.tj.util.auth;

/**
 * 超级节点权限认证
 */
public @interface SacAuth {
    /**
     * 需要的角色
     * 使用 or/and 分割
     *
     * @return
     */
    String hasRole();

    String hasAuth();
}
