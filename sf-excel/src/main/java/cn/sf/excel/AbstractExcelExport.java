package cn.sf.excel;

import cn.sf.excel.utils.ExcelUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by nijianfeng on 17/3/23.
 */
@Slf4j
public abstract class AbstractExcelExport {

    @Getter
    @Setter
    protected String exportFilePath;
    @Getter
    protected String templateFilePath;
    @Getter
    protected Workbook workbook;
    @Getter
    protected Sheet sheet;
    @Getter
    protected Row row;

    ///////////////////////////////////////////////////////////
    //行操作
    ///////////////////////////////////////////////////////////
    //根据行号增加一行
    public void createRow(int index) {
        this.row = this.sheet.createRow(index);
    }
    //删除行
    public void deleteRow(Row row) {
        if (this.sheet == null)
            throw new RuntimeException("sheet is null!");
        this.sheet.removeRow(row);
    }
    //根据行号删除一行
    public void deleteRow(int index) {
        if (this.sheet == null)
            throw new RuntimeException("sheet is null!");
        deleteRow(sheet.getRow(index));
    }
    //根据name获取sheet第一行
    public Row getFirstRowBySheetName(String name) {
        Sheet sheet = this.workbook.getSheet(name);
        if (sheet != null)
            return sheet.getRow(0);
        return null;
    }
    //设置行高
    public void setRowHight(float height) {
        if (this.row != null)
            this.row.setHeightInPoints(height);
    }

    ///////////////////////////////////////////////////////////
    //sheet操作
    ///////////////////////////////////////////////////////////
    //添加一张sheet
    public void addSheet(String sheetName) {
        if (this.workbook == null) {
            this.workbook = new HSSFWorkbook();
        }
        Sheet tmpSheet = workbook.getSheet(sheetName);
        if (tmpSheet == null)
            this.sheet = workbook.createSheet(sheetName);
        else {
            this.sheet = tmpSheet;
        }
    }
    //根据name修改当前操作的sheet
    public boolean changeSheet(String name) {
        Sheet tmpSheet = workbook.getSheet(name);
        if (tmpSheet == null)
            return false;
        else {
            this.sheet = tmpSheet;
            return true;
        }
    }
    //根据index修改当前操作的sheet
    public boolean changeSheet(int index) {
        Sheet tmpSheet = workbook.getSheetAt(index);
        if (tmpSheet == null)
            return false;
        else {
            this.sheet = tmpSheet;
            return true;
        }
    }

    ///////////////////////////////////////////////////////////
    //导出操作
    ///////////////////////////////////////////////////////////
    //导出Excel文件
    public void exportXLS() {
        if(exportFilePath ==null||workbook==null){
            return;
        }
        Path path = Paths.get(exportFilePath);
        if(!path.toFile().getParentFile().exists()){
            path.toFile().getParentFile().mkdirs();
        }
        try {
            FileOutputStream fOut = new FileOutputStream(exportFilePath);
            workbook.write(fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
    //workbook转换成InputStream
    public InputStream getInputStreamFromWorkbook() {
        if(workbook==null){
            return null;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            workbook.write(os);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        byte[] content = os.toByteArray();
        return new ByteArrayInputStream(content);
    }
    //workbook转换成OutputStream
    public byte[] getOutputStreamFromWorkbook() {
        if(workbook==null){
            return null;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            workbook.write(os);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return os.toByteArray();
    }

    ///////////////////////////////////////////////////////////
    //data数据操作
    ///////////////////////////////////////////////////////////
    public void createEXCEL(List data, Class type) {
        // 还未创建sheet返回
        if (this.workbook == null || this.sheet == null) {
            return;
        }
        //组装数据
        Field[] fields = type.getDeclaredFields();
        int rowOffset = 0;
        this.createRow(rowOffset++);
        //设置标题列
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            ExcelExportField excelAnnotation = field.getAnnotation(ExcelExportField.class);
            if (excelAnnotation == null)
                continue;
            int cellIndex = excelAnnotation.cellIndex();
            String title = excelAnnotation.title();
            ExcelUtils.setCell(this.row, cellIndex, title);
        }
        //遍历list
        listDatas(data, type, fields, rowOffset);
    }
    public void createEXCEL(List<?> data, Class type,int startRowIndex) {
        // 还未创建sheet返回
        if (this.workbook == null || this.sheet == null) {
            return;
        }
        //组装数据
        Field[] fields = type.getDeclaredFields();
        //遍历list
        listDatas(data, type, fields, startRowIndex);
    }
    private void listDatas(List data, Class type, Field[] fields, int rowOffset) {
        if (data != null && data.size() > 0) {
            for (Object obj : data) {
                this.createRow(rowOffset++);
                for (int i = 0; i < fields.length; i++) {
                    Field field = fields[i];
                    ExcelExportField excelAnnotation = field.getAnnotation(ExcelExportField.class);
                    if (excelAnnotation == null)
                        continue;
                    int cellIndex = excelAnnotation.cellIndex();
                    // 利用反射赋值
                    StringBuilder sb = new StringBuilder();
                    String fieldName = field.getName();
                    sb.append("get");
                    sb.append(fieldName.substring(0, 1).toUpperCase());
                    sb.append(fieldName.substring(1));
                    try {
                        Method getMethod = type.getMethod(sb.toString());
                        Object result = getMethod.invoke(obj);
                        if (result != null) {
                            String val = result.toString();
                            if (val != null && !"".equals(val)) {
                                if (field.getType() == Date.class) {
                                    ExcelUtils.setCell(this.row, cellIndex, new SimpleDateFormat(excelAnnotation.dateFormat()).format(result));
                                }else if (field.getType() == String.class) {
                                    ExcelUtils.setCell(this.row, cellIndex, (String) result);
                                }else if (field.getType() == Long.class) {
                                    ExcelUtils.setCell(this.row, cellIndex, (Long) result);
                                }else if (field.getType() == Double.class) {
                                    ExcelUtils.setCell(this.workbook, this.row, cellIndex, (Double) result);
                                }else if (field.getType() == Integer.class) {
                                    ExcelUtils.setCell(this.row,cellIndex, (Integer) result);
                                }else {
                                    ExcelUtils.setCell(this.row, cellIndex, val);
                                }
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("给excel设值失败.",e);
                    }

                }
            }
        }
    }

}
