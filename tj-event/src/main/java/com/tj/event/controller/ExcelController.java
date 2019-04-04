package com.tj.event.controller;

import com.tj.event.service.ExcelService;
import com.tj.util.Results;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/excel")
@Api(tags = "excel", description = "Excel导入赛事")
public class ExcelController {

    public final ExcelService excelService;

    @Autowired
    public ExcelController(ExcelService excelService) {
        this.excelService = excelService;
    }


    @ApiOperation(value = "从Excel手动导入赛事信息")

    @ApiImplicitParam(name = "file", value = "导入文件全路径",required = true, dataType = "string", paramType = "header")
    @RequestMapping(value = "/import/scoreInfo", method = RequestMethod.POST)
    public Results.Result importScoreInfoFromExcel(@RequestParam("file") MultipartFile file) {
        return excelService.importScoreInfoFromExcel(file);
    }

}
