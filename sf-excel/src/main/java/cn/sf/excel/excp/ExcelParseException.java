package cn.sf.excel.excp;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ExcelParseException extends Exception {

	private List<ExcelParseExceptionInfo> infoList;

	public ExcelParseException(List<ExcelParseExceptionInfo> infoList) {
		super();
		this.infoList = infoList;
	}

}
