package cn.sf.mybatis.base;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by nijianfeng on 17/9/21.
 */
@Data
public class BaseDomain implements Serializable {

    private String orderBy;
    private Integer offset;
    private Integer limit;

}
