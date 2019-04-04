package com.tj.util.easypoi;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * created by lh 2018-11-05
 */
public class ExcelUtil {

    //

    /**
     * Excel模板导出
     *
     * @param templateUrl 模板地址
     * @param targetUrl
     * @param map
     */
    public static void ExcelTemlateExport(String templateUrl, String targetUrl, Map map) throws Exception {
        TemplateExportParams params = new TemplateExportParams(templateUrl);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        LocalDateTime time = LocalDateTime.now();
        String name = time.toString() + ".xls";
        String path = "";
        if (null != targetUrl) {
            Integer lastIndex = targetUrl.lastIndexOf("/");
            name = targetUrl.substring(lastIndex, targetUrl.length());
            path = targetUrl.substring(0, lastIndex);
        }
        File saveFile = new File(path);
        if (!saveFile.exists()) {
            saveFile.mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(targetUrl);
        workbook.write(fos);
        fos.close();
    }
}
