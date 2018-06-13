package cn.sf.excel;

import cn.sf.excel.base.BaseEnum;
import cn.sf.excel.excp.ExcelParseExceptionInfo;
import cn.sf.excel.excp.ExcelParseException;
import cn.sf.excel.utils.ExcelUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class ExcelParse<T> {

    public List<T> getList(Workbook book, String sheetName,Integer startRow, Class<T> targetClass) throws ExcelParseException {
        if(book==null||sheetName==null||"".equals(sheetName)) return Lists.newArrayList();
        Sheet sheet = book.getSheet(sheetName);
        if(sheet==null) return Lists.newArrayList();
        List<T> retList = Lists.newArrayList();
        boolean isExcelExp = false;
        List<ExcelParseExceptionInfo> infoList = Lists.newArrayList();
        Iterator<Row> iter = sheet.rowIterator();
        for (int i = 0; i < startRow; i++) {
            iter.next();
        }
        while (iter.hasNext()) {
            Row row = iter.next();
            boolean isEmpty = ExcelUtils.isRowEmpty(row);
            if (!isEmpty) {
                try {
                    retList.add(getObject(row, targetClass,sheetName));
                }catch (ExcelParseException e){
                    isExcelExp=true;
                    infoList.addAll(e.getInfoList());
                }
            }
        }
        if (isExcelExp) {
            throw new ExcelParseException(infoList);
        }
        return retList;
    }

    public T getObject(Row row, Class<T> targetClass, String sheetName) throws ExcelParseException {
        Object target;
        boolean isExcelExp = false;
        List<ExcelParseExceptionInfo> infoList = Lists.newArrayList();
        try {
            target = targetClass.newInstance();
            Field[] fields = targetClass.getDeclaredFields();
            int rowNum = row.getRowNum() + 1;
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                // 只注入有注解的属性
                ExcelField excelAnnotation = field.getAnnotation(ExcelField.class);
                if (excelAnnotation == null)
                    continue;
                String title = excelAnnotation.title();
                int cellIndex = excelAnnotation.cellIndex();
                Object value = null;
                // 获取数据并转换类型
                if (row.getLastCellNum() >= cellIndex) {
                    Cell cell = row.getCell(cellIndex);
                    try {
                        value = getFieldValue(cell, field.getType());
                    } catch (Exception e) {
                        isExcelExp = true;
                        ExcelParseExceptionInfo expInfo = new ExcelParseExceptionInfo(sheetName, rowNum, title, "录入数据和定义属性类型有误");
                        infoList.add(expInfo);
                        continue;
                    }
                }
                // 检验是否必须
                boolean required = excelAnnotation.required();
                if (required && (value == null || (value instanceof String && "".equals(value)))) {
                    isExcelExp = true;
                    ExcelParseExceptionInfo expInfo = new ExcelParseExceptionInfo(sheetName, rowNum, title, "定义属性不能为空而录入数据为空");
                    infoList.add(expInfo);
                    continue;
                }
                // 属性类型是枚举的校验
                if (value != null && value instanceof BaseEnum) {
                    int valueTemp = ((BaseEnum) value).getValue();
                    String descTemp = ((BaseEnum) value).getDesc();
                    if ("NULL".equals(descTemp) && valueTemp == -1) {
                        isExcelExp = true;
                        ExcelParseExceptionInfo expInfo = new ExcelParseExceptionInfo(sheetName, rowNum, title, "录入数据不在枚举指定范围内");
                        infoList.add(expInfo);
                        continue;
                    }
                }
                // 校验单元格的字符串的最大长度或者数值的最大值
                if (value != null) {
					String format = excelAnnotation.dateParse();
					if (field.getType() == Date.class) {
						try {
							value = new SimpleDateFormat(format).parse((String)value);
						} catch (Exception e) {
							isExcelExp = true;
							ExcelParseExceptionInfo expInfo = new ExcelParseExceptionInfo(sheetName, rowNum, title, "日期格式需要满足" + format);
							infoList.add(expInfo);
							continue;
						}
					}

					String max = excelAnnotation.max();
                    if (!"-1".equals(max)) {
                        if (field.getType() == BigDecimal.class) {
                            BigDecimal maxValue = new BigDecimal(max);
                            if (((BigDecimal) value).compareTo(maxValue) > 0) {
                                isExcelExp = true;
                                ExcelParseExceptionInfo expInfo = new ExcelParseExceptionInfo(sheetName, rowNum, title, "数值必须小于" + max);
                                infoList.add(expInfo);
                                continue;
                            }
                        }
                        if (field.getType() == Long.class) {
                            Long maxValue = Long.valueOf(max);
                            if ((((Long) value) - maxValue) > 0) {
                                isExcelExp = true;
                                ExcelParseExceptionInfo expInfo = new ExcelParseExceptionInfo(sheetName, rowNum, title, "数值必须小于" + max);
                                infoList.add(expInfo);
                                continue;
                            }
                        }
                        if (field.getType() == Integer.class) {
                            Integer maxValue = Integer.valueOf(max);
                            if ((((Integer) value) - maxValue) > 0) {
                                isExcelExp = true;
                                ExcelParseExceptionInfo expInfo = new ExcelParseExceptionInfo(sheetName, rowNum, title, "数值必须小于" + max);
                                infoList.add(expInfo);
                                continue;
                            }
                        }
                        if (field.getType() == String.class) {
                            if (((String) value).length() > Integer.valueOf(max)) {
                                isExcelExp = true;
                                ExcelParseExceptionInfo expInfo = new ExcelParseExceptionInfo(sheetName, rowNum, title, "文字必须在" + max + "字以内");
                                infoList.add(expInfo);
                                continue;
                            }
                        }
                    }
                }
                // 利用反射赋值
                StringBuilder sb = new StringBuilder();
                String fieldName = field.getName();
                sb.append("set");
                sb.append(fieldName.substring(0, 1).toUpperCase());
                sb.append(fieldName.substring(1));
                Method setMethod = targetClass.getMethod(sb.toString(), field.getType());
                try {
                    setMethod.invoke(target, value);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (isExcelExp) {
            throw new ExcelParseException(infoList);
        }
        return (T) target;
    }

    private static Object getFieldValue(Cell cell, Class fieldTypeClass) {
        if (cell == null)
            return null;
        String strVal = ExcelUtils.getValueOfCell(cell);
        if (strVal == null || "".equals(strVal)) {
            return null;
        }
        Object value;
        if (BaseEnum.class.isAssignableFrom(fieldTypeClass)) {
            // CASE1: ExcelEnum值
            value = ((BaseEnum) fieldTypeClass.getEnumConstants()[0]).getEnumByDesc(strVal);
        } else if (fieldTypeClass == BigDecimal.class) {
            // CASE2: BigDecimal
            value = new BigDecimal(strVal);
        } else if (fieldTypeClass == Long.class || fieldTypeClass == long.class) {
            // CASE3: LONG
            value = Long.valueOf(strVal);
        } else if (fieldTypeClass == Integer.class || fieldTypeClass == int.class) {
            // CASE4: INT
            value = Integer.valueOf(strVal);
        } else {
            // CASE7: DEFAULT
            value = strVal;
        }
        return value;
    }

}
