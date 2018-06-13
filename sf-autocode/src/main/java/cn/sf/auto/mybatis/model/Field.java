package cn.sf.auto.mybatis.model;

import lombok.Data;
import lombok.ToString;

/**
 * Created by nijianfeng on 18/1/29.
 */
@Data
@ToString
public class Field {

    private String field;//column名称
    private String type;//数据库类型
    private String memo;//注释
    private String numericLength;
    private String numericScale;
    private String isNullable;//是否可为空
    private Boolean isAutoIncrement;
    private String isDefault;//默认值
    private String characterLength;

}
