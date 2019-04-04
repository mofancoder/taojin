package com.tj.event.service;

import com.tj.util.Results;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

public interface ExcelService {

    /**
     * 从 Excel 手动导入赛事信息
     * @return
     */
    Results.Result importScoreInfoFromExcel(MultipartFile file);


    Results.Result importScoreInfoFromExcel2(File file);
}
