package com.tj.event.service.impl;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.tj.event.domain.ExcelRace;
import com.tj.event.domain.Student;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StudentTest {

    @Test
    public void saveStudent() {
        try {
            List<Student> students = new ArrayList<>();
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            params.setNeedVerfiy(true);  //校验

            File file = new File("D:\\0-Resouse\\TestStudent.xls");
            students = ExcelImportUtil.importExcel(file, ExcelRace.class, params);

            System.out.println(students.size());
            for (Student s : students) {
                System.out.println(s.getName() + "\t" + s.getAge());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}

