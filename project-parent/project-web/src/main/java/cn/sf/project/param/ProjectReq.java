package cn.sf.project.param;

import cn.sf.compiler.rap.RapField;
import cn.sf.project.converts.EnumKeySerializer;
import cn.sf.project.converts.Long2StringConverter;
import cn.sf.project.enums.ProjectType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

/**
 * Created by nijianfeng on 17/6/24.
 */
@Data
public class ProjectReq {

    @RapField(fieldMeans = "项目id")
    @JsonSerialize(converter = Long2StringConverter.class)
    private Long id;
    private String projectName;
    @JsonSerialize(using=EnumKeySerializer.class)
    private ProjectType projectType;

    public Integer getProjectTypeKey(){
        if(projectType==null){
            return null;
        }
        return projectType.getKey();
    }
    public String getProjectTypeDesc(){
        if(projectType==null){
            return null;
        }
        return projectType.getDesc();
    }
    public void setProjectTypeKey(int key){
        this.projectType = ProjectType.NULL.genEnumByKey(key);
    }
}
