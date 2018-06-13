package cn.sf.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelExportField {

	// excel的cellIndex
	int cellIndex() default 0;

	// excel的title
	String title() default "";

	// excel的date约束
	String dateFormat() default "yyyy-MM-dd HH:mm:ss";

	// excel字段描述
	String desc() default "";
}
