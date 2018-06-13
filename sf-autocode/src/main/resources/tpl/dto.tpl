package ${dtoPackage};

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

//${tableComment}
@Data
@NoArgsConstructor
public class ${className} implements Serializable {
    private static final long serialVersionUID = 1L;

<#list classVos as vo>
    private ${vo.type} ${vo.name};//${vo.memo}
</#list>

}