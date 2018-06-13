package cn.sf.mybatis.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ToString(callSuper = true)
@ConfigurationProperties(
    prefix = "multiMybatis",
    ignoreInvalidFields = true
)
public class MybatisProperties extends MybatisProperty {

    public Map<String,MybatisProperty> mybatisMap = null;
}
