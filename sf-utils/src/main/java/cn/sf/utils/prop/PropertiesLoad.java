package cn.sf.utils.prop;

import cn.sf.utils.prop.excps.PropertyException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

public class PropertiesLoad {

    private Properties properties;

    public PropertiesLoad(String propertiesPath){
        try {
            properties = PropertiesLoaderUtils.loadAllProperties(propertiesPath);
        } catch (IOException e) {
            String message = "PropertiesLoad load "+ propertiesPath  +" failed.";
            throw PropertyException.valueOf(message,e);
        }
    }

    public String getByKey(String key, Boolean isVaildNull){
        if(properties==null){
            String message = "properties is null. please execute init first.";
            throw PropertyException.valueOf(message);
        }
        String ret = properties.getProperty(key);
        if(StringUtils.isEmpty(ret)){
            ret = null;
        }
        if(isVaildNull&&ret==null){
            String message = key + " is null in properties. ";
            throw PropertyException.valueOf(message);
        }
        return ret;
    }

}