package cn.sf.excel;

import cn.sf.excel.utils.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;

@Slf4j
public class XLSExport extends AbstractExcelExport{

    public XLSExport(String exportFilePath) {
        this.exportFilePath = exportFilePath;
        this.workbook = new HSSFWorkbook();
    }

    public XLSExport(String exportFilePath,String templateFilePath) {
        this.exportFilePath = exportFilePath;
        this.templateFilePath = templateFilePath;
        this.workbook = ExcelUtils.createWorkBook(templateFilePath);
    }

    public XLSExport(String exportFilePath,InputStream inputStream) {
        this.workbook = ExcelUtils.createWorkBook(inputStream);
        this.exportFilePath = exportFilePath;
    }

    public XLSExport(InputStream inputStream) {
        this.workbook = ExcelUtils.createWorkBook(inputStream);
    }

}
