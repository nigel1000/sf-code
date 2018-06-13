package cn.sf.auto.mybatis.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Created by nijianfeng on 18/1/29.
 */
@Data
@Builder
public class Table {

    private String name;
    private String comment;
    private List<Field> fields;

}
