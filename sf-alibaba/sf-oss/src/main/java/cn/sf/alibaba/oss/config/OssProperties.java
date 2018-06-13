package cn.sf.alibaba.oss.config;

import com.google.common.collect.Maps;
import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by nijianfeng on 17/7/2.
 */
@Component
@ConfigurationProperties(
        prefix = "alibaba.oss",
        ignoreInvalidFields = true
)
@Data
@ToString(callSuper = true)
public class OssProperties {

    private Map<String,OssProperty> accessDataMap = Maps.newHashMap();

}
