package cn.sf.project.controller.redis.out;

import cn.sf.project.converts.Long2StringConverter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class CacheInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String key;
	private String url;
	private int count;
	private int expireTime;
	@JsonSerialize(converter = Long2StringConverter.class)
	private Long ttl;
	private List<FieldInfo> fieldInfos = new ArrayList<FieldInfo>();

	public void addFieldInfo(String field, String url, String objJson) {
		FieldInfo fieldInfo = new FieldInfo();
		fieldInfo.setField(field);
		fieldInfo.setUrl(url);
		fieldInfo.setObjJson(objJson);
		fieldInfos.add(fieldInfo);
	}

}

