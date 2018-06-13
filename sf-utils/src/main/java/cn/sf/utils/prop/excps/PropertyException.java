package cn.sf.utils.prop.excps;

public class PropertyException extends RuntimeException {

    private PropertyException() {
    }

    private PropertyException(String message) {
        super(message);
    }

    private PropertyException(String message, Throwable throwable) {
        super(message,throwable);
    }

    public static PropertyException valueOf(String message){
        return new PropertyException(message);
    }

    public static PropertyException valueOf(String message, Throwable throwable){
        return new PropertyException(message,throwable);
    }

}
