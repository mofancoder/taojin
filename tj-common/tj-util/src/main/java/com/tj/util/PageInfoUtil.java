package com.tj.util;

import com.github.pagehelper.PageInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by ldh on 2018-02-26.
 */
public class PageInfoUtil {

    public static <T1, T2> void copy(PageInfo<T1> from, PageInfo<T2> to) {
        Field[] var2 = from.getClass().getDeclaredFields();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            Field field = var2[var4];
            String fieldName = field.getName();
            if ("list".equals(fieldName)) {
                continue;
            }
            if (!Modifier.isFinal(field.getModifiers())) {
                field.setAccessible(true);

                try {
                    field.set(to, field.get(from));
                } catch (Exception var7) {
                    throw new RuntimeException("Failed to copy PageInfo state: " + var7.getMessage(), var7);
                }
            }
        }

    }

    @Deprecated
    public static <T1, T2> void copy2(PageInfo<T1> from, PageInfo<T2> to) {
        to.setSize(from.getSize());
        to.setTotal(from.getTotal());
        to.setEndRow(from.getEndRow());
        to.setFirstPage(from.getFirstPage());
        //to.setHasNextPage();
        //to.setHasPreviousPage();
        //to.setIsFirstPage();
        //to.setIsLastPage();
        to.setNavigatepageNums(from.getNavigatepageNums());
        to.setNavigatePages(from.getNavigatePages());
        to.setNextPage(from.getNextPage());
        to.setPageNum(from.getPageNum());
        to.setPages(from.getPages());
        to.setPageSize(from.getPageSize());
        to.setPrePage(from.getPrePage());
        to.setStartRow(from.getStartRow());
        to.setLastPage(from.getLastPage());
    }
}
