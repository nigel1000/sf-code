package cn.sf.utils.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class Json {

    public static<T> String of (T o, String defaultValue) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.warn("Write object to JSON string failed: {}", e);
        }
        return defaultValue;
    }

    public static<T> byte[] of (T o) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsBytes(o);
        } catch (JsonProcessingException e) {
            log.warn("Write object to JSON bytes failed: {}", e);
        }
        return new byte[0];
    }

    public static<T> T from (String value, Class<T> clazz) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(value, clazz);
        } catch (IOException e) {
            log.warn("Read object from string failed: {}/{}/{}", value, clazz, e);
            return null;
        }
    }

    public static<T> T from (byte[] bytes, Class<T> clazz) {
         return from(new ByteArrayInputStream(bytes), clazz);
    }

    public static<T> T from (InputStream is, Class<T> clazz) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(is, clazz);
        } catch (IOException e) {
            log.warn("Read object from bytes failed: {}/{}", clazz, e);
            return null;
        }
    }

    public static StringBuffer e (StringBuffer buf, String key, String value) {
        buf.append("\"").append(key).append("\":");
        if (null == value)
            return buf.append("null");
        else
            return buf.append('\"').append(value).append('\"');
    }

    public static StringBuffer e (StringBuffer buf, String key, Object value) {
        buf.append("\"").append(key).append("\":");
        if (null == value)
            return buf.append("null");
        else
            return buf.append(value.toString());
    }

}