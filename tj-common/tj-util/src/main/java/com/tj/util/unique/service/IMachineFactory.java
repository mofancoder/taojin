package com.tj.util.unique.service;

/**
 * 创建时间：2017/3/1
 * 创建人： by LeWis
 */
public interface IMachineFactory {

    Long machineId(long maxVal);

    void destroy();

    void init();
}
