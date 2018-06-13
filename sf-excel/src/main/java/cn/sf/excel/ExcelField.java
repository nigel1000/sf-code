package cn.sf.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelField {

    // 必填约束
    boolean required() default true;

    // excel的cellIndex
    int cellIndex() default 0;

    // excel的title
    String title() default "";

    // 单元格的字符串的最大长度或者数值的最大值
    // String,Integer,Long,BigDecimal
    String max() default "-1";

    // excel的date约束
    String dateParse() default "yyyy-MM-dd HH:mm:ss";

    // excel字段描述
    String desc() default "";
}
