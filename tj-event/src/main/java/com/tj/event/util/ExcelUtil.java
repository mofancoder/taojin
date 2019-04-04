package com.tj.event.util;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Excel导入导出
 *
 */
public class ExcelUtil {

    private String fileNameString;
    /*
     * @methodName:fileToList
     * @Description(内部方法用来，将输入文件流转化为list)
     * @ParameterNames: [fs, isExcel2003, sheetName]
     * @return: List<String[]>
     * @auther: Songrongkai
     * @date: 2018/5/18 15:06
     */
    private List<String[]> fileToList(FileInputStream fs, boolean isExcel2003, String sheetName) throws IOException {
        Workbook workbook = null;
        if (isExcel2003) {
            workbook = new HSSFWorkbook(fs);
        } else {
            workbook = new XSSFWorkbook(fs);
        }
        // 在Excel文档中，第一张工作表的缺省索引是0
        // 其语句为：HSSFSheet sheet = wookbook.getSheetAt(0);
        Sheet sheet = workbook.getSheet(sheetName);
        // 获取到Excel文件中的所有行数
        int rows = sheet.getPhysicalNumberOfRows();
        // 获取到Excel文件中的标题的列
        Row rowTitle = sheet.getRow(0);
        int cellsTitle = rowTitle.getPhysicalNumberOfCells();
        // 遍历行
        List<String[]> list_excel = new ArrayList<String[]>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        int dmCells = Integer.MAX_VALUE;

        for (int i = 0; i <= rows; i++) {
            // 读取左上端单元格
            Row row = sheet.getRow(i);
            // 行不为空
            if (row != null) {
                String value = "";
                // 遍历列
                for (int j = 0; j < cellsTitle; j++) {
                    // 获取到列的值
                    Cell cell = row.getCell(j);
                    if (cell != null) {
                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_BLANK:
                                value += "" + ",";
                                break;
                            case Cell.CELL_TYPE_BOOLEAN:
                                value += cell.getBooleanCellValue() ? "TURE" : "FALSE" + ",";
                                break;
                            case Cell.CELL_TYPE_ERROR:
                                value += ErrorEval.getText(cell.getErrorCellValue()) + ",";
                                break;
                            case Cell.CELL_TYPE_FORMULA:
                                value += cell.getCellFormula() + ",";
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
                                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                    value += simpleDateFormat.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue())) + ",";
                                } else {
                                    value += cell.getNumericCellValue() + ",";
                                }
                                break;
                            case Cell.CELL_TYPE_STRING:
                                value += cell.getStringCellValue() + ",";
                                break;
                            default:
                                value += "0" + ",";
                                break;
                        }
                    } else {
                        value += "" + ",";
                    }
                }
                String[] val = value.split(",");

                if (val.length != 0 && dmCells == val.length || dmCells == Integer.MAX_VALUE) {
                    list_excel.add(val);
                } else {
                    break;
                }
                dmCells = val.length;
            }
        }
        return list_excel;
    }

    /**
     * filePath转换为list
     * @param filePath：Excel文件完整目录
     * @param sheetName：Excel工作表名称，一般默认的为Sheet1
     * @return List<String[]>,list中存储行，String[]中为该行每一列的值
     * @throws Exception
     */
    public List<String[]> convertExcelToList(String filePath, String sheetName) throws Exception {
        File file =new File(filePath);
        return convertExcelToList(file,sheetName);
    }
    /**
     * file转换为list
     * @param file：Excel文件
     * @param sheetName：Excel工作表名称，一般默认的为Sheet1
     * @return List<String[]>,list中存储行，String[]中为该行每一列的值
     * @throws Exception
     */
    public List<String[]> convertExcelToList(File file, String sheetName) throws Exception {
        // 创建对Excel工作簿文件的引用
        fileNameString=file.getName();
        if (fileNameString.endsWith("xls") ||fileNameString.endsWith("xlsx")) {
            boolean isExcel2003 = fileNameString.endsWith("xls") ? true : false;
            FileInputStream fs = new FileInputStream(file);
            return fileToList(fs, isExcel2003, sheetName);
        } else {
            return null;
        }
    }

    /**
     *
     * <p>Title: exportProduct</p>
     * <p>Description:将list导出EXCEL到templatePath地址 </p>
     * @param products
     * @param templatePath
     * @param fileName
     */
    public void exportProduct(List<String> products, String templatePath, String fileName) {
        InputStream is = null;
        OutputStream out = null;

        System.out.println("OUTPUT = "+ fileName);
        try {
            out = new FileOutputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            File file = new File(templatePath);
            is = new FileInputStream(file);
            Workbook workbook = WorkbookFactory.create(is);

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 0; i < products.size(); i++) {
//                ProductExportTuple productExportTuple = products.get(i);
                Row row = sheet.createRow(i + 1);
                Cell cellName = row.createCell(0);
                cellName.setCellValue("1");

                Cell cellChineseName = row.createCell(1);
                cellChineseName.setCellValue("2");

                Cell cellQuantity = row.createCell(2);
                cellQuantity.setCellValue("3");
            }
            workbook.write(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.apache.poi.openxml4j.exceptions.InvalidFormatException e) {
            e.printStackTrace();
        }
    }


}
