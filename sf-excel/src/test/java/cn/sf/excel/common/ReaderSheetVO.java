package cn.sf.excel.common;


import cn.sf.excel.ExcelField;
import cn.sf.excel.enums.ExcelBool;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by hznijianfeng on 2017/3/22.
 */
@Data
public class ReaderSheetVO  implements Serializable {
    private static final long serialVersionUID = 2673802046675941279L;

    @ExcelField(cellIndex = 0, title = "属性1",required = false, desc = "字符串")
    private String attr1;
    @ExcelField(cellIndex = 1, title = "属性2",required = false, desc = "Integer")
    private Integer attr2;
    @ExcelField(cellIndex = 2, title = "属性3",required = false, desc = "Long")
    private Long attr3;
    @ExcelField(cellIndex = 3, title = "属性4",required = false, desc = "BigDecimal")
    private BigDecimal attr4;
    @ExcelField(cellIndex = 4, title = "属性5",required = false, desc = "范围类型")
    private ExcelBool bool;
    @ExcelField(cellIndex = 5, dateParse = "yyyy-MM-dd HH:mm:ss",title = "属性6",required = false, desc = "日期类型")
    private Date date;
}
