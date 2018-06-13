package cn.sf.excel.utils;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

@Slf4j
public class ExcelUtils {

	// 设置cell编码解决中文高位字节截断
	private static short XLS_ENCODING = HSSFCell.ENCODING_UTF_16;
	// 定制浮点数格式
	private static String NUMBER_FORMAT = " #,##0.00 ";

	///////////////////////////////////////////////////////////
	//逻辑操作
	///////////////////////////////////////////////////////////
	public static boolean isRowEmpty(@NonNull Row row) {
		Iterator<Cell> cellIter = row.cellIterator();
		boolean isRowEmpty = true;
		while (cellIter.hasNext()) {
			Cell cell = cellIter.next();
			String value = getValueOfCell(cell);
			if(!"".equals(value)){
				isRowEmpty = false;
				break;
			}
		}
		return isRowEmpty;
	}
	public static String getValueOfCell(@NonNull Cell cell) {
		String value;
		if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
			value = NumberToTextConverter.toText(cell.getNumericCellValue());
		} else {
			value = cell.toString();
		}
		return value.trim();
	}
	///////////////////////////////////////////////////////////
	//workbook操作
	///////////////////////////////////////////////////////////
	public static Workbook createWorkBook(@NonNull String filePath) {
		Workbook workbook;
		try {
			workbook = WorkbookFactory.create(new FileInputStream(new File(filePath)));
		} catch (Exception e) {
			throw new RuntimeException("请上传xlsx文件!",e);
		}
		return workbook;
	}
	public static Workbook createWorkBook(@NonNull InputStream inputStream) {
		Workbook workbook;
		try {
			workbook = WorkbookFactory.create(inputStream);
		} catch (Exception e) {
			throw new RuntimeException("xlsx文件流有误!",e);
		}
		return workbook;
	}
	///////////////////////////////////////////////////////////
	//cell操作
	///////////////////////////////////////////////////////////
	public static void setCell(@NonNull Row row,@NonNull int index, String value, CellStyle style) {
		Cell cell = row.createCell(index);
		cell.setCellType(Cell.CELL_TYPE_STRING);
		cell.setCellType(XLS_ENCODING);
		if(style!=null) {
			cell.setCellStyle(style);
		}
		cell.setCellValue(value);
	}
	//设置单元格填充值
	public static void setCell(@NonNull Row row,@NonNull int index, String value) {
		Cell cell = row.createCell(index);
		cell.setCellType(Cell.CELL_TYPE_STRING);
		cell.setCellType(XLS_ENCODING);
		cell.setCellValue(value);
	}
	//设置单元格填充值
	public static void setCell(@NonNull Row row, @NonNull int index, Integer value) {
		Cell cell = row.createCell(index);
		cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		cell.setCellValue(value);
	}
	//设置单元格填充值
	public static void setCell(@NonNull Row row, @NonNull int index, Long value) {
		Cell cell = row.createCell(index);
		cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		cell.setCellValue(value);
	}
	//设置单元格填充值
	public static void setCell(@NonNull Workbook workbook, @NonNull  Row row,@NonNull int index, Double value) {
		Cell cell = row.createCell(index);
		cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		cell.setCellValue(value);
		CellStyle cellStyle = workbook.createCellStyle(); // 建立新的cell样式
		DataFormat format = workbook.createDataFormat();
		cellStyle.setDataFormat(format.getFormat(NUMBER_FORMAT)); // 设置cell样式为定制的浮点数格式
		cell.setCellStyle(cellStyle); // 设置该cell浮点数的显示格式
	}


}
