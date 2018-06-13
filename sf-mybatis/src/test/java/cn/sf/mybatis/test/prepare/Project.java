package cn.sf.mybatis.test.prepare;

import cn.sf.mybatis.base.BaseDomain;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by nijianfeng on 17/6/10.
 */
@Data
@NoArgsConstructor
public class Project extends BaseDomain implements Serializable{

    private Long id;//
    private String projectName;//项目名称
    private String projectNameLike;//项目名称
    private Integer projectType;//项目类型

}
