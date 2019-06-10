package zhongchiedu.controller.WebRepair.utils;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class ExportInfo extends BaseRowModel {
    @ExcelProperty(value = "姓名" ,index = 0)
    private String name;

    @ExcelProperty(value = "年龄",index = 1)
    private String age;

    @ExcelProperty(value = "邮箱",index = 2)
    private String email;

    @ExcelProperty(value = "地址",index = 3)
    private String address;


}