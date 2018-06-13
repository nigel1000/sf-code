package cn.sf.bean.excps;

import lombok.Data;
import lombok.NonNull;

import java.util.Map;

@Data
public class KnowException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 自定义errorCode,可根据这个errorCode做异常的筛选、特殊处理
     * 默认是服务异常
     */
    private int errorCode = SysErrorCode.SERVICE_ERROR.getIntValue();

    /**
     * 异常错误信息
     */
    private String errorMessage = SysErrorCode.SERVICE_ERROR.getDesc();

    /**
     * 异常上下文，可以设置一些关键业务参数
     */
    private Map<String, Object> context;

    public static KnowException valueOf(String message){
        return new KnowException(message);
    }

    public static KnowException valueOf(String message,Throwable throwable){
        return new KnowException(message,throwable);
    }

    public KnowException(String message) {
        super(message);
        this.errorMessage = message;
    }

    public KnowException(String message, Throwable cause) {
        super(message,cause);
        this.errorMessage = message;
    }

    public KnowException(int errorCode, String errorMessage, Throwable cause) {
        super(errorMessage,cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public KnowException(@NonNull SysErrorCode errorCode, Throwable cause) {
        super(errorCode.toString(),cause);
        this.errorCode = errorCode.getIntValue();
        this.errorMessage = errorCode.getDesc();
    }

    public KnowException(@NonNull SysErrorCode errorCode, Map<String, Object> context, Throwable cause) {
        super(errorCode.toString(),cause);
        this.errorCode = errorCode.getIntValue();
        this.errorMessage = errorCode.getDesc();
        this.context = context;
    }

    public KnowException(int errorCode, String errorMessage, Map<String, Object> context, Throwable cause) {
        super(errorMessage,cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.context = context;
    }


}
