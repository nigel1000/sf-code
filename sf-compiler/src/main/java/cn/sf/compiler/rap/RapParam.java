package cn.sf.compiler.rap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created by nijianfeng on 18/4/30.
 */
@Target(ElementType.PARAMETER)
public @interface RapParam {

    String paramMeans();

    String paramMockValue() default "";

}
