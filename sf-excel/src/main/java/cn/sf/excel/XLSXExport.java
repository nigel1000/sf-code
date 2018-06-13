package cn.sf.excel;

import cn.sf.excel.utils.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;

@Slf4j
public class XLSXExport extends AbstractExcelExport{

	public XLSXExport(String exportFilePath) {
		this.exportFilePath = exportFilePath;
		this.workbook = new XSSFWorkbook();
	}

	public XLSXExport(String exportFilePath,String templateFilePath) {
		this.exportFilePath = exportFilePath;
		this.templateFilePath = templateFilePath;
		this.workbook = ExcelUtils.createWorkBook(templateFilePath);
	}

	public XLSXExport(String exportFilePath,InputStream inputStream) {
		this.workbook = ExcelUtils.createWorkBook(inputStream);
		this.exportFilePath = exportFilePath;
	}

	public XLSXExport(InputStream inputStream) {
		this.workbook = ExcelUtils.createWorkBook(inputStream);
	}

}