package cn.sf.project.controller.redis.out;

import lombok.Data;

import java.io.Serializable;

@Data
public class FieldInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String field;
	private String url;
	private String objJson;

}