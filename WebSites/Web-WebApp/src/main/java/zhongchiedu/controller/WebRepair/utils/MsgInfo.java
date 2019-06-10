package zhongchiedu.controller.WebRepair.utils;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MsgInfo extends BaseRowModel {
    @ExcelProperty(value = "报修地址" ,index = 0)
    private String schoolname;
    
    @ExcelProperty(value = "维修分类" ,index = 1)
    private String repairclass;
    
    @ExcelProperty(value = "报修内容" ,index = 2)
    private String content;
    
    @ExcelProperty(value = "联系人" ,index = 3)
    private String teachername;
    
    @ExcelProperty(value = "联系电话" ,index = 4)
    private String teachertel;

    @ExcelProperty(value = "报修时间" ,index = 5)
    private String createTime;

    @ExcelProperty(value = "维修人" ,index = 6)
    private String repairmanname;
    
    @ExcelProperty(value = "维修时间" ,index = 7)
    private String repairtime;
    
    @ExcelProperty(value = "如何处理" ,index = 8)
    private String note;
    
    @ExcelProperty(value = "维修情况" ,index = 9)
    private String status;

    @ExcelProperty(value = "时间间隔" ,index = 10)
    private String timelag;
    	
}