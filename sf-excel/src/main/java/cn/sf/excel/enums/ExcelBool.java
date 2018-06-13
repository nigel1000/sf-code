package cn.sf.excel.enums;

import cn.sf.excel.base.BaseEnum;

public enum ExcelBool implements BaseEnum<ExcelBool> {

	/**
	 * NULL
	 */
	NULL(-1, "NULL"),

	FALSE(0, "否"),
	TRUE(1, "是"),
	;

	/**
	 * 值
	 */
	private final int value;
	/**
	 * 描述
	 */
	private final String desc;

	/**
	 * 构造函数
	 * 
	 * @param v
	 * @param d
	 */
	ExcelBool(int v, String d) {
		value = v;
		desc = d;
	}

	@Override
	public ExcelBool getEnumByDesc(String descValue) {
		for (ExcelBool type : ExcelBool.values()) {
			if (type.desc.equals(descValue))
				return type;
		}
		return NULL;
	}

	@Override
	public String getDesc() {
		return desc;
	}

	@Override
	public int getValue() {
		return value;
	}

}